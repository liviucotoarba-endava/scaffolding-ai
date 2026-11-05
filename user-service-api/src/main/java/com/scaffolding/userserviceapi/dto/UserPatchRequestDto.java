package com.scaffolding.userserviceapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserPatchRequestDto {

    // An absent wrapper means omitted; a wrapper containing null must fail validation.
    @Valid
    private NameValue name;

    @Valid
    private EmailValue email;

    public String getName() {
        return name == null ? null : name.value();
    }

    public void setName(String name) {
        this.name = new NameValue(name);
    }

    public String getEmail() {
        return email == null ? null : email.value();
    }

    public void setEmail(String email) {
        this.email = new EmailValue(email);
    }

    private record NameValue(@NotBlank @Size(max = 100) String value) {
    }

    private record EmailValue(@NotBlank @Email @Size(max = 254) String value) {
    }
}
