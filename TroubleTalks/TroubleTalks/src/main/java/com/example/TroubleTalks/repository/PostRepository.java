package com.example.TroubleTalks.repository;

import com.example.TroubleTalks.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByPostedAtDesc();

    @Query("SELECT p FROM Post p WHERE " +
            "LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.journey) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.author.username) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "ORDER BY p.postedAt DESC")
    List<Post> searchPosts(@Param("query") String query);
}