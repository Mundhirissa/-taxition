package com.example.TAX.EXEMPTION.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long CommentId;
    private Date CommentDate;
    private String Text;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "UserId")
    private User user;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ApplicationId")
    private Application application;
}
