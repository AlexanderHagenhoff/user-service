package com.github.alexanderhagenhoff.userservice.controller;

import com.github.alexanderhagenhoff.userservice.exception.NotFoundException;
import com.github.alexanderhagenhoff.userservice.service.UserService;
import com.github.alexanderhagenhoff.userservice.service.dto.UserDto;
import com.github.alexanderhagenhoff.userservice.service.dto.UserUpsertDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "Endpoints for managing users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Retrieve a user by ID", description = "Fetches a user from the system using their unique UUID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(
            @Parameter(description = "UUID of the user to retrieve") @PathVariable("id") UUID id) {
        try {
            UserDto userDto = userService.getUser(id);
            return ResponseEntity.ok(userDto);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Upsert user",
            description = "Finds an existing user by external subject ID or creates a new one."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User existed and was retrieved"),
            @ApiResponse(responseCode = "201", description = "User was created")
    })
    @PutMapping("")
    public ResponseEntity<UserDto> upsertUser(@Valid @RequestBody UserUpsertDto request) {
        UserDto userDto = userService.getOrCreateUser(request.externalSubjectId(), request.emailHash());

        return ResponseEntity.ok(userDto);
    }

    @Operation(summary = "Delete a user", description = "Removes a user from the system by their UUID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User successfully deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "UUID of the user to delete") @PathVariable("id") UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
