package org.example.sem_backend.common_module.service.validator_service;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        return value.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)[^+\\-x:]*$");
    }
}

