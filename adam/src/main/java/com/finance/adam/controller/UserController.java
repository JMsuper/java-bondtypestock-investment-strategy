package com.finance.adam.controller;

import com.finance.adam.auth.dto.AccountDto;
import com.finance.adam.repository.account.dto.UserUpdateEmailDTO;
import com.finance.adam.repository.account.dto.UserUpdatePasswordDTO;
import com.finance.adam.exception.CustomException;
import com.finance.adam.exception.ErrorCode;
import com.finance.adam.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/{userId}/email")
    public AccountDto updateUserEmail(
            @PathVariable String userId,
            @RequestBody @Valid UserUpdateEmailDTO userUpdateDTO,
            @AuthenticationPrincipal AccountDto accountDto){
        String id = accountDto.getId();
        if(!id.equals(userId)){
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        AccountDto updatedAccountDto = userService.updateUserEmail(userUpdateDTO,userId);
        return updatedAccountDto;
    }

    @PutMapping("/{userId}/password")
    public AccountDto updateUserPassword(
            @PathVariable String userId,
            @RequestBody @Valid UserUpdatePasswordDTO userUpdateDTO,
            @AuthenticationPrincipal AccountDto accountDto){
        String id = accountDto.getId();
        if(!id.equals(userId)){
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        AccountDto updatedAccountDto = userService.updateUserPassword(userUpdateDTO, userId);
        return updatedAccountDto;
    }
}
