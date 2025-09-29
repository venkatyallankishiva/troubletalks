package com.example.TroubleTalks.repository;

import com.example.TroubleTalks.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // NEW: Custom query to fetch user and their posts for the profile page
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.posts p WHERE u.email = :email ORDER BY p.postedAt DESC")
    Optional<User> findByEmailWithPosts(@Param("email") String email);
}