package com.github.alexanderhagenhoff.userservice.service.dto;

import jakarta.validation.constraints.NotBlank;

public record UserUpsertDto(
        @NotBlank(message = "externalSubjectId must not be blank")
        String externalSubjectId,

        @NotBlank(message = "emailHash must not be blank")
        String emailHash
) {
}
