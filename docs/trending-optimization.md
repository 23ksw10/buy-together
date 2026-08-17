# 인기 공동구매 조회 최적화 여정 (`GET /group-buys/trending`)

동일한 기능을 5단계로 점진 최적화하며 각 단계를 라이브 엔드포인트로 보존한 기록.
v1~v5 본문의 측정치는 초기 소규모 시드(상품 5,000 / 참여 20,000 / 좋아요 30,000,
시간 윈도우 도입 전, 상위 10개 조회 당시)의 실측값이다.

## 요구사항

- 인기 상품 상위 100개 조회 (OPEN 상품만)
- **트렌딩 = 최근 7일 활동 기준** — 참여는 `created_at`, 좋아요는 `updated_at`이 윈도우 안에 있어야 집계
- 인기 점수 = **참여 수 × 2 + 좋아요 수** (참여가 좋아요보다 강한 구매 의사 신호)
- 좋아요는 행 삭제 없이 상태 토글 방식이므로 `like_status = 'OPEN'`인 행만 집계
- 랭킹 특성상 30초 수준의 staleness 허용 → 이벤트 기반 무효화 대신 TTL 만료 채택

## 단계별 엔드포인트

| 단계 | 엔드포인트 | 전략 | 콜드/핫 응답 시간(실측) |
|---|---|---|---|
| v1 | `/group-buys/trending/v1` | 단일 컬럼 `product_id` 인덱스 베이스라인 | 인덱스 적용 후 재측정 필요 |
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

### 초기 무인덱스 실험 (현재 v1 아님) — EXPLAIN

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

원인: 당시 `enroll.product_id`는 `@ForeignKey(NO_CONSTRAINT)`로 FK 제약이 없어 **인덱스 자체가 존재하지 않았다.**
`product_like.product_id`는 FK 인덱스가 있었지만 `like_status` 필터는 클러스터드 인덱스 룩업이 추가로 필요했다.

이 플랜은 최악의 경우를 확인하는 데는 유용하지만, 일반적인 관계형 스키마의 베이스라인으로는 지나치게 비현실적이다.
현재 v1은 이 풀스캔 플랜을 사용하지 않는다.

### 현재 v1: PK/FK 스타일 기본 인덱스

현재 v1은 `Enroll`의 Product FK 제약을 복원하고, 두 자식 테이블에 안정적인 이름의 단일 컬럼 인덱스를 명시한다:

```sql
CREATE INDEX idx_enroll_product_id ON enroll (product_id);
CREATE INDEX idx_product_like_product_id ON product_like (product_id);
```

v1 쿼리는 v2의 커버링 인덱스만 `IGNORE INDEX`로 제외한다. 따라서 MySQL은
`product_id` 단일 컬럼 인덱스로 상품별 자식 행을 찾은 뒤 `created_at`, `like_status`, `updated_at`을
추가 필터링한다. 예상 실행 계획은 두 상관 서브쿼리 모두 `type=ref`,
`key=idx_*_product_id`이며 커버링되지 않은 조건은 테이블 행을 조회해 평가한다.

### v2: 쿼리 전용 커버링 인덱스 — EXPLAIN

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

### v1 플랜 고정 (현실적인 비교 장치)

인덱스는 DB 전역에 적용되므로 v2 도입 후 힌트가 없으면 v1도 커버링 인덱스를 선택한다.
나란히 비교를 유지하기 위해 v1은 `IGNORE INDEX`로 v2 복합 인덱스만 제외한다.
기본 PK/FK 스타일 인덱스는 그대로 사용하므로, "무인덱스 대 인덱스"가 아니라
"일반 관계 인덱스 대 쿼리 전용 커버링 인덱스"를 비교한다.

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

## 시간 윈도우 도입: 전체 누적 → 최근 7일

초기 쿼리는 **전 기간 누적** 참여/좋아요를 집계했다. 이는 "역대 인기 상품"이지 "트렌딩"이 아니다:
오래 열려 있던 상품일수록 카운트가 계속 쌓여 신규 인기 상품이 순위에 진입할 수 없고,
랭킹이 날마다 거의 변하지 않는다. 그래서 두 신호 모두에 **최근 7일 윈도우**를 적용했다
(`TrendingProductService.TRENDING_WINDOW_DAYS`, 캐시 키는 `top100` 고정이라 cutoff는 미스 시점에만 평가).

### 좋아요 recency의 함정: created_at이 아니라 updated_at

`enroll`은 이벤트 행이라 `created_at` 필터로 충분하다. 그러나 `product_like`는
(member, product)당 한 행을 OPEN/CLOSED로 **토글**하는 상태 행이고 타임스탬프 자체가 없었다.
JPA Auditing(`@CreatedDate`/`@LastModifiedDate`)으로 `created_at`/`updated_at`을 추가하고
윈도우 필터는 `updated_at`을 사용한다:

- 좋아요 행은 최초 좋아요 때 한 번만 생성되므로 `created_at`만으로 윈도우를 걸면
  "좋아요 취소 후 재좋아요"가 영원히 집계에서 빠진다 (created_at은 불변)
- `updated_at >= cutoff AND like_status = 'OPEN'` = "윈도우 안에서 활성화된 좋아요".
  취소 토글도 updated_at을 갱신하지만 상태가 CLOSED라 집계에서 자연히 제외된다
- 대안이었던 실삭제 모델(취소 = DELETE, 재좋아요 = INSERT)이면 created_at으로 충분하지만,
  동작 중인 토글 기반 서비스 로직 전면 수정 + 좋아요 이력 유실이라 기각

### 인덱스 진화: 윈도우 컬럼까지 커버링 유지

윈도우 조건이 추가되면 v1의 단일 컬럼 인덱스로는 범위 필터 시 클러스터드 인덱스 룩업이 생긴다.
v2에서는 커버링을 유지하도록 별도의 복합 인덱스를 함께 둔다:

```sql
-- v1 기본 인덱스                              v2 커버링 인덱스
idx_enroll_product_id (product_id)            idx_enroll_product_id_created_at (product_id, created_at)
idx_product_like_product_id (product_id)       idx_product_like_product_id_like_status_updated_at
                                                  (product_id, like_status, updated_at)
```

동등 조건(product_id, like_status) 뒤에 범위 조건(created_at/updated_at) 컬럼을 두는
표준 복합 인덱스 설계다. 두 인덱스의 접두가 같아 운영 관점에서는 중복이지만,
v1/v2 실행 계획을 같은 스키마에서 재현하기 위해 의도적으로 함께 유지한다.

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

# 3. 단계별 응답 시간 (v1은 먼저 1회 실행 후 반복 횟수를 결정)
time curl -s localhost:8080/group-buys/trending/v2 > /dev/null   # v3, v4, v5 동일하게 2회씩

# 4. 캐시 키 확인
docker exec local-redis redis-cli --scan --pattern '*trending*'

# 5. 스탬피드 비교 (콜드 키에 동시 50요청 후 로그에서 쿼리 실행 횟수 비교)
docker exec local-redis redis-cli DEL 'trending_singleflight::top100'
seq 1 50 | xargs -P 50 -I{} curl -s localhost:8080/group-buys/trending/v5 > /dev/null
docker exec local-redis redis-cli DEL 'trending_redis::top100'
seq 1 50 | xargs -P 50 -I{} curl -s localhost:8080/group-buys/trending/v3 > /dev/null

# 6. 벤치마크 테스트 (시드 데이터 필요, CI 게이트 아님)
./gradlew test --tests TrendingBenchmarkTest
```
