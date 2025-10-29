package com.devconnect.profile.profileservice.consumer;

import com.devconnect.profile.profileservice.dto.UserRegisteredEvent;
import com.devconnect.profile.profileservice.service.ProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegisteredEventConsumer {

    private final ProfileService profileService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "auth-events", groupId = "profile-service")
    public void handleUserRegisteredEvent(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        try {
            log.info("🔔 Received message from topic: {}, partition: {}, offset: {}", topic, partition, offset);
            log.info("📨 Message content: {}", message);

            // Parse the JSON message
            UserRegisteredEvent event = objectMapper.readValue(message, UserRegisteredEvent.class);

            log.info("👤 Processing user registration event for userId: {}, email: {}",
                    event.getUserId(), event.getEmail());

            // Create default profile
            profileService.createDefaultProfile(event);

            // Manually acknowledge the message
            acknowledgment.acknowledge();

            log.info("✅ Successfully processed user registration event for userId: {}", event.getUserId());

        } catch (Exception e) {
            log.error("❌ Failed to process user registration event. Message: {}. Error: {}",
                    message, e.getMessage(), e);

            // In a production environment, you might want to:
            // 1. Send to a dead letter queue
            // 2. Implement retry logic
            // 3. Alert monitoring systems

            // For now, we'll acknowledge to avoid infinite reprocessing
            // but in production you should handle this more carefully
            acknowledgment.acknowledge();
        }
    }
}