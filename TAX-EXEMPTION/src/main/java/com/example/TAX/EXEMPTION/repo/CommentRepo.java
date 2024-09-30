package com.example.TAX.EXEMPTION.repo;

import com.example.TAX.EXEMPTION.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepo extends JpaRepository<Comment,Long> {
}
