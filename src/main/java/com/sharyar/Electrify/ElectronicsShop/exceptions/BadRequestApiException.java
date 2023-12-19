package com.sharyar.Electrify.ElectronicsShop.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
public class BadRequestApiException extends RuntimeException {
    private String message = "Bad Api Request from user";
    private final boolean  success = false;
    private final HttpStatus status =HttpStatus.BAD_REQUEST;

    public BadRequestApiException(String message)
    {
        this.message = message;
    }

}
