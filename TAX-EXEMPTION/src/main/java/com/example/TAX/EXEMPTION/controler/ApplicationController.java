package com.example.TAX.EXEMPTION.controler;

import com.example.TAX.EXEMPTION.model.*;
import com.example.TAX.EXEMPTION.repo.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/application")
public class ApplicationController {
    @Autowired
    private CommentRepo commentRepository;
    @Autowired
    private AssuranceRepo assuranceRepository;
    @Autowired
    private ApplicationRepo applicationRepo;
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private StatusRepo statusRepo;

    private final String UPLOAD_DIR = "E:\\TAX EXEPTION\\TAX-EXEMPTION\\DOCUMENT\\";

    @PostMapping
    public ResponseEntity<Application> createApplication(
            @RequestParam("userId") Long userId,
            @RequestParam("doc1") MultipartFile doc1,
            @RequestParam("doc2") MultipartFile doc2,
            @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

        Application application = new Application();
        application.setUser(new User(userId));  // Assuming User class has a constructor that accepts userId
        application.setSubmissionDate(new Date());

        // Automatically setting status to 'Pending'
        Status pendingStatus = statusRepo.findByStatusName("Pending");
        if (pendingStatus == null) {
            // If 'Pending' status doesn't exist in DB, create it
            pendingStatus = new Status();
            pendingStatus.setStatusName("Pending");
            pendingStatus = statusRepo.save(pendingStatus);
        }
        application.setStatus(pendingStatus);

        // Save files
        saveFile(doc1, application, "Doc1");
        saveFile(doc2, application, "Doc2");
        saveImageFile(imageFile, application);

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
    @PutMapping("/{ApplicationId}")
    public ResponseEntity<Application> updateApplication(
            @PathVariable Long ApplicationId,
            @RequestParam(value = "doc1", required = false) MultipartFile doc1,
            @RequestParam(value = "doc2", required = false) MultipartFile doc2,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam("userId") Long userId,
            @RequestParam("statusId") Long statusId) throws IOException {

        Application existingApplication = applicationRepo.findById(ApplicationId).orElse(null);
        if (existingApplication != null) {
            // Automatically set the current date and time
            existingApplication.setSubmissionDate(new Date());

            existingApplication.setUser(new User(userId));  // Assuming User class has a constructor that accepts userId
            existingApplication.setStatus(new Status(statusId));  // Assuming Status class has a constructor that accepts statusId

            // Handle doc1 update
            if (doc1 != null && !doc1.isEmpty()) {
                saveFile(doc1, existingApplication, "Doc1");
            }

            // Handle doc2 update
            if (doc2 != null && !doc2.isEmpty()) {
                saveFile(doc2, existingApplication, "Doc2");
            }

            // Handle image update
            if (imageFile != null && !imageFile.isEmpty()) {
                saveImageFile(imageFile, existingApplication);
            }

            Application updatedApplication = applicationRepo.save(existingApplication);
            return ResponseEntity.ok(updatedApplication);
        }
        return ResponseEntity.notFound().build();
    }
    @DeleteMapping("/delete/{ApplicationId}")
    @Transactional
    public ResponseEntity<String> deleteApplication(@PathVariable Long ApplicationId) {
        // Fetch the Application by its ID
        Application application = applicationRepo.findById(ApplicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));

        // Delete associated files (ensure it's successful before proceeding)
        try {
            deleteApplicationFiles(application);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error deleting associated files: " + e.getMessage());
        }

        // First, explicitly delete comments associated with the application
        List<Comment> comments = application.getComments();
        if (comments != null && !comments.isEmpty()) {
            for (Comment comment : comments) {
                comment.setApplication(null);  // Unlink the comment from the application
                commentRepository.delete(comment);  // Explicitly delete the comment
            }
            application.getComments().clear();  // Ensure the collection is cleared
        }

        // Delete associated assurance (if any) before proceeding with the Application deletion
        Assurance assurance = application.getAssurance();
        if (assurance != null) {
            assurance.setApplication(null);  // Unlink assurance from the application
            assuranceRepository.delete(assurance);  // Explicitly delete the assurance
        }

        // Unlink the Application from the User
        User user = application.getUser();
        if (user != null) {
            user.getApplications().remove(application);  // Remove the application from the user's list
            application.setUser(null);  // Unlink the user from the application
            userRepo.save(user);  // Save the updated user
        }

        // Unlink the Application from Status
        Status status = application.getStatus();
        if (status != null) {
            status.getApplications().remove(application);  // Remove the application from the status
            application.setStatus(null);  // Unlink the status from the application
            statusRepo.save(status);  // Save the updated status
        }

        // Finally, delete the Application itself
        applicationRepo.delete(application);  // This should now delete the application

        return ResponseEntity.ok("Application and its associated data deleted successfully.");
    }



    // Helper method to delete the application files
    private void deleteApplicationFiles(Application application) throws IOException {
        // Example for deleting documents and images
        if (application.getDoc1() != null) {
            Files.deleteIfExists(Paths.get(UPLOAD_DIR + application.getDoc1()));
        }
        if (application.getDoc2() != null) {
            Files.deleteIfExists(Paths.get(UPLOAD_DIR + application.getDoc2()));
        }
        if (application.getImage() != null) {
            Files.deleteIfExists(Paths.get(UPLOAD_DIR + application.getImage()));
        }
    }



    // Method to download a single file (doc1, doc2, or image) by applicationId and file type

    @GetMapping("/{ApplicationId}/download")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long ApplicationId,
            @RequestParam("fileType") String fileType) {

        // Find the application by ID
        Application application = applicationRepo.findById(ApplicationId).orElse(null);
        if (application == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Update the status to "On Progress"
        Status inProgressStatus = statusRepo.findByStatusName("In Progress");
        if (inProgressStatus == null) {
            // If "On Progress" status doesn't exist in DB, create it
            inProgressStatus = new Status();
            inProgressStatus.setStatusName("In Progress");
            inProgressStatus = statusRepo.save(inProgressStatus);
        }
        application.setStatus(inProgressStatus);
        applicationRepo.save(application); // Save the updated application status

        // Determine the file name based on the fileType
        String fileName;
        switch (fileType.toLowerCase()) {
            case "doc1":
                fileName = application.getDoc1();
                break;
            case "doc2":
                fileName = application.getDoc2();
                break;
            case "image":
                fileName = application.getImage();
                break;
            default:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // If the file name is not present, return a not found response
        if (fileName == null || fileName.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Get the file path
        File file = new File(UPLOAD_DIR + fileName);
        if (!file.exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Create a resource for the file
        Resource resource = new FileSystemResource(file);

        try {
            // Return the file as a downloadable response
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

    private void saveImageFile(MultipartFile file, Application application) throws IOException {
        if (file != null && !file.isEmpty()) {
            // Validate that the file is an image
            String fileType = file.getContentType();
            if (!fileType.startsWith("image/")) {
                throw new IOException("Only image files are allowed.");
            }

            // Save the file
            String fileName = file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.createDirectories(path.getParent());
            file.transferTo(path);

            // Set the file name in the application entity
            application.setImage(fileName);
        }
    }


    @DeleteMapping("/delete/{applicationId}/{fileName:.+}")
    public ResponseEntity<String> deleteFile(@PathVariable Long applicationId, @PathVariable String fileName) {
        try {
            // Construct the file path
            Path filePath = Paths.get(UPLOAD_DIR + fileName);

            // Check if the file exists
            if (Files.exists(filePath)) {
                // Delete the file
                Files.delete(filePath);

                // Retrieve the application from the database
                Optional<Application> optionalApplication = applicationRepo.findById(applicationId);
                if (optionalApplication.isPresent()) {
                    Application application = optionalApplication.get();

                    // Clear the corresponding field based on the filename
                    if (fileName.equals(application.getDoc1())) {
                        application.setDoc1(null);
                    } else if (fileName.equals(application.getDoc2())) {
                        application.setDoc2(null);
                    } else if (fileName.equals(application.getImage())) {
                        application.setImage(null);
                    }

                    // Save the updated application
                    applicationRepo.save(application);

                    return ResponseEntity.ok("File deleted successfully: " + fileName);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Application not found: " + applicationId);
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found: " + fileName);
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting file: " + e.getMessage());
        }
    }




    @PutMapping("/{ApplicationId}/approve")
    public String approveApplication(@PathVariable Long ApplicationId) {
        // Fetch the application
        Optional<Application> applicationOpt = applicationRepo.findById(ApplicationId);
        if (applicationOpt.isEmpty()) {
            return "Application not found!";
        }

        Application application = applicationOpt.get();

        // Fetch the "Done" status
        Status doneStatus = statusRepo.findByStatusName("Done");
        if (doneStatus == null) {
            return "'Done' status not found!";
        }

        // Update the application's status
        application.setStatus(doneStatus);
        applicationRepo.save(application);

        return "Application approved successfully!";
    }




//    @PutMapping("/update/{applicationId}/{fileType}")
//    public ResponseEntity<Application> updateFile(
//            @PathVariable Long applicationId,
//            @PathVariable String fileType,
//            @RequestParam("file") MultipartFile newFile) {
//        try {
//            // Retrieve the application from the database
//            Optional<Application> optionalApplication = applicationRepo.findById(applicationId);
//            if (!optionalApplication.isPresent()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//            }
//
//            Application application = optionalApplication.get();
//            String oldFileName = null;
//
//            // Determine which file to update and get the old file name
//            if (fileType.equalsIgnoreCase("doc1")) {
//                oldFileName = application.getDoc1();
//            } else if (fileType.equalsIgnoreCase("doc2")) {
//                oldFileName = application.getDoc2();
//            } else if (fileType.equalsIgnoreCase("imageFile")) {
//                oldFileName = application.getImage();
//            } else {
//                return ResponseEntity.badRequest().body(null); // Invalid file type
//            }
//
//            // Delete the old file if it exists
//            if (oldFileName != null) {
//                Path oldFilePath = Paths.get(UPLOAD_DIR + oldFileName);
//                if (Files.exists(oldFilePath)) {
//                    Files.delete(oldFilePath);
//                }
//            }
//
//            // Save the new file
//            saveFile(newFile, application, fileType);
//
//            // Save the updated application to the database
//            Application updatedApplication = applicationRepo.save(application);
//
//            return ResponseEntity.ok(updatedApplication);
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(null);
//        }
//    }





}
