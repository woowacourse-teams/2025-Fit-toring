package fittoring.mentoring.presentation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {PhoneNumberValidator.class})
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {

    String message() default "전화번호 형식이 잘못되었습니다: ex) 010-1234-5678";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
