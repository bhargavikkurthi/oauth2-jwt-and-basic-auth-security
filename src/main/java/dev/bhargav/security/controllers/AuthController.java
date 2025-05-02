package dev.bhargav.security.controllers;

import dev.bhargav.security.entity.User;
import dev.bhargav.security.exception.UserAlreadyExistAuthenticationException;
import dev.bhargav.security.model.ApiGenericResponse;
import dev.bhargav.security.model.JwtAuthenticationResponse;
import dev.bhargav.security.model.SignUpRequest;
import dev.bhargav.security.model.UserDto;
import dev.bhargav.security.service.UserManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.stream.Collectors;

@Tag(name = "Authentication Management", description = "Authentication Management APIs")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    UserManagementService userManagementService;

    @Autowired
    JwtEncoder jwtEncoder;

    @Operation(summary = "Create a new user")
    @ApiResponse(responseCode = "200", description = "New user created successfully",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiGenericResponse.class))})
    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiGenericResponse> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) throws UserAlreadyExistAuthenticationException {
        UserDto userDto;
        try {
            userDto = this.userManagementService.registerNewUser(signUpRequest);
        } catch (UserAlreadyExistAuthenticationException e) {
            throw new UserAlreadyExistAuthenticationException("Username is already in use");
        }
        return ResponseEntity.ok().body(new ApiGenericResponse(true, "User registered successfully: " + userDto.getUsername()));
    }

    @Operation(summary = "Get Bearer Access Token")
    @ApiResponse(responseCode = "200", description = "Fetched access token",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = JwtAuthenticationResponse.class))})
    @PostMapping(value = "/token", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public ResponseEntity<JwtAuthenticationResponse> getToken(Authentication authentication) {
        User localUser = (User) authentication.getPrincipal();
        String jwt = getToken(localUser);
        return ResponseEntity.ok((new JwtAuthenticationResponse(jwt)));
    }

    /**
     * Builds JWT token for the given User.
     *
     * @param localUser - UserEntity which implements UserDetails
     * @return token - String - JWT Token
     */
    private String getToken(User localUser) {
        Instant now = Instant.now();
        long expiry = 36000L;
        String scope = localUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(localUser.getUsername())
                .claim("scope", scope)
                .build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
