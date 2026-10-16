package com.scaffolding.userserviceapi.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.annotation.Annotation;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class UserRequestDtoTest {

    private static final String MAX_LENGTH_EMAIL = "a".repeat(64) + "@" + "b".repeat(63)
            + "." + "c".repeat(63) + "." + "d".repeat(57) + ".com";

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @Test
    void acceptsValidNameAndEmail() {
        assertThat(validator.validate(new UserRequestDto("Ada Lovelace", "ada@example.com"))).isEmpty();
    }

    @Test
    void acceptsMaximumFieldLengths() {
        assertThat(validator.validate(new UserRequestDto("a".repeat(100), MAX_LENGTH_EMAIL))).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void rejectsInvalidFields(UserRequestDto request, String field, Class<? extends Annotation> constraint) {
        assertThat(validator.validate(request))
                .anyMatch(violation -> violation.getPropertyPath().toString().equals(field)
                        && violation.getConstraintDescriptor().getAnnotation().annotationType().equals(constraint));
    }

    private static Stream<Arguments> invalidRequests() {
        return Stream.of(
                Arguments.of(new UserRequestDto(null, "ada@example.com"), "name", NotBlank.class),
                Arguments.of(new UserRequestDto(" ", "ada@example.com"), "name", NotBlank.class),
                Arguments.of(new UserRequestDto("a".repeat(101), "ada@example.com"), "name", Size.class),
                Arguments.of(new UserRequestDto("Ada Lovelace", null), "email", NotBlank.class),
                Arguments.of(new UserRequestDto("Ada Lovelace", " "), "email", NotBlank.class),
                Arguments.of(new UserRequestDto("Ada Lovelace", "invalid-email"), "email", Email.class),
                Arguments.of(new UserRequestDto("Ada Lovelace", MAX_LENGTH_EMAIL + "x"), "email", Size.class)
        );
    }
}
