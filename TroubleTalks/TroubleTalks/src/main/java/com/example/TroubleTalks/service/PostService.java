package com.example.TroubleTalks.service;

import com.example.TroubleTalks.entity.Post;
import com.example.TroubleTalks.repository.PostRepository;
import jakarta.transaction.Transactional; // Import Transactional
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    public List<Post> findAllPosts() {
        return postRepository.findAllByOrderByPostedAtDesc();
    }

    public Optional<Post> findPostById(Long id) {
        return postRepository.findById(id);
    }

    // NEW: Method to delete a post
    @Transactional
    public void deletePostById(Long id) {
        postRepository.deleteById(id);
    }
}