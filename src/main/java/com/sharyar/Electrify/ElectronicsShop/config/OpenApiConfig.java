package com.sharyar.Electrify.ElectronicsShop.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.servers.Servers;

@OpenAPIDefinition(
        info = @Info(
                   contact = @Contact(
                        name = "Syed Sharyar Javaid",
                        email = "sharyarjavaid1@gmail.com"
                       ),
                   title = "Technify",
                   description = "Pakistan's no.1 online store for computers , laptops , mobiles and " +
                           "many other electronic devices and accessories. Delivery services only " +
                           " in Pakistan."
        ),
        servers = @Server(
                  url = "http://localhost:9090"
        ),
        security = @SecurityRequirement(
                name = "bearerAuth"
        )
)
@SecurityScheme(
        name = "bearerAuth",
        description = "Jwt authentication. Provide your jwt token to get access as a user.",
        type= SecuritySchemeType.HTTP,
        scheme = "Bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {

}
