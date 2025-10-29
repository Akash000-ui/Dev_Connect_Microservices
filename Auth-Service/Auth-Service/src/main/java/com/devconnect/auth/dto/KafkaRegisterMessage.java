package com.devconnect.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaRegisterMessage {
    private String userId;
    private String email;
    private String name;
    private LocalDateTime timestamp;
}
