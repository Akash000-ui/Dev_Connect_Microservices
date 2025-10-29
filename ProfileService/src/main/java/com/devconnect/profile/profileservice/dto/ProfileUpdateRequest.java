package com.devconnect.profile.profileservice.dto;

import com.devconnect.profile.profileservice.model.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Size;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {

    @Size(max = 50, message = "Username must not exceed 50 characters")
    private String username;

    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @Size(max = 500, message = "About section must not exceed 500 characters")
    private String about;

    private String profilePicUrl;

    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location;

    private String website;

    @Size(max = 15, message = "Phone must not exceed 15 characters")
    private String phone;

    private List<String> skills;
    private List<UserProfile.Education> education;
    private List<UserProfile.Experience> experience;
    private List<UserProfile.Project> projects;
    private List<String> interests;
}