package com.finance.adam.repository.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// jackson 은 직렬화를 수행할 때 기본생성자를 사용함.
@Data
public class UserUpdatePasswordDTO {

    @NotBlank
    @Size(min = 6, max = 10, message = "비밀번호는 6자 이상 20자 이하로 입력해주세요.")
    private String password;
}