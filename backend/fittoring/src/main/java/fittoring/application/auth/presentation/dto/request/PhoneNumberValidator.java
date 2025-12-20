package fittoring.application.auth.presentation.dto.request;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    private static final String REGEXP = "^\\d{2,3}-\\d{3,4}-\\d{4}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        return Pattern.matches(REGEXP, value);
    }
}
