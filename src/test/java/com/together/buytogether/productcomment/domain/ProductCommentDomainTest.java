package com.together.buytogether.productcomment.domain;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.product.domain.ProductFixture;
import com.together.buytogether.product.domain.ProductStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProductCommentDomainTest {
    private String updateCommentContent;

    @BeforeEach
    void setUp() {
        updateCommentContent = "Updated Content";
    }

    @Test
    @DisplayName("댓글 수정")
    void updateComment() {
        ProductComment productComment = ProductCommentFixture.aProductComment().build();
        LocalDateTime updateDate = LocalDateTime.now();
        productComment.update(updateCommentContent, updateDate);
        assertThat(productComment.getContent()).isEqualTo(updateCommentContent);
        assertThat(productComment.getUpdatedAt()).isEqualTo(updateDate);
    }

    @Test
    @DisplayName("댓글 수정 - 상품 상태가 CLOSED 경우 예외가 발생한다")
    void throwExceptionWhenUpdateCommentOnClosedProduct() {
        ProductFixture productFixture = ProductFixture.aProduct().status(ProductStatus.CLOSED);
        ProductComment productComment = ProductCommentFixture.aProductComment().product(productFixture.build()).build();
        LocalDateTime updateDate = LocalDateTime.now();
        assertThatThrownBy(() -> {
            productComment.update(updateCommentContent, updateDate);
        })
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 마감된 상품입니다");
    }
}
