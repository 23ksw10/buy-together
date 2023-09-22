package com.together.buytogether.common;

import com.together.buytogether.member.feature.api.RegisterMemberApi;
import com.together.buytogether.member.feature.api.SignInMemberApi;

public class Scenario {
    public static RegisterMemberApi registerMember() {
        return new RegisterMemberApi();
    }

    public SignInMemberApi signInMember() {
        return new SignInMemberApi();
    }
}
