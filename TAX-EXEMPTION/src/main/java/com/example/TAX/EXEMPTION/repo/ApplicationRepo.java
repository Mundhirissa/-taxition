package com.example.TAX.EXEMPTION.repo;

import com.example.TAX.EXEMPTION.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepo extends JpaRepository<Application,Long> {
}
