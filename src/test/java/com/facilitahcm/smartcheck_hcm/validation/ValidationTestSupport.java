package com.facilitahcm.smartcheck_hcm.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class ValidationTestSupport {
    protected static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    protected <T> Set<ConstraintViolation<T>> validate(T target) {
        return VALIDATOR.validate(target);
    }

    protected <T> void assertNoViolations(T target) {
        Set<ConstraintViolation<T>> violations = validate(target);
        assertTrue(violations.isEmpty(), () -> "Expected no violations, but got: " + violations);
    }

    protected <T> void assertHasViolation(T target, String field, String expectedMessage) {
        Set<ConstraintViolation<T>> violations = validate(target);
        boolean match = violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals(field)
                        && violation.getMessage().equals(expectedMessage));

        assertTrue(match, () -> "Expected violation on field '" + field + "' with message '" + expectedMessage + "', but got: " + violations);
    }

    protected static String senhaComTamanhoExato(int tamanho) {
        if (tamanho < 8) {
            throw new IllegalArgumentException("Tamanho mínimo de senha é 8");
        }
        if (tamanho > 50) {
            throw new IllegalArgumentException("Tamanho máximo de senha é 50");
        }

        String base = "Senha1!bc";

        if (tamanho < 9) {
            return base.substring(0, tamanho);
        }

        String padrao = "abcdefghijklmnopqrstuvwxyz";
        String padding = padrao.repeat((tamanho - base.length()) / padrao.length() + 1);
        return base + padding.substring(0, tamanho - base.length());
    }
}
