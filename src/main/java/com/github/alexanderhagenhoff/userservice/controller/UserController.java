package com.github.alexanderhagenhoff.userservice.controller;

import com.github.alexanderhagenhoff.userservice.exception.EmailAlreadyInUseException;
import com.github.alexanderhagenhoff.userservice.exception.NotFoundException;
import com.github.alexanderhagenhoff.userservice.service.UserService;
import com.github.alexanderhagenhoff.userservice.service.dto.CreateUserDto;
import com.github.alexanderhagenhoff.userservice.service.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;

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

    @Operation(summary = "Retrieve a user by email", description = "Finds a user in the system by their email address.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/email")
    public ResponseEntity<UserDto> getUserByEmail(
            @Parameter(description = "Email address of the user to retrieve") @RequestParam(name = "email") String email) {
        UserDto userDto = userService.getUserByEmail(email);
        if (userDto == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userDto);
    }

    @Operation(summary = "Create a new user", description = "Adds a new user to the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User successfully created"),
            @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @Parameter(description = "User object containing first name, last name, and email") @RequestBody CreateUserDto createUserDto) {
        try {
            UserDto createdUserDto = userService.createUser(createUserDto);
            return ResponseEntity.status(CREATED).body(createdUserDto);
        } catch (EmailAlreadyInUseException e) {
            return ResponseEntity.status(CONFLICT).build();
        }
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
