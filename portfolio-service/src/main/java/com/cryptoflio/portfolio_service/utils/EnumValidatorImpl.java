package com.cryptoflio.portfolio_service.utils;

import com.cryptoflio.portfolio_service.Entites.Action;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;

public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, Action> {

    private List<String> valueList;

    @Override
    public void initialize(EnumValidator constraintAnnotation) {
        valueList = new ArrayList<>();
        Class<? extends Enum<?>> enumClass = constraintAnnotation.enumClazz();
        Enum<?>[] enumValArr = enumClass.getEnumConstants();

        for (Enum<?> enumVal : enumValArr) {
            valueList.add(enumVal.name().toUpperCase());
        }
    }

    @Override
    public boolean isValid(Action value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Consider null values as valid. Use @NotNull to enforce non-null values.
        }
        return valueList.contains(value.name().toUpperCase());
    }
}
