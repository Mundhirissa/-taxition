package com.example.TAX.EXEMPTION.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Gender {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long genderId;
    public  String genderType;

    // Bidirectional relationship to User
    @OneToMany(mappedBy = "gender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users;
}
