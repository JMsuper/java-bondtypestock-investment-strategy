package com.finance.adam.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class UserRegisterDTO {

    @Size(min = 4, max = 10, message = "아이디는 4자 이상 20자 이하로 입력해주세요.")
    private String id;

    @Email(message = "이메일 형식이 아닙니다.")
    private String email;

    @Size(min = 6, max = 10, message = "비밀번호는 6자 이상 20자 이하로 입력해주세요.")
    private String password;
}
