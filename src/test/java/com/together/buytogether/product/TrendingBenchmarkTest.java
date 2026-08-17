package com.together.buytogether.product;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.together.buytogether.product.dto.response.TrendingProductResponseDTO;
import com.together.buytogether.product.service.TrendingProductService;

/**
 * 인기 공동구매 조회 v1~v5 단계별 성능 측정.
 * local-seed 프로필로 시드 데이터를 적재한 로컬 MySQL(3308)/Redis(6381)가 필요하다.
 * CI 게이트가 아닌 데모/측정용 하네스 (EnrollQueryTest와 동일한 성격).
 */
@SpringBootTest
public class TrendingBenchmarkTest {

	int queryCount = 10;

	@Autowired
	private TrendingProductService trendingProductService;

	@Test
	@DisplayName("[쿼리 속도 측정] v1 베이스라인 (인덱스 미사용)")
	public void benchmarkV1() {
		// v1은 호출당 수십 초가 걸리므로 반복 횟수를 줄인다
		runBenchmark("v1", trendingProductService::getTrendingV1, 3);
	}

	@Test
	@DisplayName("[쿼리 속도 측정] v2 인덱스 적용")
	public void benchmarkV2() {
		runBenchmark("v2", trendingProductService::getTrendingV2);
	}

	@Test
	@DisplayName("[쿼리 속도 측정] v3 Redis 캐시")
	public void benchmarkV3() {
		runBenchmark("v3", trendingProductService::getTrendingV3);
	}

	@Test
	@DisplayName("[쿼리 속도 측정] v4 Caffeine+Redis 2계층 캐시")
	public void benchmarkV4() {
		runBenchmark("v4", trendingProductService::getTrendingV4);
	}

	@Test
	@DisplayName("[쿼리 속도 측정] v5 싱글플라이트 캐시")
	public void benchmarkV5() {
		runBenchmark("v5", trendingProductService::getTrendingV5);
	}

	@Test
	@DisplayName("v1과 v2는 동일한 결과를 반환한다")
	public void v1AndV2ReturnSameResult() {
		assertThat(trendingProductService.getTrendingV1())
			.isEqualTo(trendingProductService.getTrendingV2());
	}

	@Test
	@DisplayName("캐시를 거친 두 번째 호출은 첫 호출과 동일한 결과를 반환한다 (Redis JSON 직렬화 검증)")
	public void cachedCallReturnsSameResult() {
		List<TrendingProductResponseDTO> first = trendingProductService.getTrendingV3();
		List<TrendingProductResponseDTO> second = trendingProductService.getTrendingV3();
		assertThat(second).isEqualTo(first);

		List<TrendingProductResponseDTO> firstV5 = trendingProductService.getTrendingV5();
		List<TrendingProductResponseDTO> secondV5 = trendingProductService.getTrendingV5();
		assertThat(secondV5).isEqualTo(firstV5);
	}

	private void runBenchmark(String version, Supplier<List<TrendingProductResponseDTO>> query) {
		runBenchmark(version, query, queryCount);
	}

	private void runBenchmark(String version, Supplier<List<TrendingProductResponseDTO>> query, int iterations) {
		query.get(); // warm-up (캐시 버전은 첫 호출이 캐시 미스)

		long totalTime = 0;
		for (int i = 0; i < iterations; i++) {
			long startTime = System.currentTimeMillis();
			query.get();
			long endTime = System.currentTimeMillis();
			long executionTime = endTime - startTime;
			totalTime += executionTime;
			System.out.println("Trending " + version + " " + (i + 1) + ": Execution time " + executionTime + "ms");
		}
		System.out.println("Trending " + version + " average time " + (totalTime / iterations) + "ms");
	}
}
