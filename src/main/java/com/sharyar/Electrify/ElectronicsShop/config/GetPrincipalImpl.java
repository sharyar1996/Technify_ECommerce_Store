package com.sharyar.Electrify.ElectronicsShop.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class GetPrincipalImpl implements GetPrincipal {

    @Override
    public Principal getPrincipal() {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        return principal;
    }

}
