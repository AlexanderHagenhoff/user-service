package com.github.alexanderhagenhoff.userservice.controller;

import com.github.alexanderhagenhoff.userservice.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping(TokenController.PUBLIC_TOKEN_ENDPOINT)
public class TokenController {

    public static final String PUBLIC_TOKEN_ENDPOINT = "/token";

    private final JwtService jwtService;

    @Value("${inter-service.jwt.valid_for_seconds:3600}")
    private int secondsValid;

    @Value("${inter-service.allowed_service.client_id}")
    private String expectedClientId;

    @Value("${inter-service.allowed_service.client_secret}")
    private String expectedClientSecret;

    public TokenController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> issueToken(
            @RequestParam(name = "clientId") String clientId,
            @RequestParam(name = "clientSecret") String clientSecret,
            @RequestParam(name = "grantType", required = false) String grantType
    ) {
        if (!isAuthorized(clientId, clientSecret)) {
            return ResponseEntity.status(UNAUTHORIZED).build();
        }

        String token = jwtService.generateToken(clientId);
        return ResponseEntity.ok(Map.of(
                "access_token", token,
                "token_type", "Bearer",
                "expires_in", secondsValid
        ));
    }

    private boolean isAuthorized(String client_id, String client_secret) {
        return expectedClientId.equals(client_id) && expectedClientSecret.equals(client_secret);
    }
}
