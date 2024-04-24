package com.finance.adam.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserUpdateEmailDTO {

    @NotBlank
    private String id;

    @NotBlank
    @Email(message = "이메일 형식이 아닙니다.")
    private String email;
}