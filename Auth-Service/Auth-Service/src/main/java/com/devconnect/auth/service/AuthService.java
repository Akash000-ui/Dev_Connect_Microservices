package com.devconnect.auth.service;

import com.devconnect.auth.dto.*;
import com.devconnect.auth.exception.RegistrationException;
import com.devconnect.auth.model.AuthProvider;
import com.devconnect.auth.model.UserAuth;
import com.devconnect.auth.model.UserRole;
import com.devconnect.auth.repo.AuthRepo;
import com.devconnect.auth.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthRepo UserAuthRepository;

    @Value("${google.clientId:}")
    private String googleClientId;

    public ApiResponse<AuthResponse> register(RegisterRequestDTO request) {

        if (UserAuthRepository.existsByEmail(request.getEmail())) {
            throw new RegistrationException("Email already exists");
        }

        if (request.getEmail().isEmpty()) {
            throw new RegistrationException("Email is required");
        }

        //checking username is unique from other users
        if (request.getUsername().isEmpty()){
            throw new RegistrationException("Username is required");
        }

        if (UserAuthRepository.findAll().stream()
                .anyMatch(user -> request.getUsername().equals(user.getUsername()))) {
            throw new RegistrationException("Username already exists");
        }

        UserAuth user = new UserAuth();

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUsername(request.getUsername());
        user.setCreatedAt(java.time.LocalDateTime.now());
        user.setUpdatedAt(java.time.LocalDateTime.now());

        user.getRoles().add(UserRole.USER);

        UserAuthRepository.save(user);
        AuthResponse authResponse = AuthResponse.builder()
                .token(jwtUtil.generateToken(user.getId(), user.getEmail()))
                .user(new UserResponse(user))
                .build();
        if (authResponse.getToken() == null) {
            ApiResponse<AuthResponse> response = new ApiResponse<>(false, "Token generation failed", null);
            response.setStatusCode(500);
            return response;
        }

        sendAuthEvent(KafkaRegisterMessage.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getUsername())
                .timestamp(java.time.LocalDateTime.now())
                .build());

        ApiResponse<AuthResponse> response = new ApiResponse<>(true, "UserAuth registered successfully", authResponse);
        response.setStatusCode(201);
        return response;
    }

    public ApiResponse<AuthResponse> login(LoginRequestDTO request) {

        UserAuth user = UserAuthRepository.findByEmail(request.getEmail());
        if (user == null) {
            ApiResponse<AuthResponse> response = new ApiResponse<>(false, "UserAuth not found", null);
            response.setStatusCode(404);
            return response;
        }

        // If the account is a Google account, require Google sign-in
        if (user.getAuthProvider() == AuthProvider.GOOGLE) {
            ApiResponse<AuthResponse> response = new ApiResponse<>(false, "This account uses Google sign-in. Please sign in with Google.", null);
            response.setStatusCode(409);
            return response;
        }

        if (user.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            ApiResponse<AuthResponse> response = new ApiResponse<>(false, "Invalid credentials", null);
            response.setStatusCode(401);
            return response;
        }

        // Generate token
        AuthResponse authResponse = AuthResponse.builder()
                .token(jwtUtil.generateToken(user.getId(), user.getEmail()))
                .user(new UserResponse(user))
                .build();

        if (authResponse.getToken() == null) {
            ApiResponse<AuthResponse> response = new ApiResponse<>(false, "Token generation failed", null);
            response.setStatusCode(500);
            return response;
        }

        ApiResponse<AuthResponse> response = new ApiResponse<>(true, "Login successful", authResponse);
        response.setStatusCode(200);
        return response;
    }

    public ApiResponse<String> deleteAccount(String UserAuthId) {

        UserAuth UserAuth = UserAuthRepository.findById(UserAuthId).orElse(null);
        if (UserAuth == null) {
            ApiResponse<String> response = new ApiResponse<>(false, "UserAuth not found", null);
            response.setStatusCode(404);
            return response;
        }

        try {
            UserAuthRepository.delete(UserAuth);
            ApiResponse<String> response = new ApiResponse<>(true, "Account deleted successfully", null);
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(false, "Failed to delete account", null);
            response.setStatusCode(500);
            return response;
        }
    }


    public ApiResponse<String> addAdminRole(String UserAuthId) {

        UserAuth UserAuth = UserAuthRepository.findById(UserAuthId).orElse(null);
        if (UserAuth == null) {
            ApiResponse<String> response = new ApiResponse<>(false, "UserAuth not found", null);
            response.setStatusCode(404);
            return response;
        }

        if (UserAuth.getRoles().contains(UserRole.ADMIN)) {
            ApiResponse<String> response = new ApiResponse<>(false, "UserAuth is already an admin", null);
            response.setStatusCode(400);
            return response;
        }

        UserAuth.getRoles().add(UserRole.ADMIN);
        UserAuth.setUpdatedAt(java.time.LocalDateTime.now());
        UserAuthRepository.save(UserAuth);

        ApiResponse<String> response = new ApiResponse<>(true, "Admin role added successfully", null);
        response.setStatusCode(200);
        return response;
    }

    public ApiResponse<AuthResponse> googleLogin(GoogleAuthRequest request) {
        // Verify the ID token with Google
        try {
            String expectedClientId = Optional.ofNullable(request.getClientId()).filter(s -> !s.isBlank()).orElse(googleClientId);
            if (expectedClientId == null || expectedClientId.isBlank()) {
                ApiResponse<AuthResponse> response = new ApiResponse<>(false, "Google clientId not configured", null);
                response.setStatusCode(500);
                return response;
            }

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(expectedClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.getIdToken());
            if (idToken == null) {
                ApiResponse<AuthResponse> response = new ApiResponse<>(false, "Invalid Google ID token", null);
                response.setStatusCode(401);
                return response;
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = Optional.ofNullable((String) payload.get("name")).orElse(email);

            UserAuth UserAuth = UserAuthRepository.findByEmail(email);
            if (UserAuth == null) {
                // Create new UserAuth as GOOGLE provider
                UserAuth = UserAuth.builder()
                        .email(email)
                        .verified(true)
                        .authProvider(AuthProvider.GOOGLE)
                        .createdAt(java.time.LocalDateTime.now())
                        .updatedAt(java.time.LocalDateTime.now())
                        .build();
                UserAuth.getRoles().add(UserRole.USER);
            } else {
                // If existing LOCAL account, block sign-in via Google to avoid account takeover
                if (UserAuth.getAuthProvider() == AuthProvider.LOCAL) {
                    ApiResponse<AuthResponse> response = new ApiResponse<>(false, "Account registered with email/password. Please sign in with password.", null);
                    response.setStatusCode(409);
                    return response;
                }
                // Ensure provider is GOOGLE and mark verified
                UserAuth.setAuthProvider(AuthProvider.GOOGLE);
                UserAuth.setVerified(true);
                UserAuth.setUpdatedAt(java.time.LocalDateTime.now());
//
                if (!UserAuth.getRoles().contains(UserRole.USER)) {
                    UserAuth.getRoles().add(UserRole.USER);
                }
            }

            UserAuthRepository.save(UserAuth);

            AuthResponse authResponse = AuthResponse.builder()
                    .token(jwtUtil.generateToken(UserAuth.getId(), UserAuth.getEmail()))
                    .user(new UserResponse(UserAuth))
                    .build();

            ApiResponse<AuthResponse> response = new ApiResponse<>(true, "Login successful", authResponse);
            response.setStatusCode(200);
            return response;
        } catch (java.security.GeneralSecurityException | java.io.IOException e) {
            ApiResponse<AuthResponse> response = new ApiResponse<>(false, "Google login failed", null);
            response.setStatusCode(500);
            return response;
        }
    }


    private static final String TOPIC = "auth-events";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendAuthEvent(KafkaRegisterMessage message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(TOPIC, jsonMessage);
            System.out.println("✅ Sent auth event: " + jsonMessage);
        } catch (Exception e) {
            System.err.println("❌ Failed to send Kafka message: " + e.getMessage());
        }
    }
}
