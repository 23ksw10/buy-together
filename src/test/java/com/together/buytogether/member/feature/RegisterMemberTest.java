package com.together.buytogether.member.feature;

import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.member.domain.SEX;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegisterMemberTest {
    @LocalServerPort
    private int port;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        if (RestAssured.UNDEFINED_PORT == RestAssured.port) {
            RestAssured.port = port;
        }
    }

    @Test
    @DisplayName("회원가입")
    void registerMember() {
        String address = "경기도 고양시 덕양구 화정로 27";
        String detailAddress = "625동 1004호";
        RegisterMember.Request request = new RegisterMember.Request(
                "name",
                "loginId",
                "password",
                "phoneNumber",
                SEX.MALE,
                address, //도로명 주소
                detailAddress    //상세 주소
        );
        //when

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/members")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());

        //then
        assertThat(memberRepository.findAll().size()).isEqualTo(1);
    }

}
