package com.together.buytogether.common;

import com.together.buytogether.member.feature.api.RegisterMemberApi;
import com.together.buytogether.member.feature.api.SignInMemberApi;
import com.together.buytogether.member.feature.api.SignOutMemberApi;

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
}
