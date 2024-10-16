package com.example.TAX.EXEMPTION.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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


    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<Application>applications;


   @ManyToOne
    @JoinColumn(name = "roleId",nullable = true)
    private  Role role;



    @ManyToOne
    @JoinColumn(name = "gender_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Gender gender;



    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<Comment>comments;


    public User() {
    }


    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                // Avoid including role and gender to prevent recursion
                '}';
    }


}
