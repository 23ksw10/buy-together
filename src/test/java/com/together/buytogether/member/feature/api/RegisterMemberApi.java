package com.together.buytogether.member.feature.api;

import com.together.buytogether.common.Scenario;
import com.together.buytogether.member.domain.SEX;
import com.together.buytogether.member.dto.request.RegisterMemberDTO;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.springframework.http.HttpStatus;

public class RegisterMemberApi {
    private String name = "name";
    private String loginId = "loginId";
    private String password = "password";
    private String phoneNumber = "phoneNumber";
    private SEX sex = SEX.MALE;
    private String address = "경기도 고양시 덕양구 화정로 27";
    private String detailAddress = "625동 1004호";

    public RegisterMemberApi name(String name) {
        this.name = name;
        return this;
    }

    public RegisterMemberApi loginId(String loginId) {
        this.loginId = loginId;
        return this;
    }

    public RegisterMemberApi password(String password) {
        this.password = password;
        return this;
    }

    public RegisterMemberApi phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public RegisterMemberApi sex(SEX sex) {
        this.sex = sex;
        return this;
    }

    public RegisterMemberApi address(String address) {
        this.address = address;
        return this;
    }

    public RegisterMemberApi detailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
        return this;
    }

    public Scenario request() {
        RegisterMemberDTO request = new RegisterMemberDTO(
                name,
                loginId,
                password,
                phoneNumber,
                sex,
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
        return new Scenario();
    }
}
