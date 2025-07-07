package com.funding.backend.global.validator.annotaion;

import com.funding.backend.global.validator.ProjectDetailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ProjectDetailValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidProjectDetail {
    String message() default "잘못된 프로젝트 상세 정보입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

