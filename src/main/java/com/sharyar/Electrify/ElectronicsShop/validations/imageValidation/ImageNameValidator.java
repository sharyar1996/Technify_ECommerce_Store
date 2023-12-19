package com.sharyar.Electrify.ElectronicsShop.validations.imageValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ImageNameValidator implements ConstraintValidator<ImageNameValid , MultipartFile> {

    private Logger logger = LoggerFactory.getLogger(ImageNameValidator.class);


    //This is where we will write our logic for custom validation
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {

            String format = file.getContentType();
            logger.info("format in isValid() method : {}" , format );

            String[] suffix = {"image/jpg" , "image/jpeg" , "image/png" ,"image/webp" , "image/gif" };
            int suffixLen = suffix.length;

            for (int i=0 ; i< suffixLen ; i++)
            {
                if(format.equalsIgnoreCase(suffix[i]))
                {
                    return true;
                }
            }

        return  false;
    }

}
