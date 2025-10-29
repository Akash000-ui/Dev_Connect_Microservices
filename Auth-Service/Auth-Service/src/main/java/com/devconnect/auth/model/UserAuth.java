package com.devconnect.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "userAuth")
public class UserAuth {
    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    @Indexed(unique = true)
    private String username;

    private String password;
    @Builder.Default
    private boolean verified = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Builder.Default
    private com.devconnect.auth.model.AuthProvider authProvider = com.devconnect.auth.model.AuthProvider.LOCAL;
    @Builder.Default
    private Set<UserRole> roles = new HashSet<>();

}
