package com.example.TAX.EXEMPTION.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    private Date commentDate;
    private String text;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "UserId")
    private User user;


    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "ApplicationId")
    private Application application;
}
