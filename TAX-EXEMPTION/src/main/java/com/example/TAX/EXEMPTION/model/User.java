package com.example.TAX.EXEMPTION.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String firstName;  // Changed from fName to firstName
    private String middleName; // Changed from mName to middleName
    private String lastName;

    @Column(unique = true)  // Ensure email is unique
    private String email;

    public User(Long userId) {
        this.userId = userId;
    }

    private String address;

    @Column(unique = true)  // Ensure username is unique
    private String username;

    private String password;
    private String phoneNumber;
    private String employeeId;

   @ManyToOne
    @JoinColumn(name = "roleId")
    private  Role role;



    @ManyToOne
    @JoinColumn(name = "genderId")
    private Gender gender;



}
