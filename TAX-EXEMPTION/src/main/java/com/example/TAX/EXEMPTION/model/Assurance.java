package com.example.TAX.EXEMPTION.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Assurance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assuranceId;
    private String assuranceFile;
    private String recommendation;



    @OneToOne
    @JoinColumn(name = "ApplicationId", nullable = false) // Foreign key column in Assurance
    private Application application;

    @Override
    public String toString() {
        return "Assurance{" +
                "assuranceId=" + assuranceId +
                ", assuranceFile='" + assuranceFile + '\'' +
                ", recommendation='" + recommendation + '\'' +
                '}';
    }
}
