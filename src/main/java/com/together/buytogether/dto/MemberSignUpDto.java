package com.together.buytogether.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSignUpDto {
    @NotBlank(message = "이름이 입력되지 않았습니다.")
    @Size(max = 20)
    private String name;

    @NotBlank(message = "로그인 아이디는 필수 값입니다")
    @Size(max = 20)
    private String loginId;

    @NotBlank(message = "비밀번호는 필수 값입니다")
    @Size(max = 20)
    private String password;

    @NotBlank(message = "주소가 입력되지 않았습니다.")
    @Size(max = 30)
    private String address;

    @NotBlank(message = "전화번호가 입력되지 않았습니다.")
    @Size(max = 30)
    private String phoneNumber;

    @NotBlank(message = "성별이 입력되지 않았습니다.")
    @Size(max = 1)
    private String sex;

}
