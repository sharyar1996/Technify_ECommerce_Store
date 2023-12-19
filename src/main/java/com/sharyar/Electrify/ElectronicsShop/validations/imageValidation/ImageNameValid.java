package com.sharyar.Electrify.ElectronicsShop.validations.imageValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;
import java.util.List;

@Target({ElementType.FIELD , ElementType.PARAMETER , ElementType.ANNOTATION_TYPE ,ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ImageNameValidator.class)
public @interface ImageNameValid {

    //error message
    String message() default "This Image format/type not supported.";

    //represents group of constraints
    Class<?>[] groups() default {};

    //additional information about annotations
    Class<? extends Payload>[] payload() default { };

}
