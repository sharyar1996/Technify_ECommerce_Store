package com.sharyar.Electrify.ElectronicsShop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sharyar.Electrify.ElectronicsShop.validations.PasswordValidation.PasswordValid;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {

    private String userId;

    private String userName;

    private String email;

    private String password;

    private Set<RoleDto> roles = new HashSet<>();

    private BillingAddressDto billingAddress ;

}
