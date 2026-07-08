package com.together.buytogether.productcomment.repository;

import com.together.buytogether.config.JpaAuditingConfig;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.product.domain.Product;
import com.together.buytogether.product.domain.ProductRepository;
import com.together.buytogether.productcomment.domain.ProductComment;
import com.together.buytogether.productcomment.domain.ProductCommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static com.together.buytogether.member.domain.MemberFixture.aMember;
import static com.together.buytogether.product.domain.ProductFixture.aProduct;
import static com.together.buytogether.productcomment.domain.ProductCommentFixture.aProductComment;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProductComment JPA 연결 테스트")
@DataJpaTest
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductCommentRepositoryTest {
    @Autowired
    ProductCommentRepository productCommentRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ProductRepository productRepository;

    Member savedMember;
    Product product;
    Product savedProduct;
    ProductComment productComment;

    @BeforeEach
    void setUp() {
        savedMember = memberRepository.save(aMember().build());
        product = aProduct().member(savedMember).build();
        savedProduct = productRepository.save(product);
        productComment = aProductComment().member(savedMember).product(savedProduct).build();
    }

    @Test
    @DisplayName("insert 테스트")
    void insertProductComment() {
        productCommentRepository.save(productComment);
        assertThat(productCommentRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("select 테스트")
    void selectProductComment() {
        productCommentRepository.save(productComment);
        List<ProductComment> productComments = productCommentRepository.findAll();
        assertThat(productComments).isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("update 테스트")
    void updateProductComment() {

        ProductComment savedProductComment = productCommentRepository.save(productComment);
        savedProductComment.update("newContent", LocalDateTime.now());
        ProductComment updateProductComment = productCommentRepository.saveAndFlush(savedProductComment);

        assertThat(updateProductComment).hasFieldOrPropertyWithValue("content", "newContent");
    }

    @Test
    @DisplayName("delete 테스트")
    void deleteProductComment() {

        ProductComment savedProductComment = productCommentRepository.save(productComment);
        productCommentRepository.delete(savedProductComment);
        assertThat(productCommentRepository.count()).isEqualTo(0);
    }


}
