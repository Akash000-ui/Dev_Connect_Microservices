package com.devconnect.profile.profileservice.controller;

import com.devconnect.profile.profileservice.dto.ApiResponse;
import com.devconnect.profile.profileservice.dto.ProfileUpdateRequest;
import com.devconnect.profile.profileservice.model.UserProfile;
import com.devconnect.profile.profileservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ProfileController {

        private final ProfileService profileService;

        @GetMapping("/health")
        public ResponseEntity<ApiResponse<String>> health() {
                ApiResponse<String> response = ApiResponse.<String>builder()
                                .success(true)
                                .message("Profile Service is running")
                                .data("OK")
                                .statusCode(HttpStatus.OK.value())
                                .build();

                return ResponseEntity.ok(response);
        }

        @GetMapping("/me")
        public ResponseEntity<ApiResponse<UserProfile>> getCurrentUserProfile(HttpServletRequest request) {
                // Get user ID from Gateway headers instead of JWT
                String userId = request.getHeader("X-User-Id");

                if (userId == null) {
                        ApiResponse<UserProfile> response = ApiResponse.<UserProfile>builder()
                                        .success(false)
                                        .message("User ID not found in request headers")
                                        .data(null)
                                        .statusCode(HttpStatus.UNAUTHORIZED.value())
                                        .build();
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }
                UserProfile profile = profileService.getProfileByUserId(userId)
                                .orElseThrow(() -> new RuntimeException("Profile not found"));

                ApiResponse<UserProfile> response = ApiResponse.<UserProfile>builder()
                                .success(true)
                                .message("Profile retrieved successfully")
                                .data(profile)
                                .statusCode(HttpStatus.OK.value())
                                .build();

                return ResponseEntity.ok(response);
        }

        @GetMapping("/{userId}")
        public ResponseEntity<ApiResponse<UserProfile>> getProfileByUserId(@PathVariable String userId) {
                UserProfile profile = profileService.getProfileByUserId(userId)
                                .orElseThrow(() -> new RuntimeException("Profile not found for userId: " + userId));

                ApiResponse<UserProfile> response = ApiResponse.<UserProfile>builder()
                                .success(true)
                                .message("Profile retrieved successfully")
                                .data(profile)
                                .statusCode(HttpStatus.OK.value())
                                .build();

                return ResponseEntity.ok(response);
        }

        @PutMapping("/me")
        public ResponseEntity<ApiResponse<UserProfile>> updateCurrentUserProfile(
                        @Valid @RequestBody ProfileUpdateRequest updateRequest,
                        HttpServletRequest request) {
                String userId = request.getHeader("X-User-Id");

                if (userId == null) {
                        ApiResponse<UserProfile> response = ApiResponse.<UserProfile>builder()
                                        .success(false)
                                        .message("User ID not found in request headers")
                                        .data(null)
                                        .statusCode(HttpStatus.UNAUTHORIZED.value())
                                        .build();
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }

                UserProfile updatedProfile = profileService.updateProfile(userId, updateRequest);

                ApiResponse<UserProfile> response = ApiResponse.<UserProfile>builder()
                                .success(true)
                                .message("Profile updated successfully")
                                .data(updatedProfile)
                                .statusCode(HttpStatus.OK.value())
                                .build();

                return ResponseEntity.ok(response);
        }

        @DeleteMapping("/me")
        public ResponseEntity<ApiResponse<String>> deleteCurrentUserProfile(HttpServletRequest request) {
                String userId = request.getHeader("X-User-Id");

                if (userId == null) {
                        ApiResponse<String> response = ApiResponse.<String>builder()
                                        .success(false)
                                        .message("User ID not found in request headers")
                                        .data(null)
                                        .statusCode(HttpStatus.UNAUTHORIZED.value())
                                        .build();
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }

                profileService.deleteProfile(userId);

                ApiResponse<String> response = ApiResponse.<String>builder()
                                .success(true)
                                .message("Profile deleted successfully")
                                .data("Profile deleted")
                                .statusCode(HttpStatus.OK.value())
                                .build();

                return ResponseEntity.ok(response);
        }
}