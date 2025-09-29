package com.example.TroubleTalks.repository;

import com.example.TroubleTalks.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    // Custom method to fetch all posts ordered by creation date (newest first)
    List<Post> findAllByOrderByPostedAtDesc();
}