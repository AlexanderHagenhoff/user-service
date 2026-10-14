package com.github.alexanderhagenhoff.userservice.service.dto;

public record CreateUserDto(
        String firstName,
        String lastName,
        String externalSubjectId,
        String email) {
}
