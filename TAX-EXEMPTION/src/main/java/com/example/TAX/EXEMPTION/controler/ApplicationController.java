package com.example.TAX.EXEMPTION.controler;

import com.example.TAX.EXEMPTION.model.Application;
import com.example.TAX.EXEMPTION.model.Status;
import com.example.TAX.EXEMPTION.model.User;
import com.example.TAX.EXEMPTION.repo.ApplicationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/application")
public class ApplicationController {
    @Autowired
    private ApplicationRepo applicationRepo;

    private final String UPLOAD_DIR = "E:\\TAX EXEPTION\\TAX-EXEMPTION\\DOCUMENT\\";

    @PostMapping
    public ResponseEntity<Application> createApplication(
            @RequestParam("userId") Long userId,
            @RequestParam("statusId") Long statusId,
            @RequestParam("doc1") MultipartFile doc1,
            @RequestParam("doc2") MultipartFile doc2) throws IOException {

        Application application = new Application();
        application.setUser(new User(userId));  // Assuming User class has a constructor that accepts userId
        application.setStatus(new Status(statusId));  // Assuming Status class has a constructor that accepts statusId
        application.setSubmissionDate(new Date());

        saveFile(doc1, application, "Doc1");
        saveFile(doc2, application, "Doc2");

        Application createdApplication = applicationRepo.save(application);
        return ResponseEntity.created(URI.create("/applications/" + createdApplication.getApplicationId())).body(createdApplication);
    }

    @GetMapping
    public List<Application> getAllApplications() {
        return applicationRepo.findAll();
    }

    @GetMapping("/{id}")
    public Application getApplicationById(@PathVariable Long id) {
        return applicationRepo.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Application> updateApplication(@PathVariable Long id, @RequestBody Application application) {
        Application existingApplication = applicationRepo.findById(id).orElse(null);
        if (existingApplication != null) {
            existingApplication.setSubmissionDate(application.getSubmissionDate());
            existingApplication.setDoc1(application.getDoc1());
            existingApplication.setDoc2(application.getDoc2());
            existingApplication.setUser(application.getUser());
            existingApplication.setStatus(application.getStatus());
            Application updatedApplication = applicationRepo.save(existingApplication);
            return ResponseEntity.ok(updatedApplication);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        applicationRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<File> downloadFile(@PathVariable String fileName) {
        File file = new File(UPLOAD_DIR + fileName);
        return ResponseEntity.ok().body(file);
    }

    private void saveFile(MultipartFile file, Application application, String docField) throws IOException {
        if (file != null && !file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.createDirectories(path.getParent());
            file.transferTo(path);
            if (docField.equals("Doc1")) {
                application.setDoc1(fileName);
            } else if (docField.equals("Doc2")) {
                application.setDoc2(fileName);
            }
        }
    }

}
