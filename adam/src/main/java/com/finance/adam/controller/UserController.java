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
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/email")
    public AccountDto updateUserEmail(@RequestBody @Valid UserUpdateEmailDTO userUpdateDTO, @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        if(!userId.equals(userUpdateDTO.getId())){
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        AccountDto updatedAccountDto = userService.updateUserEmail(userUpdateDTO);
        return updatedAccountDto;
    }

    @PutMapping("/password")
    public AccountDto updateUserPassword(@RequestBody @Valid UserUpdatePasswordDTO userUpdateDTO, @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        if(!userId.equals(userUpdateDTO.getId())){
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        AccountDto updatedAccountDto = userService.updateUserPassword(userUpdateDTO);
        return updatedAccountDto;
    }
}
