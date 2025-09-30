package com.example.TroubleTalks.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The Problem/Error Title
    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 50)
    private String category;

    // The detailed account of the journey, mistakes, and attempts (The Core Value)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String journey;

    // The final, working solution
    @Column(nullable = false, columnDefinition = "TEXT")
    private String solution;

    // The key takeaway or lesson learned
    @Column(nullable = false, length = 300)
    private String takeaway;

    // --> NEW FIELD FOR FILE URL/PATH <--
    private String fileName;

    // Link the post to the User who created it
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @CreationTimestamp
    private LocalDateTime postedAt;

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getJourney() { return journey; }
    public void setJourney(String journey) { this.journey = journey; }
    public String getSolution() { return solution; }
    public void setSolution(String solution) { this.solution = solution; }
    public String getTakeaway() { return takeaway; }
    public void setTakeaway(String takeaway) { this.takeaway = takeaway; }

    // --> NEW GETTER/SETTER FOR FILE NAME <--
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    public LocalDateTime getPostedAt() { return postedAt; }
    public void setPostedAt(LocalDateTime postedAt) { this.postedAt = postedAt; }
}