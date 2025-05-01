package com.vishal.electronicsstore.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ImageNameValidator implements ConstraintValidator<ImageName, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !value.isBlank();
    }

}
