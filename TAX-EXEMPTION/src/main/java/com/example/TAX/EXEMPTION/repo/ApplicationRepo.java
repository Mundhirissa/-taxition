package com.example.TAX.EXEMPTION.repo;

import com.example.TAX.EXEMPTION.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationRepo extends JpaRepository<Application,Long> {
    // In ApplicationRepo.java
    Optional<Application> findByDoc1OrDoc2OrImage(String doc1, String doc2, String image);


}
