package com.example.TAX.EXEMPTION.model;


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

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "UserId")
    private User user;


    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "StatusId")
    private  Status status;



    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment>comments;




}
