package com.example.TroubleTalks.service;

import com.example.TroubleTalks.entity.Comment;
import com.example.TroubleTalks.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdOrderByCommentedAtAsc(postId);
    }

    public long countComments(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    public Map<Long, Long> getCommentCountsForPosts(List<Long> postIds) {
        return commentRepository.countCommentsByPostIdIn(postIds).stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],
                        arr -> (Long) arr[1]
                ));
    }
}