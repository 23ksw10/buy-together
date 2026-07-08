package com.together.buytogether.productcomment.domain;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberFixture;
import com.together.buytogether.product.domain.Product;
import com.together.buytogether.product.domain.ProductFixture;

public class ProductCommentFixture {
    private Member member = MemberFixture.aMember().build();
    private Product product = ProductFixture.aProduct().build();
    private Long commentId = 1L;
    private String content = "content";


    public static ProductCommentFixture aProductComment() {
        return new ProductCommentFixture();
    }

    public ProductCommentFixture member(Member member) {
        this.member = member;
        return this;
    }

    public ProductCommentFixture product(Product product) {
        this.product = product;
        return this;
    }

    public ProductCommentFixture commentId(Long commentId) {
        this.commentId = commentId;
        return this;
    }

    public ProductCommentFixture content(String content) {
        this.content = content;
        return this;
    }


    public ProductComment build() {
        return new ProductComment(
                member,
                product,
                content
        );
    }
}