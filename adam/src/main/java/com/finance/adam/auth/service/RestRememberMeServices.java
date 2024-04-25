package com.finance.adam.auth.service;

import com.finance.adam.auth.dto.AccountContext;
import com.finance.adam.auth.dto.AccountDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

@Component
public class RestRememberMeServices extends TokenBasedRememberMeServices {
    public RestRememberMeServices(UserDetailsService userDetailsService) {
        super("rememberme", userDetailsService);
    }

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    public void onLoginSuccess(HttpServletRequest request, HttpServletResponse response,
                               Authentication successfulAuthentication) {
        AccountDto principal = (AccountDto) successfulAuthentication.getPrincipal();
        String username = principal.getId();
        String password = retrievePassword(successfulAuthentication);

        if (!StringUtils.hasLength(username)) {
            this.logger.debug("Unable to retrieve username");
            return;
        }
        if (!StringUtils.hasLength(password)) {
            UserDetails user = getUserDetailsService().loadUserByUsername(username);
            password = user.getPassword();
            if (!StringUtils.hasLength(password)) {
                this.logger.debug("Unable to obtain password for user: " + username);
                return;
            }
        }
        int tokenLifetime = calculateLoginLifetime(request, successfulAuthentication);
        long expiryTime = System.currentTimeMillis();

        expiryTime += 1000L * ((tokenLifetime < 0) ? TWO_WEEKS_S : tokenLifetime);
        String signatureValue = makeTokenSignature(expiryTime, username, password, RememberMeTokenAlgorithm.SHA256);
        setCookie(new String[] { username, Long.toString(expiryTime), RememberMeTokenAlgorithm.SHA256.name(), signatureValue },
                tokenLifetime, request, response);
        if (this.logger.isDebugEnabled()) {
            this.logger
                    .debug("Added remember-me cookie for user '" + username + "', expiry: '" + new Date(expiryTime) + "'");
        }
    }

    @Override
    protected Authentication createSuccessfulAuthentication(HttpServletRequest request, UserDetails user) {
        AccountContext accountContext = (AccountContext) user;
        AccountDto accountDto = accountContext.getAccountDto();
        RememberMeAuthenticationToken auth = new RememberMeAuthenticationToken(this.getKey(), accountDto,
                authoritiesMapper.mapAuthorities(user.getAuthorities()));
        auth.setDetails(getAuthenticationDetailsSource().buildDetails(request));
        return auth;
    }

}
