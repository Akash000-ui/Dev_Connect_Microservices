package com.devconnect.profile.profileservice.service;

import com.devconnect.profile.profileservice.dto.ProfileUpdateRequest;
import com.devconnect.profile.profileservice.dto.UserRegisteredEvent;
import com.devconnect.profile.profileservice.model.UserProfile;
import com.devconnect.profile.profileservice.repo.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final UserProfileRepository profileRepository;

    /**
     * Creates a default profile when a user registers
     */
    public void createDefaultProfile(UserRegisteredEvent event) {
        try {
            // Check if profile already exists
            if (profileRepository.existsByUserId(event.getUserId())) {
                log.warn("Profile already exists for userId: {}", event.getUserId());
                return;
            }

            // Extract first name and last name from the full name
            String[] nameParts = event.getName() != null ? event.getName().split(" ", 2) : new String[] { "", "" };
            String firstName = nameParts.length > 0 ? nameParts[0] : "";
            String lastName = nameParts.length > 1 ? nameParts[1] : "";

            UserProfile defaultProfile = UserProfile.builder()
                    .userId(event.getUserId())
                    .email(event.getEmail())
                    .username(generateUsername(event.getEmail()))
                    .firstName(firstName)
                    .lastName(lastName)
                    .about("Welcome to Dev-Connect! Tell us about yourself.")
                    .profilePicUrl("")
                    .location("")
                    .website("")
                    .phone("")
                    .skills(new ArrayList<>())
                    .education(new ArrayList<>())
                    .experience(new ArrayList<>())
                    .projects(new ArrayList<>())
                    .interests(new ArrayList<>())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            UserProfile savedProfile = profileRepository.save(defaultProfile);
            log.info("✅ Created default profile for user: {}",
                    event.getUserId());

        } catch (Exception e) {
            log.error("❌ Failed to create default profile for user: {}. Error: {}",
                    event.getUserId(), e.getMessage(), e);
            throw new RuntimeException("Failed to create default profile", e);
        }
    }

    /**
     * Get profile by userId
     */
    public Optional<UserProfile> getProfileByUserId(String userId) {
        return profileRepository.findByUserId(userId);
    }

    /**
     * Update user profile
     */
    public UserProfile updateProfile(String userId, ProfileUpdateRequest request) {
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for userId: " + userId));

        // Update only non-null fields
        if (request.getUsername() != null) {
            profile.setUsername(request.getUsername());
        }
        if (request.getFirstName() != null) {
            profile.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            profile.setLastName(request.getLastName());
        }
        if (request.getAbout() != null) {
            profile.setAbout(request.getAbout());
        }
        if (request.getProfilePicUrl() != null) {
            profile.setProfilePicUrl(request.getProfilePicUrl());
        }
        if (request.getLocation() != null) {
            profile.setLocation(request.getLocation());
        }
        if (request.getWebsite() != null) {
            profile.setWebsite(request.getWebsite());
        }
        if (request.getPhone() != null) {
            profile.setPhone(request.getPhone());
        }
        if (request.getSkills() != null) {
            profile.setSkills(request.getSkills());
        }
        if (request.getEducation() != null) {
            profile.setEducation(request.getEducation());
        }
        if (request.getExperience() != null) {
            profile.setExperience(request.getExperience());
        }
        if (request.getProjects() != null) {
            profile.setProjects(request.getProjects());
        }
        if (request.getInterests() != null) {
            profile.setInterests(request.getInterests());
        }

        profile.setUpdatedAt(LocalDateTime.now());

        UserProfile updatedProfile = profileRepository.save(profile);
        log.info("✅ Updated profile for userId: {}", userId);

        return updatedProfile;
    }

    /**
     * Delete profile
     */
    public void deleteProfile(String userId) {
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for userId: " + userId));

        profileRepository.delete(profile);
        log.info("✅ Deleted profile for userId: {}", userId);
    }

    /**
     * Generate username from email
     */
    private String generateUsername(String email) {
        if (email == null || !email.contains("@")) {
            return "user" + System.currentTimeMillis();
        }

        String baseUsername = email.substring(0, email.indexOf("@"));
        baseUsername = baseUsername.replaceAll("[^a-zA-Z0-9]", "");

        // Check if username already exists and append number if needed
        String username = baseUsername;
        int counter = 1;
        while (profileRepository.findByUserId(username).isPresent()) {
            username = baseUsername + counter;
            counter++;
        }

        return username.toLowerCase();
    }
}