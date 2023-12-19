package com.sharyar.Electrify.ElectronicsShop.services;

import com.sharyar.Electrify.ElectronicsShop.dto.BillingAddressDto;
import com.sharyar.Electrify.ElectronicsShop.dto.UserResponseDto;

public interface BillingAddressService {
    UserResponseDto setBillingAddress(BillingAddressDto dto);
    BillingAddressDto getBillingAddress();
    UserResponseDto updateBillingAddress(BillingAddressDto dto);
}
