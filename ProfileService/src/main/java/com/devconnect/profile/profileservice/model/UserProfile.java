package com.devconnect.profile.profileservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "userProfile")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    @Id
    @Indexed(unique = true)
    private String userId; // References the user ID from Auth-Service

    @Indexed(unique = true)
    private String email;

    private String username;
    private String firstName;
    private String lastName;
    private String about;
    private String profilePicUrl;
    private String location;
    private String website;
    private String phone;

    private List<String> skills;
    private List<Education> education;
    private List<Experience> experience;
    private List<Project> projects;
    private List<String> interests;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Education {
        private String institution;
        private String degree;
        private String fieldOfStudy;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Experience {
        private String company;
        private String position;
        private String location;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String description;
        private boolean currentJob;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Project {
        private String name;
        private String description;
        private String techStack;
        private String githubUrl;
        private String liveUrl;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
    }
}
