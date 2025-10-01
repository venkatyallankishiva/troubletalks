package com.example.TroubleTalks.service;

import com.example.TroubleTalks.entity.Like;
import com.example.TroubleTalks.entity.Post;
import com.example.TroubleTalks.entity.User;
import com.example.TroubleTalks.repository.LikeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LikeService {

    private final LikeRepository likeRepository;

    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    public boolean isPostLikedByUser(Long postId, Long userId) {
        return likeRepository.findByPostIdAndUserId(postId, userId).isPresent();
    }

    public long countLikes(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    // Fetches like counts for a list of posts, returning a map: PostId -> Count
    public Map<Long, Long> getLikeCountsForPosts(List<Long> postIds) {
        return likeRepository.countLikesByPostIdIn(postIds).stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],
                        arr -> (Long) arr[1]
                ));
    }

    @Transactional
    public void likePost(Post post, User user) {
        if (!isPostLikedByUser(post.getId(), user.getId())) {
            Like newLike = new Like();
            newLike.setPost(post);
            newLike.setUser(user);
            likeRepository.save(newLike);
        }
    }

    @Transactional
    public void unlikePost(Long postId, Long userId) {
        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }
}