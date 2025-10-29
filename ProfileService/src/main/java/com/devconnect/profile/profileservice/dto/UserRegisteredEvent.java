package com.devconnect.profile.profileservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent {
    private String userId;
    private String email;
    private String name;
    private LocalDateTime timestamp;
}