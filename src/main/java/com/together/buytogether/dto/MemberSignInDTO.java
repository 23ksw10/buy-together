package com.together.buytogether.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

@Getter
public class MemberSignInDTO {

    @NotBlank(message = "")
    @Size(max = 20)
    private String loginId;

    @NotBlank(message = "필수값입니다")
    @Size(max = 20)
    private String password;
}
