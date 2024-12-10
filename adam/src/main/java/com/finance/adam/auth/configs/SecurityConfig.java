package com.finance.adam.auth.configs;


import com.finance.adam.auth.entrypoint.RestAuthenticationEntryPoint;
import com.finance.adam.auth.filters.RestAuthenticationFilter;
import com.finance.adam.auth.handler.RestAccessDeniedHandler;
import com.finance.adam.auth.handler.RestAuthenticationFailureHandler;
import com.finance.adam.auth.handler.RestAuthenticationSuccessHandler;
import com.finance.adam.auth.service.RestRememberMeServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final AuthenticationProvider restAuthenticationProvider;
    private final RestAuthenticationSuccessHandler successHandler;
    private final RestAuthenticationFailureHandler failureHandler;
    private final RestRememberMeServices restRememberMeServices;
    private final RememberMeAuthenticationProvider rememberMeAuthenticationProvider;

    public SecurityConfig(AuthenticationProvider restAuthenticationProvider, RestAuthenticationSuccessHandler successHandler, RestAuthenticationFailureHandler failureHandler, RestRememberMeServices restRememberMeServices, RememberMeAuthenticationProvider rememberMeAuthenticationProvider) {
        this.restAuthenticationProvider = restAuthenticationProvider;
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.restRememberMeServices = restRememberMeServices;
        this.rememberMeAuthenticationProvider = rememberMeAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {


        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder.authenticationProvider(restAuthenticationProvider);
        authenticationManagerBuilder.authenticationProvider(rememberMeAuthenticationProvider);

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build(); // build() 는 최초 한번 만 호출해야 한다

        RestAuthenticationFilter restAuthenticationFilter = restAuthenticationFilter(authenticationManager, http);
        RememberMeAuthenticationFilter rememberMeAuthenticationFilter = rememberMeAuthenticationFilter(authenticationManager);

        http
                .securityMatcher("/api/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**", "/api/v1/finances/**")
                        .permitAll()
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(restAuthenticationFilter, UsernamePasswordAuthenticationFilter.class).authenticationManager(authenticationManager)
                .addFilterAfter(rememberMeAuthenticationFilter, RestAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                        .accessDeniedHandler(new RestAccessDeniedHandler())
                )
        ;

        return http.build();
    }

    private RestAuthenticationFilter restAuthenticationFilter(AuthenticationManager authenticationManager, HttpSecurity http) {

        RestAuthenticationFilter restAuthenticationFilter = new RestAuthenticationFilter(http);
        restAuthenticationFilter.setAuthenticationManager(authenticationManager);
        restAuthenticationFilter.setRememberMeServices(restRememberMeServices);
        restAuthenticationFilter.setAuthenticationSuccessHandler(successHandler);
        restAuthenticationFilter.setAuthenticationFailureHandler(failureHandler);

        return restAuthenticationFilter;
    }

    private RememberMeAuthenticationFilter rememberMeAuthenticationFilter(AuthenticationManager authenticationManager) {
        return new RememberMeAuthenticationFilter(authenticationManager, restRememberMeServices);
    }
}
