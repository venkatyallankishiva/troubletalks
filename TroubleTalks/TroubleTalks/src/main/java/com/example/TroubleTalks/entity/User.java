package com.example.TroubleTalks.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List; // Import List

@Entity
@Table(name = "userInfo")
public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String username;

        @Column(unique = true, nullable = false)
        private String email;

        private String password;

        // NEW: Bio field for the user profile
        @Column(columnDefinition = "TEXT")
        private String bio;

        // NEW: One-to-many relationship for posts created by this user
        @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private List<Post> posts;


        @CreationTimestamp
        private LocalDateTime createdAt;

        public User() {}

        // Getters and Setters
        public Long getId() {
                return id;
        }
        public void setId(Long id) {
                this.id = id;
        }
        public String getUsername() {
                return username;
        }
        public void setUsername(String username) {
                this.username = username;
        }
        public String getEmail() {
                return email;
        }
        public void setEmail(String email) {
                this.email = email;
        }
        public String getPassword() {
                return password;
        }
        public void setPassword(String password) {
                this.password = password;
        }

        // NEW GETTER/SETTER for Bio
        public String getBio() {
                return bio;
        }
        public void setBio(String bio) {
                this.bio = bio;
        }

        // NEW GETTER/SETTER for Posts
        public List<Post> getPosts() {
                return posts;
        }
        public void setPosts(List<Post> posts) {
                this.posts = posts;
        }

        public LocalDateTime getCreatedAt() {
                return createdAt;
        }
        public void setCreatedAt(LocalDateTime createdAt) {
                this.createdAt = createdAt;
        }
}