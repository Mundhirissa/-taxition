package com.example.TAX.EXEMPTION.repo;

import com.example.TAX.EXEMPTION.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepo extends JpaRepository<Status,Long> {
    Status findByStatusName(String statusName);
}
