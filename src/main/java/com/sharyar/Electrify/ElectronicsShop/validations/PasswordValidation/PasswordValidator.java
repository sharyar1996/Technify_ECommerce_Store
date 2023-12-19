package com.sharyar.Electrify.ElectronicsShop.validations.PasswordValidation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordValid , String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        int len = value.length();
        if( len <8 || len > 64)
        {
            return false;
        }
        boolean isDigit = false;
        boolean isUpperCase = false;
        boolean isLowerCase = false;
        boolean hasSpecialCharacters = false;
        boolean hasNoSpace = true;

        for( int i=0 ; i< len ; i++)
        {
            if( Character.isDigit( value.charAt(i) ))
            {
                isDigit = true;
            }
            else if(Character.isLowerCase(value.charAt(i)))
            {
                isLowerCase = true;
            }
            else if (Character.isUpperCase(value.charAt(i)))
            {
                isUpperCase = true;
            }
            else if (Character.isWhitespace(value.charAt(i)))
            {
                 hasNoSpace = false;
                 return  false;
            }
            else{
                hasSpecialCharacters = true;
            }
        }
       if( isDigit && isLowerCase && isUpperCase && hasSpecialCharacters)
       {
           return true;
       }
        return false;
    }

}
