package com.sharyar.Electrify.ElectronicsShop.services.implementations;

import com.sharyar.Electrify.ElectronicsShop.config.GetPrincipal;
import com.sharyar.Electrify.ElectronicsShop.dto.BillingAddressDto;
import com.sharyar.Electrify.ElectronicsShop.dto.UserResponseDto;
import com.sharyar.Electrify.ElectronicsShop.entities.BillingAddress;
import com.sharyar.Electrify.ElectronicsShop.entities.User;
import com.sharyar.Electrify.ElectronicsShop.repositories.BillingAddressRepository;
import com.sharyar.Electrify.ElectronicsShop.services.BillingAddressService;
import com.sharyar.Electrify.ElectronicsShop.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class BillingAddressServiceImplementation  implements BillingAddressService {
    @Autowired
    private BillingAddressRepository billingAddressRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private GetPrincipal getPrincipal;
    @Autowired
    private UserDetailsService userDetailsService;


    @Override
    public UserResponseDto setBillingAddress(BillingAddressDto dto) {

        Principal principal = getPrincipal.getPrincipal();
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        user.setBillingAddress(null);
        userService.updateUser(user);
        BillingAddress address = billingAddressRepository.save(modelMapper.map(dto , BillingAddress.class));
        user.setBillingAddress(address);
        address.setUser(user);
        billingAddressRepository.save(address);
        User savedUser = address.getUser();

        return modelMapper.map( savedUser , UserResponseDto.class );
    }

    @Override
    public UserResponseDto updateBillingAddress(BillingAddressDto dto) {

        Principal principal = getPrincipal.getPrincipal();
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        BillingAddress existingBillingAddress = user.getBillingAddress();
        existingBillingAddress.setAddress(dto.getAddress());
        existingBillingAddress.setCity(dto.getCity());
        existingBillingAddress.setState(dto.getState());
        existingBillingAddress.setPhoneNumber(dto.getPhoneNumber());
        existingBillingAddress.setZipCode(dto.getZipCode());
        billingAddressRepository.save(existingBillingAddress);

        return modelMapper.map(user , UserResponseDto.class);
    }
    @Override
    public BillingAddressDto getBillingAddress() {

        Principal principal = getPrincipal.getPrincipal();
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());

        return modelMapper.map(user.getBillingAddress() , BillingAddressDto.class);
    }

}
