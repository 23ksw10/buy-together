package com.together.buytogether.member.feature;

import com.together.buytogether.common.ApiTest;
import com.together.buytogether.common.Scenario;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class SignInMemberTest extends ApiTest {

    @Autowired
    private MemberRepository memberRepository;


    @Test
    @DisplayName("로그인 성공")
    void signUpMember() {
        //given
        Scenario.registerMember().request();
        SignInMember.Request request = new SignInMember.Request(
                "loginId",
                "password");

        //when
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/members/sign-in")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        Member member = memberRepository.findByLoginId("loginId").stream()
                .filter(m -> m.getPassword().equals("password"))
                .findFirst()
                .orElseThrow(null);
        assertThat(member.getPassword()).isEqualTo("password");

    }

}
