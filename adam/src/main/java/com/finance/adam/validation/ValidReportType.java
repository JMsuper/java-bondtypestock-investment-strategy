package com.finance.adam.validation;

import com.finance.adam.repository.reportalarm.domain.ReportType;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {ReportTypeValidator.class})
@Target({ElementType.METHOD,ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidReportType{
    String message() default "Invalid value. This is not permitted.";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    Class<ReportType> enumClass();
}
