package com.example.TAX.EXEMPTION.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Entity
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long  ApplicationId;
    private Date SubmissionDate;
    private String doc1;
    private String doc2;
    private String image;

    @ManyToOne
    @JoinColumn(name = "UserId")
    private User user;


    @ManyToOne
    @JoinColumn(name = "StatusId")
    private  Status status;


    @JsonIgnore
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment>comments;

    @JsonIgnore
    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL,orphanRemoval = true) // Maps to the assurance field in Assurance
    private Assurance assurance;



    @Override
    public String toString() {
        return "Application{" +
                "ApplicationId=" + ApplicationId +
                ", SubmissionDate=" + SubmissionDate +
                ", doc1='" + doc1 + '\'' +
                ", doc2='" + doc2 + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
