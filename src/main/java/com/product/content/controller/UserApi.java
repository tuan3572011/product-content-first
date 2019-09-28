package com.product.content.controller;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserApi {
    final String express = "hasRole('ROLE_PARTNER')";

    @PostMapping
    public String createAccess(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName()+ "is login and access to sensitive resource";
    }

    @GetMapping
    @PreAuthorize(express)
    public String getAccess(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName() + "is login and access to read resource";
    }


    @PutMapping
    @PreAuthorize(express)
    public String editAccess(){
        return "edit";
    }

    @DeleteMapping
    public String deleteAccess(){
        return "delete";
    }
}
