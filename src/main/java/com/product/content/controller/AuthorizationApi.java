package com.product.content.controller;

import com.product.content.security.Scope;
import com.product.content.security.service.ForceLogoutCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.endpoint.CheckTokenEndpoint;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;

import java.util.function.Predicate;

@RestController
@RequestMapping("authorization")
public class AuthorizationApi {
    private static final Predicate<Integer> REMAIN_TIME_LESS_THAN_45_MINS = secondsRemaining -> ((double) secondsRemaining) / 60 < 45;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private AccessTokenConverter tokenConverter;

    @Autowired
    private CheckTokenEndpoint checkTokenEndpoint;

    private WebResponseExceptionTranslator<OAuth2Exception> exceptionTranslator = new DefaultWebResponseExceptionTranslator();


    @Autowired
    private ForceLogoutCacheService logoutCacheService;

    @PostMapping
    @RequestMapping("check_token")
    public Object validateToken(@RequestParam("token") String value) throws Exception {
        if(logoutCacheService.contains(value)){
            return new InvalidTokenException("Another user is logged in using this account");
        }
        OAuth2AccessToken token = tokenStore.readAccessToken(value);
        if (token == null) {
            return checkTokenEndpoint.handleException(new InvalidTokenException("Token was not recognised"));
        }

        if (token.isExpired()) {
            return checkTokenEndpoint.handleException(new InvalidTokenException("Token has expired"));
        }

        OAuth2Authentication authentication = tokenStore.readAuthentication(token.getValue());

        if (authentication.getOAuth2Request().getScope().contains(Scope.SENSITIVE.getValue())) {
            if (REMAIN_TIME_LESS_THAN_45_MINS.test(token.getExpiresIn())) {
                return exceptionTranslator.translate(new AccessDeniedException("sensitive scope is expired"));
            }
        }

        return tokenConverter.convertAccessToken(token, authentication);
    }

    @PostMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    @RequestMapping("token/{token}")
    public boolean isLoggedInByAnOther(@PathVariable("token") String token) {
        return logoutCacheService.contains(token);
    }
}
