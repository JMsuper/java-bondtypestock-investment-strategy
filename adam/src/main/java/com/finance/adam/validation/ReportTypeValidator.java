package com.finance.adam.validation;

import com.finance.adam.repository.reportalarm.domain.ReportType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReportTypeValidator implements ConstraintValidator<ValidReportType, ReportType>{

    private List<ReportType> reportTypeList;

    @Override
    public void initialize(ValidReportType constraintAnnotation) {
        this.reportTypeList = Arrays.stream(constraintAnnotation.enumClass().getEnumConstants())
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(ReportType value, ConstraintValidatorContext context) {
        return reportTypeList.contains(value);
    }
}
