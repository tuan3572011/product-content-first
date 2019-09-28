package com.product.content.controller;

import com.product.content.security.Scope;
import com.product.content.security.service.ForceLogoutCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

@RestController
@RequestMapping("authentication")
public class AuthenticationApi {
    private static final Predicate<Integer> REMAIN_TIME_LESS_THAN_55_MINS = secondsRemaining -> ((double) secondsRemaining) / 60 < 55;

    @Autowired
    private TokenEndpoint tokenEndpoint;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private AuthorizationServerEndpointsConfiguration configuration;

    @Autowired
    private ForceLogoutCacheService logoutCacheService;

    @PostMapping
    @RequestMapping("login")
    public ResponseEntity login(HttpServletRequest request, @RequestParam("username") String userName, @RequestParam("password") String password) throws HttpRequestMethodNotSupportedException {
        if(!(userName.equals("admin") && password.equals("1234qwer"))){
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
        Map<String, String> clientSecret = getClientIdAndSecret(request);
        String clientId = clientSecret.get("client_id");
        HashMap<String, String> parameters = new HashMap<>();
        parameters.putAll(clientSecret);
        parameters.put("grant_type", "password");
        parameters.put("password", password);
        parameters.put("scope", Scope.LOGIN.getValue());
        parameters.put("username", userName);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(clientId, "", Arrays.asList(new SimpleGrantedAuthority("ROLE_LOGIN")));
        return tokenEndpoint.postAccessToken(authentication, parameters);
    }

    @PostMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    @RequestMapping("security_code/{security_code}")
    public ResponseEntity validateSecurityCode(HttpServletRequest request, @PathVariable(value = "security_code") String securityCode){
        if(!securityCode.equals("112233")){
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
        String tokenVal = Optional.ofNullable(new BearerTokenExtractor().extract(request))
                .map(Authentication::getPrincipal)
                .map(Object::toString)
                .orElse("");
        OAuth2Authentication oauth = tokenStore.readAuthentication(tokenVal);
        if(Objects.isNull(oauth)){
            return new ResponseEntity(tokenVal, HttpStatus.UNAUTHORIZED);
        }

        if (oauth.getOAuth2Request().getScope().contains(Scope.LOGIN.getValue())) {
            OAuth2AccessToken token = tokenStore.readAccessToken(tokenVal);
            if (REMAIN_TIME_LESS_THAN_55_MINS.test(token.getExpiresIn())) {
                tokenStore.removeAccessToken(token);
                return new ResponseEntity(tokenVal, HttpStatus.UNAUTHORIZED);
            }
        }

        boolean isSecurityCodeInvalid = false;
        if(securityCode.isEmpty() || isSecurityCodeInvalid){
            return new ResponseEntity(tokenVal, HttpStatus.FORBIDDEN);
        }


        HashSet<String> newScope = new HashSet<>();

        Set<String> requestedScope = oauth.getOAuth2Request().getScope();
        if(requestedScope.contains(Scope.SENSITIVE.getValue())
                || requestedScope.contains(Scope.READ.getValue())
                || requestedScope.contains(Scope.LOGIN.getValue())){
            newScope.add(Scope.SENSITIVE.getValue());
            newScope.add(Scope.LOGIN.getValue());
        }
        newScope.add(Scope.READ.getValue());
        OAuth2Request oauth2RequestReadScope = oauth.getOAuth2Request().narrowScope(newScope);
        OAuth2Authentication newAuthentication = new OAuth2Authentication(oauth2RequestReadScope, oauth.getUserAuthentication());
        Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientIdAndUserName(oauth.getOAuth2Request().getClientId(), oauth.getUserAuthentication().getPrincipal().toString());
        for(OAuth2AccessToken token: tokens){
            logoutCacheService.put(token.getValue());
            tokenStore.removeAccessToken(token);
        }
        AuthorizationServerTokenServices tokenService = configuration.getEndpointsConfigurer().getTokenServices();
        OAuth2AccessToken token = tokenService.createAccessToken(newAuthentication);
        return new ResponseEntity(token, HttpStatus.OK);
    }

    private Map<String, String> getClientIdAndSecret(HttpServletRequest request){
        Map<String, String> result = new HashMap<>();
        final String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            if(values.length == 2){
                result.put("client_id", values[0]);
                result.put("client_secret", values[1]);
                return result;
            }
        }
        result.put("client_id", "partner-client");
        result.put("client_secret", "partner-client");
        return result;
    }

}
