package org.example.sem_backend.common_module.service.validator_service;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PasswordValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
public @interface ValidPassword {
    String message() default "Password không hợp lệ: Phải có ít nhất một chữ hoa, một chữ thường, một ký tự đặc biệt và không chứa các ký tự +, -, x, :";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

