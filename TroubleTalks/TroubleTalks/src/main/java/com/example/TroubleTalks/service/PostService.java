package com.example.TroubleTalks.service;

import com.example.TroubleTalks.entity.Post;
import com.example.TroubleTalks.repository.PostRepository;
import jakarta.transaction.Transactional;
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

    public List<Post> findAllPosts(String query) {
        if (query != null && !query.trim().isEmpty()) {
            return postRepository.searchPosts(query);
        }
        return postRepository.findAllByOrderByPostedAtDesc();
    }

    public Optional<Post> findPostById(Long id) {
        return postRepository.findById(id);
    }

    @Transactional
    public void deletePostById(Long id) {
        postRepository.deleteById(id);
    }
}