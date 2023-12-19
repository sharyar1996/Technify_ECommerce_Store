package com.sharyar.Electrify.ElectronicsShop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sharyar.Electrify.ElectronicsShop.validations.PasswordValidation.PasswordValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String userId;

    @Size(min = 5 , max = 25 , message = "Name should be between 5-25 characters!")
    @NotBlank(message = "User's name is required")
    @Pattern(regexp = "[^0-9]*" , message = "Name must not contain numbers")
    private String userName;

    //  @Email(message = "Invalid userEmail!") doesn't work properly
    @Pattern(regexp = "[a-zA-Z0-9_\\'][a-zA-Z0-9_\\-\\.\\+\\?\\!\\$\\&\\=\\^\\%\\*\\'\\{\\|\\`]*[a-zA-Z0-9\\'][@][a-z\\[ 0-9][a-zA-Z0-9\\.\\-]*[a-z0-9][\\.][a-z]{2,5}" , message = "This is Invalid userEmail")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @PasswordValid
    private String password;

}
