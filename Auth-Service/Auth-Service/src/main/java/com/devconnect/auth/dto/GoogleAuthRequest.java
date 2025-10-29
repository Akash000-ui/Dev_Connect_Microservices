package com.devconnect.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleAuthRequest {
    @NotBlank
    private String idToken; // Google ID token from client

    private String clientId; // optional; if provided, can override configured clientId for multi-tenant
}
