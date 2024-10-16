package com.example.TAX.EXEMPTION.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore // Bidirectional relationship to User
    @OneToMany(mappedBy = "gender", cascade = CascadeType.PERSIST)
    private List<User> users;

    @Override
    public String toString() {
        return "Gender{" +
                "genderId=" + genderId +
                ", genderType='" + genderType + '\'' +
                '}';
    }
}
