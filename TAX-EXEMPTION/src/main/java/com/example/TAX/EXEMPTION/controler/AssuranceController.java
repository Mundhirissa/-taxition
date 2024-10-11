package com.example.TAX.EXEMPTION.controler;


import com.example.TAX.EXEMPTION.model.Application;
import com.example.TAX.EXEMPTION.model.Assurance;
import com.example.TAX.EXEMPTION.model.Status;
import com.example.TAX.EXEMPTION.repo.ApplicationRepo;
import com.example.TAX.EXEMPTION.repo.AssuranceRepo;
import com.example.TAX.EXEMPTION.repo.StatusRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/assurance")
public class AssuranceController {
@Autowired
private StatusRepo statusRepo;
    @Autowired
    private AssuranceRepo assuranceRepository;

    @Autowired
    private ApplicationRepo applicationRepository;

    private static final String FILE_DIRECTORY = "E:\\TAX EXEPTION\\TAX-EXEMPTION\\ASSURANCE\\";

    // Create (POST)
    @PostMapping
    public ResponseEntity<Assurance> createAssurance(
            @RequestParam("recommendation") String recommendation,
            @RequestParam("file") MultipartFile file,
            @RequestParam("applicationId") Long ApplicationId) throws IOException {

        // Save the file
        String fileName = saveFile(file);

        // Create Assurance object
        Assurance assurance = new Assurance();
        assurance.setRecommendation(recommendation);
        assurance.setAssuranceFile(fileName);

        // Set application if exists
        Optional<Application> applicationOptional = applicationRepository.findById(ApplicationId);
        if (applicationOptional.isPresent()) {
            Application application = applicationOptional.get();
            assurance.setApplication(application);

            // Update the application status to 'Done'
            Optional<Status> statusOptional = Optional.ofNullable(statusRepo.findByStatusName("Done"));
            if (statusOptional.isPresent()) {
                Status doneStatus = statusOptional.get();
                application.setStatus(doneStatus);
                applicationRepository.save(application);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Status "Done" not found
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Application not found
        }

        // Save assurance in repository
        Assurance createdAssurance = assuranceRepository.save(assurance);
        return new ResponseEntity<>(createdAssurance, HttpStatus.CREATED);
    }


    // Read (GET by ID)
    @GetMapping("/{assuranceId}")
    public ResponseEntity<Assurance> getAssuranceById(@PathVariable("assuranceId") Long assuranceId) {
        Optional<Assurance> assurance = assuranceRepository.findById(assuranceId);
        return assurance.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Read (GET all)
    @GetMapping
    public ResponseEntity<List<Assurance>> getAllAssurances() {
        List<Assurance> assurances = assuranceRepository.findAll();
        return ResponseEntity.ok(assurances);
    }

    // Update (PUT)
    // Update (PUT)
    // Update (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<Assurance> updateAssurance(
            @PathVariable("id") Long assuranceId,
            @RequestParam("recommendation") String recommendation,
            @RequestParam("file") MultipartFile file,
            @RequestParam("applicationId") Long applicationId) throws IOException {

        // Find the existing assurance by ID
        Optional<Assurance> existingAssuranceOptional = assuranceRepository.findById(assuranceId);
        if (!existingAssuranceOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Assurance not found
        }
        Assurance existingAssurance = existingAssuranceOptional.get();

        // Update the assurance details
        existingAssurance.setRecommendation(recommendation);

        // Update the assurance file if a new one is provided
        if (file != null && !file.isEmpty()) {
            String newFileName = saveFile(file); // Save the new file
            existingAssurance.setAssuranceFile(newFileName);
        }

        // Find and update the application if needed
        Optional<Application> applicationOptional = applicationRepository.findById(applicationId);
        if (applicationOptional.isPresent()) {
            Application application = applicationOptional.get();
            existingAssurance.setApplication(application);

            // Update the application status to 'Done' if necessary
            Optional<Status> statusOptional = Optional.ofNullable(statusRepo.findByStatusName("Done"));
            if (statusOptional.isPresent()) {
                Status doneStatus = statusOptional.get();
                application.setStatus(doneStatus);
                applicationRepository.save(application);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Status "Done" not found
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Application not found
        }

        // Save the updated assurance
        Assurance updatedAssurance = assuranceRepository.save(existingAssurance);
        return new ResponseEntity<>(updatedAssurance, HttpStatus.OK);
    }


    // Delete (DELETE)
    @DeleteMapping("/{assuranceId}")
    public ResponseEntity<Void> deleteAssurance(@PathVariable("assuranceId") Long assuranceId) {
        Optional<Assurance> assurance = assuranceRepository.findById(assuranceId);
        if (assurance.isPresent()) {
            assuranceRepository.deleteById(assuranceId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Helper method to save file
    private String saveFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        File dest = new File(FILE_DIRECTORY + "/" + fileName);
        file.transferTo(dest);
        return fileName;
    }


    // Method to download assurance file by assuranceId
    @GetMapping("/download/{assuranceId}")
    public ResponseEntity<Resource> downloadAssuranceFile(@PathVariable Long assuranceId) {
        // Fetch the Assurance object from the repository
        Optional<Assurance> assuranceOptional = assuranceRepository.findById(assuranceId);
        if (assuranceOptional.isPresent()) {
            Assurance assurance = assuranceOptional.get();
            String fileName = assurance.getAssuranceFile();
            // Construct the full file path using the FILE_DIRECTORY
            File file = new File(FILE_DIRECTORY + fileName);
            if (file.exists()) {
                Resource resource = new FileSystemResource(file);
                return ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
                        .body(resource);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // File not found
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Assurance not found
        }
    }


}
