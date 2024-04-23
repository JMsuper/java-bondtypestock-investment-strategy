package com.finance.adam.auth.provider;


import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.stereotype.Component;

@Component("restRememberMeAuthenticationProvider")
public class RestRememberMeAuthenticationProvider extends RememberMeAuthenticationProvider {

    public RestRememberMeAuthenticationProvider() {
        super("rememberme");
    }
}
