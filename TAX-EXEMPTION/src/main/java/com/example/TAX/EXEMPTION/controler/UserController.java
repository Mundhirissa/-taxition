package com.example.TAX.EXEMPTION.controler;

import com.example.TAX.EXEMPTION.DTO.LoginRequest;
import com.example.TAX.EXEMPTION.model.User;
import com.example.TAX.EXEMPTION.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepo userRepo;
@Autowired
private PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        // Check if the username already exists
        if (userRepo.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username '" + user.getUsername() + "' already exists."); // Provide message
        }

        // Check if the email already exists
        if (userRepo.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email '" + user.getEmail() + "' already exists."); // Provide message
        }

        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save the user to the repository
        User savedUser = userRepo.save(user);

        // Return the saved user with a success message
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User created successfully: " + savedUser); // Provide success message
    }

    // READ all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepo.findAll();
        return ResponseEntity.ok(users);
    }



    // READ a specific user by ID
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        Optional<User> user = userRepo.findById(userId);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // UPDATE a user
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody User updatedUser) {
        return userRepo.findById(userId)
                .map(user -> {
                   user.setFirstName(updatedUser.getFirstName());
                   user.setLastName(updatedUser.getLastName());
                   user.setMiddleName(updatedUser.getMiddleName());
                    user.setEmail(updatedUser.getEmail());
                    user.setAddress(updatedUser.getAddress());
                    user.setUsername(updatedUser.getUsername());
                    user.setPassword(updatedUser.getPassword());
                    user.setPhoneNumber(updatedUser.getPhoneNumber());
                    user.setEmployeeId(updatedUser.getEmployeeId());
                    user.setRole(updatedUser.getRole());
                    user.setGender(updatedUser.getGender());
                    User savedUser = userRepo.save(user);
                    return ResponseEntity.ok(savedUser);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE a user
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        if (userRepo.existsById(userId)) {
            userRepo.deleteById(userId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }




    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestBody LoginRequest loginRequest) {
        // Retrieve the user by username
        User user = userRepo.findByUsername(loginRequest.getUsername());

        // Check if the user exists and verify the password
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password.");
        }

        // Check if the provided password matches the stored password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password.");
        }

        // Successful login
        return ResponseEntity.ok("Login successful for user: " + user.getUsername());
    }






}
