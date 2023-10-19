package com.together.buytogether.common;

import com.together.buytogether.enroll.feature.api.CancelBuyingApi;
import com.together.buytogether.enroll.feature.api.JoinBuyingApi;
import com.together.buytogether.member.feature.api.RegisterMemberApi;
import com.together.buytogether.member.feature.api.SignInMemberApi;
import com.together.buytogether.member.feature.api.SignOutMemberApi;
import com.together.buytogether.post.feature.api.DeletePostApi;
import com.together.buytogether.post.feature.api.RegisterPostApi;
import com.together.buytogether.post.feature.api.UpdatePostApi;
import com.together.buytogether.postcomment.feature.api.DeleteCommentApi;
import com.together.buytogether.postcomment.feature.api.RegisterCommentApi;
import com.together.buytogether.postcomment.feature.api.UpdateCommentApi;

public class Scenario {
    public static RegisterMemberApi registerMember() {
        return new RegisterMemberApi();
    }

    public SignInMemberApi signInMember() {
        return new SignInMemberApi();
    }

    public SignOutMemberApi signOutMember() {
        return new SignOutMemberApi();
    }

    public RegisterPostApi registerPost() {
        return new RegisterPostApi();
    }

    public UpdatePostApi updatePost() {
        return new UpdatePostApi();
    }

    public DeletePostApi deletePost() {
        return new DeletePostApi();
    }

    public RegisterCommentApi registerComment() {
        return new RegisterCommentApi();
    }

    public UpdateCommentApi updateComment() {
        return new UpdateCommentApi();
    }

    public DeleteCommentApi deleteComment() {
        return new DeleteCommentApi();
    }

    public JoinBuyingApi joinBuying() {
        return new JoinBuyingApi();
    }

    public CancelBuyingApi cancelBuying() {
        return new CancelBuyingApi();
    }
}
