package com.sharyar.Electrify.ElectronicsShop.validations.PasswordValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD , ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PasswordValidator.class)
public @interface PasswordValid {

    //error message
    String message() default "Password should contain atleast 1 lowercase, 1 uppercase," +
                             " 1 digit and 1 special character and should be between" +
                             " 8-64 characters";

    //represents group of constraints
    Class<?>[] groups() default {};

    //Additional informtation about annotation
    Class<? extends Payload>[] payload() default { };

}
