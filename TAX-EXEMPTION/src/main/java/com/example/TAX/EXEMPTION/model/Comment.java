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



    @ManyToOne
    @JoinColumn(name = "UserId")
    private User user;


    @ManyToOne
    @JoinColumn(name = "ApplicationId")
    private Application application;

    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", commentDate=" + commentDate +
                ", text='" + text + '\'' +
                '}';
    }


}
