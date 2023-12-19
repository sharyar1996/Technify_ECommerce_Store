package com.sharyar.Electrify.ElectronicsShop.validations.cityValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CityValidator implements ConstraintValidator < CityValid , String> {


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        String[] availableCities = {"karachi" , "lahore" , "faisalabad" ,"rawalpindi",
                                    "quetta", "peshawar" , "multan" , "hyderabad"
                                   };
        int length = availableCities.length;
        value = value.replaceAll(" " , "").toLowerCase();

        for(int i=0 ; i< length ; i++)
        {
            if(value.equals(availableCities[i]))
            {
                return true;
            }
        }

        return  false;
    }
}
