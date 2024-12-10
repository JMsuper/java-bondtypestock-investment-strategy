package com.finance.adam.repository.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateEmailDTO {

    @NotBlank
    private String id;

    @NotBlank
    @Email(message = "이메일 형식이 아닙니다.")
    private String email;
}