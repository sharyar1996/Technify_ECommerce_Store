package com.sharyar.Electrify.ElectronicsShop.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sharyar.Electrify.ElectronicsShop.entities.User;
import com.sharyar.Electrify.ElectronicsShop.validations.cityValidation.CityValid;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
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
public class BillingAddressDto {

    @Size(min=10 , message = "Please enter a correct phone number.")
    @Pattern(regexp = "[0-9]{10}")
    private String phoneNumber;
    @NotBlank
    private String address;
    @CityValid
    private String city;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private final String country = "Pakistan";
    private String state;
    private String zipCode;


}
