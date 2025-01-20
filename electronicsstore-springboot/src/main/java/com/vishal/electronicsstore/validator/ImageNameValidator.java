package com.vishal.electronicsstore.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ImageNameValidator implements ConstraintValidator<ImageName, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !value.isBlank();
    }

}
