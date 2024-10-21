package com.example.TAX.EXEMPTION.controler;

import com.example.TAX.EXEMPTION.model.GeneralNotice;
import com.example.TAX.EXEMPTION.repo.GeneralNoticeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/generalnotice")
public class GeneralNoticeController {
    private static final String UPLOAD_DIR = "E:/TAX EXEPTION/TAX-EXEMPTION/GENARALNOTICE/";

    @Autowired
    private GeneralNoticeRepo generalNoticeRepository;

    @PostMapping("/upload-notice")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Create the directory if it does not exist
            Path directoryPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            // Save the file to the specified directory
            String fileName = file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            Files.write(filePath, file.getBytes());

            // Create and save the GeneralNotice entity
            GeneralNotice generalNotice = new GeneralNotice();
            generalNotice.setFileName(fileName); // Store only the file name
            generalNotice.setSubmissionDate(new Date()); // Set submission date automatically

            // Save the entity in the database
            generalNoticeRepository.save(generalNotice);

            return ResponseEntity.ok("File uploaded successfully: " + generalNotice.getFileName());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload file: " + e.getMessage());
        }
    }


    @GetMapping("/list-notices")
    public ResponseEntity<List<GeneralNotice>> listAllNotices() {
        List<GeneralNotice> notices = generalNoticeRepository.findAll();
        return ResponseEntity.ok(notices);
    }


    @GetMapping("/download-notice/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        Optional<GeneralNotice> noticeOpt = generalNoticeRepository.findById(id);

        if (noticeOpt.isPresent()) {
            GeneralNotice notice = noticeOpt.get();
            String fileName = notice.getFileName();
            Path filePath = Paths.get(UPLOAD_DIR + fileName);

            try {
                Resource resource = new UrlResource(filePath.toUri());

                if (resource.exists() || resource.isReadable()) {
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                            .body(resource);
                } else {
                    return ResponseEntity.status(404).body(null);
                }
            } catch (Exception e) {
                return ResponseEntity.status(500).body(null);
            }
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }




}
