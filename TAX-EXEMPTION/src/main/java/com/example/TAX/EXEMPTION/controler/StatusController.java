package com.example.TAX.EXEMPTION.controler;


import com.example.TAX.EXEMPTION.model.Status;
import com.example.TAX.EXEMPTION.repo.StatusRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/status")
public class StatusController {

    @Autowired
    private StatusRepo statusRepo;
    // Create a new status
    @PostMapping("/create")
    public Status createStatus(@RequestBody Status status) {
        return statusRepo.save(status);
    }

    // Get all statuses
    @GetMapping("/all")
    public List<Status> getAllStatuses() {
        return statusRepo.findAll();
    }

    // Get status by ID
    @GetMapping("/{id}")
    public ResponseEntity<Status> getStatusById(@PathVariable Long id) {
        Optional<Status> status = statusRepo.findById(id);
        return status.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update status
    @PutMapping("/update/{id}")
    public ResponseEntity<Status> updateStatus(@PathVariable Long id, @RequestBody Status statusDetails) {
        Optional<Status> statusOptional = statusRepo.findById(id);

        if (statusOptional.isPresent()) {
            Status status = statusOptional.get();
            status.setStatusName(statusDetails.getStatusName());
            Status updatedStatus = statusRepo.save(status);
            return ResponseEntity.ok(updatedStatus);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete status by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable Long id) {
        if (statusRepo.existsById(id)) {
            statusRepo.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
