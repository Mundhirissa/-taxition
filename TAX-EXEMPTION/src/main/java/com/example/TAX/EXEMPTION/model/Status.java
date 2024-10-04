package com.example.TAX.EXEMPTION.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long StatusId;


    @OneToMany(mappedBy = "status", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application>applications;


    public Status(Long statusId) {
        StatusId = statusId;
    }

    private  String statusName;

    public Status() {
    }

}
