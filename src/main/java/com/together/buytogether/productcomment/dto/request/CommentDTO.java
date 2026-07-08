package com.together.buytogether.productcomment.dto.request;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.product.domain.Product;
import com.together.buytogether.productcomment.domain.ProductComment;
import jakarta.validation.constraints.NotBlank;

public record CommentDTO(
        @NotBlank(message = "댓글 내용은 필수입니다")
        String content
) {
        public ProductComment toDomain(Member member, Product product) {
                return new ProductComment(
                        member,
                        product,
                        content
                );
        }
}
