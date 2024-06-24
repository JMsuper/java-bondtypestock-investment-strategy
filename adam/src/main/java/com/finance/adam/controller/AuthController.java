package com.finance.adam.controller;

import com.finance.adam.auth.dto.AccountDto;
import com.finance.adam.exception.CustomException;
import com.finance.adam.exception.ErrorCode;
import com.finance.adam.repository.account.dto.UserRegisterDTO;
import com.finance.adam.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        Authentication authentication = SecurityContextHolder.getContextHolderStrategy().getContext().getAuthentication();
        if(authentication != null){
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        Cookie jSessionId = new Cookie("JSESSIONID", null);
        jSessionId.setMaxAge(0);
        jSessionId.setPath("/");

        Cookie rememberMe = new Cookie("remember-me", null);
        rememberMe.setMaxAge(0);
        rememberMe.setPath("/");

        response.addCookie(jSessionId);
        response.addCookie(rememberMe);

        return "success";
    }

    @PostMapping("/register")
    public String register(@RequestBody @Valid UserRegisterDTO userRegisterDTO){
        String id = userRegisterDTO.getId();
        String email = userRegisterDTO.getEmail();
        String password = userRegisterDTO.getPassword();

        userService.saveUser(id, email, password);
        return "success";
    }

    @PostMapping("/auto-login")
    public AccountDto autoLogin(@AuthenticationPrincipal AccountDto accountDto){
        if(accountDto == null) throw new CustomException(ErrorCode.UNAUTHORIZED);
        accountDto.setPassword(null);
        return accountDto;
    }

}
