package com.example.TAX.EXEMPTION.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long StatusId;

    public Status(Long statusId) {
        StatusId = statusId;
    }

    private  String statusName;

    public Status() {
    }

}
