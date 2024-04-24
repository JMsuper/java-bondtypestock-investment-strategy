package com.finance.adam.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserUpdatePasswordDTO {

    @NotBlank
    private String id;

    @NotBlank
    @Size(min = 6, max = 10, message = "비밀번호는 6자 이상 20자 이하로 입력해주세요.")
    private String password;
}