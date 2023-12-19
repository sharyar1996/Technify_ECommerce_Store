package com.sharyar.Electrify.ElectronicsShop.config;

import org.springframework.security.core.Authentication;

import java.security.Principal;

public interface GetPrincipal {
    Principal getPrincipal();
}
