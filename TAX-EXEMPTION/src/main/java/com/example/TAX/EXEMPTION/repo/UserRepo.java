package com.example.TAX.EXEMPTION.repo;

import com.example.TAX.EXEMPTION.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepo extends JpaRepository< User,Long> {
    User findByUsername(String username);  // Find user by username
    User findByEmail(String email);
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.gender = null WHERE u.gender.genderId = :genderId")
    void updateUsersWithNullGender(@Param("genderId") Long genderId);
}
