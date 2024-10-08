package com.example.TAX.EXEMPTION.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

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


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application>applications;


   @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "roleId")
    private  Role role;



    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "genderId")
    private Gender gender;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "ApplicationId")
    private Application application;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment>comments;


    public User() {
    }
}
