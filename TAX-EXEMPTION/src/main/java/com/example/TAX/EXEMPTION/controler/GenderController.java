package com.example.TAX.EXEMPTION.controler;

import com.example.TAX.EXEMPTION.model.Gender;
import com.example.TAX.EXEMPTION.repo.GenderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/api/gender")
public class GenderController {

    @Autowired
    private GenderRepo genderRepo;
    // CREATE a new gender
    @PostMapping
    public ResponseEntity<Gender> createGender(@RequestBody Gender gender) {
        Gender savedGender = genderRepo.save(gender);
        return ResponseEntity.ok(savedGender);
    }

    // READ all genders
    @GetMapping
    public ResponseEntity<List<Gender>> getAllGenders() {
        List<Gender> genders = genderRepo.findAll();
        return ResponseEntity.ok(genders);
    }

    // READ a specific gender by ID
    @GetMapping("/{GenderId}")
    public ResponseEntity<Gender> getGenderById(@PathVariable Long GenderId) {
        Optional<Gender> gender = genderRepo.findById(GenderId);
        if (gender.isPresent()) {
            return ResponseEntity.ok(gender.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // UPDATE a gender
    @PutMapping("/{GenderId}")
    public ResponseEntity<Gender> updateGender(@PathVariable Long GenderId, @RequestBody Gender updatedGender) {
        return genderRepo.findById(GenderId)
                .map(gender -> {
                    gender.setGenderType(updatedGender.getGenderType());
                    Gender savedGender = genderRepo.save(gender);
                    return ResponseEntity.ok(savedGender);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE a gender
    @DeleteMapping("/{GenderId}")
    public ResponseEntity<Void> deleteGender(@PathVariable Long GenderId) {
        if (genderRepo.existsById(GenderId)) {
            genderRepo.deleteById(GenderId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }



}
