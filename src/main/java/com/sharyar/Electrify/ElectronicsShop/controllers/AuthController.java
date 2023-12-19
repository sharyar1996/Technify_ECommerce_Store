package com.sharyar.Electrify.ElectronicsShop.controllers;

import com.sharyar.Electrify.ElectronicsShop.Security.JwtHelper;
import com.sharyar.Electrify.ElectronicsShop.config.GetPrincipal;
import com.sharyar.Electrify.ElectronicsShop.dto.JwtRequest;
import com.sharyar.Electrify.ElectronicsShop.dto.JwtResponse;
import com.sharyar.Electrify.ElectronicsShop.dto.UserResponseDto;
import com.sharyar.Electrify.ElectronicsShop.exceptions.BadRequestApiException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
//@SecurityRequirement( name = "bearerAuth")
public class AuthController {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private GetPrincipal getPrincipal;



    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request)
    {
       this.doAuthenticate(request.getEmail(),request.getPassword());
       UserDetails user = loadUser(request.getEmail());
       String token = this.jwtHelper.generateToken(user);
       JwtResponse response = new JwtResponse(token , modelMapper.map(user, UserResponseDto.class));

       return  new ResponseEntity<>(response , HttpStatus.CREATED);
    }

    private void doAuthenticate(String email , String password)
    {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(email,password);
        try{
            authenticationManager.authenticate(authentication);
        }
        catch (BadCredentialsException e)
        {
            throw new BadRequestApiException("Invalid username or password");
        }

    }

    @GetMapping({"/currentUser" , "/getMyAccount"})
    public ResponseEntity<UserResponseDto> getCurrentUser()
    {
        Principal principal = getPrincipal.getPrincipal();
        UserDetails user = loadUser(principal.getName());
        return new ResponseEntity<>(modelMapper.map(user, UserResponseDto.class) , HttpStatus.OK);
    }

    private UserDetails loadUser(String username)
    {
        return userDetailsService.loadUserByUsername(username);
    }

}
