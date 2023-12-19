package com.sharyar.Electrify.ElectronicsShop.validations.cityValidation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD , ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = CityValidator.class)
public @interface CityValid {
    //default error message:
    String message() default "We are not available in this city.Make sure you entered " +
                             "correct spelling of the city.";

    //represents group of constraints
    Class<?>[] groups() default {};

    //Additional informtation about annotation
    Class<? extends Payload>[] payload() default { };

}
