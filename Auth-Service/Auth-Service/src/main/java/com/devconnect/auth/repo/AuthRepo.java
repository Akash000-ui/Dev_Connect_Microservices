package com.devconnect.auth.repo;

import com.devconnect.auth.model.UserAuth;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepo extends MongoRepository<UserAuth , String> {
    boolean existsByEmail(String email);
    UserAuth findByEmail(String email);
}

