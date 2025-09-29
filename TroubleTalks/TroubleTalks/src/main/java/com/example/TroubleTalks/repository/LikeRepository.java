package com.example.TroubleTalks.repository;

import com.example.TroubleTalks.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    // Check if a specific user has liked a specific post
    Optional<Like> findByPostIdAndUserId(Long postId, Long userId);

    // Delete a like by Post and User ID (for unliking)
    void deleteByPostIdAndUserId(Long postId, Long userId);

    // Custom query to count likes for a single post
    long countByPostId(Long postId);

    // Custom query to fetch like counts for a list of post IDs
    @Query("SELECT l.post.id, COUNT(l) FROM Like l WHERE l.post.id IN :postIds GROUP BY l.post.id")
    List<Object[]> countLikesByPostIdIn(List<Long> postIds);
}