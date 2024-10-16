package com.example.TAX.EXEMPTION.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;
    private String roleName;

    @JsonIgnore // Bidirectional relationship to User
    @OneToMany(mappedBy = "role", cascade = CascadeType.PERSIST)
    private List<User> users;


    @Override
    public String toString() {
        return "Role{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                '}';
    }




}
