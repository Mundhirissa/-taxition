package com.example.TAX.EXEMPTION.repo;

import com.example.TAX.EXEMPTION.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository< User,Long> {
    User findByUsername(String username);  // Find user by username
    User findByEmail(String email);
}
