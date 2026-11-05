package com.scaffolding.userserviceapi.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class UserPatchRequestDtoTest {

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
    void omittedFieldsAreValid() {
        assertThat(validator.validate(new UserPatchRequestDto())).isEmpty();
    }

    @Test
    void acceptsMaximumFieldLengths() {
        UserPatchRequestDto request = new UserPatchRequestDto();
        request.setName("a".repeat(100));
        request.setEmail("a".repeat(64) + "@" + "b".repeat(63) + "." + "c".repeat(63)
                + "." + "d".repeat(57) + ".com");

        assertThat(validator.validate(request)).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void rejectsSuppliedNullOrBlankName(String value) {
        UserPatchRequestDto request = new UserPatchRequestDto();
        request.setName(value);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "invalid-email"})
    void rejectsSuppliedInvalidEmail(String value) {
        UserPatchRequestDto request = new UserPatchRequestDto();
        request.setEmail(value);

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void rejectsNameBeyondMaximumLength() {
        UserPatchRequestDto request = new UserPatchRequestDto();
        request.setName("a".repeat(101));

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void rejectsEmailBeyondMaximumLengthEvenWhenFormatIsValid() {
        UserPatchRequestDto request = new UserPatchRequestDto();
        request.setEmail("a".repeat(64) + "@" + "b".repeat(63) + "." + "c".repeat(63)
                + "." + "d".repeat(58) + ".com");

        assertThat(validator.validate(request)).anyMatch(violation ->
                violation.getConstraintDescriptor().getAnnotation()
                        instanceof jakarta.validation.constraints.Size);
    }
}
