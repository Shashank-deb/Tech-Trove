package com.shashank.electronic.store.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ImageNameValidator implements ConstraintValidator<ImageNameValid, String> {


    private Logger logger= LoggerFactory.getLogger(ImageNameValidator.class);
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

              logger.info("Message from isValid()  is "+s);

               if(s.isBlank()){
                   return false;
               }

               return true;
    }
}
