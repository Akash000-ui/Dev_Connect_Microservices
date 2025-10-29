package com.devconnect.auth.dto;

import com.devconnect.auth.model.UserAuth;
import com.devconnect.auth.model.UserRole;
import lombok.Data;

import java.util.Set;

@Data
public class UserResponse {

    private String id;
    private String username;
    private String email;
    private boolean verified;
    private String authProvider;
    private Set<UserRole> roles;
    private String createdAt;

    public UserResponse() {}

    public UserResponse(UserAuth user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.verified = user.isVerified();
        this.roles = user.getRoles();
        this.authProvider = user.getAuthProvider().name();
        this.createdAt = user.getCreatedAt() != null ? user.getCreatedAt().toString() : null;
    }

}