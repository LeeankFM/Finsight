package com.finsight.portfoliomanager.application.ports.dto.auth;

import java.util.UUID;
import com.finsight.portfoliomanager.domain.Role;

import lombok.Builder;

@Builder
public class AuthResult {
    UUID userId;
    String email;
    String username;
    Role role;

    String accessToken;
    String refreshToken;
}
