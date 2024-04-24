package com.finance.adam.controller;

import com.finance.adam.dto.UserRegisterDTO;
import com.finance.adam.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/test")
    public String test(){
        return "test";
    }

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

}
