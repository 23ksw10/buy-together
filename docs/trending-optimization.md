# 인기 공동구매 조회 최적화 여정 (`GET /group-buys/trending`)

동일한 기능을 5단계로 점진 최적화하며 각 단계를 라이브 엔드포인트로 보존한 기록.
모든 측정치는 local-seed 시드 데이터(상품 5,000 / 참여 20,000 / 좋아요 30,000) 기준 실측값이다.

## 요구사항

- 인기 상품 상위 10개 조회 (OPEN 상품만)
- 인기 점수 = **참여 수 × 2 + 좋아요 수** (참여가 좋아요보다 강한 구매 의사 신호)
- 좋아요는 행 삭제 없이 상태 토글 방식이므로 `like_status = 'OPEN'`인 행만 집계
- 랭킹 특성상 30초 수준의 staleness 허용 → 이벤트 기반 무효화 대신 TTL 만료 채택

## 단계별 엔드포인트

| 단계 | 엔드포인트 | 전략 | 콜드/핫 응답 시간(실측) |
|---|---|---|---|
| v1 | `/group-buys/trending/v1` | 집계 쿼리 직행 (인덱스 미사용 플랜 고정) | 매 요청 ~60초 |
| v2 | `/group-buys/trending/v2` | 동일 쿼리 + 인덱스 | 매 요청 ~50ms |
| v3 | `/group-buys/trending/v3` | Redis 단일 캐시 (TTL 30초) | 미스 ~60ms / 히트 ~7ms |
| v4 | `/group-buys/trending/v4` | Caffeine(L1) + Redis(L2) 2계층 | 미스 ~66ms / L1 히트 ~4ms |
| v5 | `/group-buys/trending/v5`, 기본 경로 | 싱글플라이트 + stale-while-revalidate | 미스 ~70ms / L1 히트 ~7ms / L2 히트 ~22ms |

## v1 → v2: EXPLAIN 분석과 인덱스

### 쿼리 형태

상관 스칼라 서브쿼리 2개로 상품별 참여/좋아요 수를 집계한다 (`TrendingProductRepository` 참고).
QueryDSL 대신 네이티브 SQL을 쓴 이유:

- JPQL/QueryDSL은 파생 테이블 조인·인덱스 힌트를 표현할 수 없다
- `leftJoin + groupBy + countDistinct` 방식은 참여×좋아요 행이 곱으로 팽창하는 잘못된 형태
- EXPLAIN으로 실행 계획을 정직하게 분석하려면 실행되는 SQL 그대로가 필요하다

### 인덱스 도입 전 (v1) — EXPLAIN

```
+----+--------------------+------------+------+---------------+------+-------+----------+----------------+
| id | select_type        | table      | type | possible_keys | key  | rows  | filtered | Extra          |
+----+--------------------+------------+------+---------------+------+-------+----------+----------------+
|  1 | PRIMARY            | <derived2> | ALL  | NULL          | NULL |  1940 |   100.00 | Using filesort |
|  2 | DERIVED            | p          | ALL  | NULL          | NULL |  5821 |    33.33 | Using where    |
|  4 | DEPENDENT SUBQUERY | pl         | ALL  | NULL          | NULL | 30349 |     5.00 | Using where    |
|  3 | DEPENDENT SUBQUERY | e          | ALL  | NULL          | NULL | 20066 |    10.00 | Using where    |
+----+--------------------+------------+------+---------------+------+-------+----------+----------------+
```

OPEN 상품 5,042행 각각에 대해 `enroll` 20,000행과 `product_like` 30,000행을 풀스캔한다.
`EXPLAIN ANALYZE` 실측: **63,280ms** (상품당 enroll 3.99ms + like 8.51ms × 5,042회).

원인: `enroll.product_id`는 `@ForeignKey(NO_CONSTRAINT)`로 FK 제약이 없어 **인덱스 자체가 존재하지 않았다.**
`product_like.product_id`는 FK 인덱스가 있었지만 `like_status` 필터는 클러스터드 인덱스 룩업이 추가로 필요했다.

### 추가한 인덱스

```sql
CREATE INDEX idx_enroll_product_id ON enroll (product_id);
CREATE INDEX idx_product_like_product_id_like_status ON product_like (product_id, like_status);
```

엔티티 `@Table(indexes = ...)`로 선언했고 `ddl-auto: update`가 기존 테이블에 생성함을 `SHOW INDEX`로 확인했다.
(부수 효과: MySQL이 `product_like`의 FK 백킹 인덱스로 새 복합 인덱스를 재사용한다.)

### 인덱스 도입 후 (v2) — EXPLAIN

```
+----+--------------------+------------+------+-----------------------------------------+------+----------+--------------------------+
| id | select_type        | table      | type | key                                     | rows | filtered | Extra                    |
+----+--------------------+------------+------+-----------------------------------------+------+----------+--------------------------+
|  1 | PRIMARY            | <derived2> | ALL  | NULL                                    | 1940 |   100.00 | Using filesort           |
|  2 | DERIVED            | p          | ALL  | NULL                                    | 5821 |    33.33 | Using where              |
|  4 | DEPENDENT SUBQUERY | pl         | ref  | idx_product_like_product_id_like_status |    6 |   100.00 | Using where; Using index |
|  3 | DEPENDENT SUBQUERY | e          | ref  | idx_enroll_product_id                   |    4 |   100.00 | Using index              |
+----+--------------------+------------+------+-----------------------------------------+------+----------+--------------------------+
```

두 서브쿼리 모두 `type=ref` + `Using index`(커버링)로 상품당 4~6행만 읽는다.
`EXPLAIN ANALYZE` 실측: **53ms** — 인덱스 2개로 약 **1,200배** 개선.

기각한 대안 (면접 토킹 포인트):

- `product.status` 인덱스 — 90% 이상이 OPEN이라 선택도가 없어 옵티마이저가 무시
- `ORDER BY score` 인덱스 — 계산식이라 인덱스 불가, 5,042행 filesort는 충분히 저렴
- 좋아요/참여 수 반정규화 컬럼 — 쓰기 경로가 바뀌는 별도 과제라 범위 제외

### v1 플랜 고정 (데모 장치)

인덱스는 DB 전역에 적용되므로 v2 도입 후 v1도 빨라져 버린다. 나란히 비교를 유지하기 위해
v1 쿼리에 `USE INDEX ()`(빈 목록 = 보조 인덱스 미사용)를 명시해 인덱스 도입 전 실행 계획을 고정했다.
쿼리는 그 외 v2와 동일 — "같은 쿼리, 접근 경로만 바뀌었다"가 이 단계의 핵심이다.

## v3: Redis 단일 캐시

- `@Cacheable(cacheNames = TRENDING_REDIS, cacheManager = "redisCacheManager")`, TTL 30초
- 히트 시 ~7ms. 그러나 **만료 순간 동시 요청이 전부 DB로 몰린다**:
  키 삭제 직후 동시 50요청 실측 → **DB 쿼리 23회 실행** (캐시 스탬피드)

## v4: Caffeine(L1) + Redis(L2) 2계층 캐시

- `CompositeCacheManager`(기본 CacheManager) 경유, `TRENDING_COMPOSITE` (COMPOSITE, TTL 30초)
- L1 히트 시 Redis 네트워크 왕복 제거(~4ms), L2 히트 시 L1 자동 재적재, 인스턴스 간 무효화는 Redis pub/sub
- 한계 1: `CacheConfig`가 COMPOSITE 캐시에 하나의 TTL을 양 계층에 적용 — 계층별 TTL 분리가 v5의 역할
- 한계 2: `CompositeCache.get(key, valueLoader)`는 L1만 적재하므로 `@Cacheable(sync=true)` 사용 금지
- 스탬피드 문제는 여전히 존재

## v5: 싱글플라이트 + stale-while-revalidate (최종, 기본 경로)

- 기존 `@SingleFlightCacheable` 인프라의 첫 실사용처
- 로컬 5초(멀티 인스턴스 신선도 상한) / Redis 30초(공유 원본), `decisionForUpdate = 80`
  → Redis TTL의 80%(24초) 경과 후 히트 시 락 기반 **비동기 선제 갱신**으로 만료 전에 재적재
- 콜드 키: Redis `SETNX`(6초 락)로 **한 요청만 DB 조회**, 나머지는 지수 백오프로 Redis 폴링
- 동시 50요청 실측: **DB 쿼리 1회** (v3의 23회와 대비)
- 주의: `TRENDING_SINGLE_FLIGHT`는 `CacheName` enum에 등록하지 않는다 —
  싱글플라이트 경로는 `LocalCacheManager`가 어노테이션 TTL로 캐시를 생성하며,
  enum에 등록하면 미리 생성된 캐시의 enum TTL이 어노테이션 TTL을 덮어쓴다

## 구현 중 발견한 함정: Stream.toList()와 Redis 직렬화

`Stream.toList()`가 반환하는 `java.util.ImmutableCollections$ListN`은 **final java.util 타입**이라
`GenericJackson2JsonRedisSerializer`가 리스트 타입 정보를 기록하지 않는다.
직렬화는 성공하지만(겉보기 정상) 캐시 **히트** 시 역직렬화에서 `SerializationException`이 발생해 500을 반환했다.
`Collectors.toList()`(ArrayList)로 수집하면 `["java.util.ArrayList", [...]]` 타입 래퍼가 기록되어 해결된다.
"캐시 미스는 통과하고 히트에서 터지는" 유형이라 히트 경로 검증 없이는 놓치기 쉽다.

## 검증 방법

```bash
# 1. 인프라 + 시드
docker compose up -d
SPRING_PROFILES_ACTIVE=local-seed LOAD_SEED_ENABLED=true LOAD_SEED_RESET=true \
  LOAD_TEST_PASSWORD=local-test-password ./gradlew bootRun

# 2. 인덱스 확인
docker exec local-mysql mysql -uroot bt -e "SHOW INDEX FROM enroll; SHOW INDEX FROM product_like;"

# 3. 단계별 응답 시간 (v1은 ~60초 소요 주의)
time curl -s localhost:8080/group-buys/trending/v2 > /dev/null   # v3, v4, v5 동일하게 2회씩

# 4. 캐시 키 확인
docker exec local-redis redis-cli --scan --pattern '*trending*'

# 5. 스탬피드 비교 (콜드 키에 동시 50요청 후 로그에서 쿼리 실행 횟수 비교)
docker exec local-redis redis-cli DEL 'trending_singleflight::top10'
seq 1 50 | xargs -P 50 -I{} curl -s localhost:8080/group-buys/trending/v5 > /dev/null
docker exec local-redis redis-cli DEL 'trending_redis::top10'
seq 1 50 | xargs -P 50 -I{} curl -s localhost:8080/group-buys/trending/v3 > /dev/null

# 6. 벤치마크 테스트 (시드 데이터 필요, CI 게이트 아님)
./gradlew test --tests TrendingBenchmarkTest
```
