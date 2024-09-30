package com.example.TAX.EXEMPTION.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long  ApplicationId;
    private Date SubmissionDate;
    private String Doc1;
    private String Doc2;

    @ManyToOne
    @JoinColumn(name = "UserId")
    private User user;


    @ManyToOne
    @JoinColumn(name = "StatusId")
    private  Status status;


}
