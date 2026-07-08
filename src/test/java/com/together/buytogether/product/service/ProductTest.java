package com.together.buytogether.product.service;

import static com.together.buytogether.product.domain.ProductFixture.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.product.domain.Product;
import com.together.buytogether.product.domain.ProductLikeRepository;
import com.together.buytogether.product.domain.ProductRepository;

@SpringBootTest
public class ProductTest {
	List<Long> memberIds = new ArrayList<>();
	Long productId;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ProductLikeRepository productLikeRepository;
	@Autowired
	private AsyncLikeService asyncLikeService;
	@Autowired
	private ProductService productService;

	@Test
	void aff() {
		List<Member> memberList = memberRepository.findAll();
		for (int i = 0; i < 10; i++) {
			for (Member m : memberList) {
				Product product = aProduct().member(m).build();
				productRepository.save(product);
			}
		}
	}
}
