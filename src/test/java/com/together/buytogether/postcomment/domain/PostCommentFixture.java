package com.together.buytogether.postcomment.domain;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberFixture;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostFixture;

public class PostCommentFixture {
    private Member member = MemberFixture.aMember().build();
    private Post post = PostFixture.aPost().build();
    private Long commentId = 1L;
    private String content = "content";


    public static PostCommentFixture aPostComment() {
        return new PostCommentFixture();
    }

    public PostCommentFixture member(Member member) {
        this.member = member;
        return this;
    }

    public PostCommentFixture post(Post post) {
        this.post = post;
        return this;
    }

    public PostCommentFixture commentId(Long commentId) {
        this.commentId = commentId;
        return this;
    }

    public PostCommentFixture content(String content) {
        this.content = content;
        return this;
    }


    public PostComment build() {
        return new PostComment(
                member,
                post,
                content
        );
    }
}