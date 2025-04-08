package com.github.alexanderhagenhoff.userservice.service.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public record UserDto(UUID id, String firstName, String lastName, String email, ZonedDateTime createdAt,
                      ZonedDateTime lastModifiedAt) {
}
