package com.github.alexanderhagenhoff.userservice.service.dto;

public record CreateUserDto(
        String externalSubjectId,
        String email) {
}
