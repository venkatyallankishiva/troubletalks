package com.example.TroubleTalks.repository;

import com.example.TroubleTalks.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Fetch all comments for a specific post, ordered by date
    List<Comment> findByPostIdOrderByCommentedAtAsc(Long postId);

    // Custom query to count comments for a single post
    long countByPostId(Long postId);

    // Custom query to fetch comment counts for a list of post IDs
    @Query("SELECT c.post.id, COUNT(c) FROM Comment c WHERE c.post.id IN :postIds GROUP BY c.post.id")
    List<Object[]> countCommentsByPostIdIn(List<Long> postIds);
}