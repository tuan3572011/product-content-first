package com.product.content.security.service;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component("customAuthenticationProvider")
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private static final Logger LOGGER = Logger.getLogger(CustomAuthenticationProvider.class.getName());
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        LOGGER.log(Level.INFO, "User "+ name+ " is logging");
        String password = authentication.getCredentials().toString();
        if (!(StringUtils.isEmpty(name) || StringUtils.isEmpty(password))) {
            return new UsernamePasswordAuthenticationToken(
                    name, password, Arrays.asList(new SimpleGrantedAuthority("ROLE_PARTNER")));
        } else {
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
