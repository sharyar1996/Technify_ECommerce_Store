package com.sharyar.Electrify.ElectronicsShop.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JwtResponse {

       private String jwtToken;
       private UserResponseDto userResponseDto;

}
