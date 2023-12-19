package com.sharyar.Electrify.ElectronicsShop.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DuplicateValueException extends RuntimeException{

      private String message;
      private HttpStatus status;



}
