package com.together.buytogether.common;

import com.together.buytogether.member.feature.api.RegisterMemberApi;

public class Scenario {
    public static RegisterMemberApi registerMember() {
        return new RegisterMemberApi();
    }
}
