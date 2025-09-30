package com.example.TroubleTalks.controller;

import com.example.TroubleTalks.entity.Comment;
import com.example.TroubleTalks.entity.Post;
import com.example.TroubleTalks.entity.User;
import com.example.TroubleTalks.service.CloudinaryService; // NEW IMPORT: Cloudinary Service
import com.example.TroubleTalks.service.CommentService;
import com.example.TroubleTalks.service.LikeService;
import com.example.TroubleTalks.service.PostService;
import com.example.TroubleTalks.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // NEW IMPORT: For File Uploads
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;
    private final LikeService likeService;
    private final CloudinaryService cloudinaryService; // NEW FIELD: Cloudinary Service

    // UPDATED CONSTRUCTOR: Now injects CloudinaryService
    public PostController(PostService postService, UserService userService, CommentService commentService, LikeService likeService, CloudinaryService cloudinaryService) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
        this.likeService = likeService;
        this.cloudinaryService = cloudinaryService; // INITIALIZE Cloudinary Service
    }

    private User getCurrentUser(UserDetails userDetails) {
        return userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private User getCurrentUserWithPosts(UserDetails userDetails) {
        return userService.findByEmailWithPosts(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private void checkAuthorization(Post post, User user) {
        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to perform this action.");
        }
    }

    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = getCurrentUserWithPosts(userDetails);
        List<Post> userPosts = user.getPosts();

        List<Long> postIds = userPosts.stream().map(Post::getId).collect(Collectors.toList());
        Map<Long, Long> likeCounts = likeService.getLikeCountsForPosts(postIds);
        Map<Long, Long> commentCounts = commentService.getCommentCountsForPosts(postIds);

        model.addAttribute("user", user);
        model.addAttribute("posts", userPosts);
        model.addAttribute("likeCounts", likeCounts);
        model.addAttribute("commentCounts", commentCounts);

        return "profile";
    }

    @PostMapping("/profile")
    public String updateBio(@RequestParam("bio") String bio,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(userDetails);

        user.setBio(bio);
        userService.saveUser(user);

        redirectAttributes.addFlashAttribute("successMessage", "Your profile bio has been updated!");

        return "redirect:/profile";
    }

    @GetMapping("/new-post")
    public String newPost(Model model) {
        model.addAttribute("post", new Post());
        return "new_post";
    }

    // UPDATED METHOD: Accepts the file upload and uses CloudinaryService
    @PostMapping("/new-post")
    public String savePost(@ModelAttribute("post") Post post,
                           @RequestParam("file") MultipartFile file, // NEW: File parameter
                           @AuthenticationPrincipal UserDetails userDetails) {

        User author = getCurrentUser(userDetails);
        post.setAuthor(author);

        // 1. Handle file upload (only if file is present)
        if (!file.isEmpty()) {
            // Upload the file and get the persistent public URL from Cloudinary
            String publicUrl = cloudinaryService.uploadFile(file);
            post.setFileName(publicUrl); // Store the public URL
        }

        // 2. Save the post entity
        postService.savePost(post);

        return "redirect:/explore";
    }

    // UPDATED METHOD: Now accepts an optional 'query' parameter for search
    @GetMapping("/explore")
    public String explorePosts(@RequestParam(required = false) String query, Model model) {

        // 1. Pass the query to the service layer (service handles logic for empty/null query)
        List<Post> posts = postService.findAllPosts(query);
        List<Long> postIds = posts.stream().map(Post::getId).collect(Collectors.toList());

        // 2. Fetch counts for all visible posts
        Map<Long, Long> likeCounts = likeService.getLikeCountsForPosts(postIds);
        Map<Long, Long> commentCounts = commentService.getCommentCountsForPosts(postIds);

        model.addAttribute("posts", posts);
        model.addAttribute("likeCounts", likeCounts);
        model.addAttribute("commentCounts", commentCounts);
        model.addAttribute("categories", List.of("Coding", "DIY", "Health", "Finance", "Life Hacks", "Other"));

        // 3. Pass the current query back to the view for pre-filling the search bar
        model.addAttribute("currentQuery", query);

        return "explore";
    }

    @GetMapping("/post/{id}")
    public String viewPost(@PathVariable Long id,
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) {

        Post post = postService.findPostById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));

        User currentUser = getCurrentUser(userDetails);

        List<Comment> comments = commentService.getCommentsByPostId(id);
        long likeCount = likeService.countLikes(id);
        boolean isLiked = likeService.isPostLikedByUser(id, currentUser.getId());

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("newComment", new Comment());
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("isLiked", isLiked);

        model.addAttribute("currentUserId", currentUser.getId());

        return "view_post";
    }

    @PostMapping("/post/comment")
    public String addComment(@ModelAttribute("newComment") Comment comment,
                             @RequestParam("postId") Long postId,
                             @AuthenticationPrincipal UserDetails userDetails) {

        User author = getCurrentUser(userDetails);
        Post post = postService.findPostById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + postId));

        comment.setAuthor(author);
        comment.setPost(post);
        commentService.saveComment(comment);

        return "redirect:/post/" + postId + "#comments";
    }

    @PostMapping("/post/{id}/like")
    public String likePost(@PathVariable Long id,
                           @AuthenticationPrincipal UserDetails userDetails) {

        User user = getCurrentUser(userDetails);
        Post post = postService.findPostById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));

        likeService.likePost(post, user);

        return "redirect:/post/" + id;
    }

    @PostMapping("/post/{id}/unlike")
    public String unlikePost(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails userDetails) {

        User user = getCurrentUser(userDetails);

        likeService.unlikePost(id, user.getId());

        return "redirect:/post/" + id;
    }

    @GetMapping("/post/{id}/edit")
    public String editPostForm(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Post post = postService.findPostById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));

        User currentUser = getCurrentUser(userDetails);

        // Authorization check: Only author can edit
        checkAuthorization(post, currentUser);

        model.addAttribute("post", post);
        return "edit_post";
    }


    @PostMapping("/post/{id}/edit")
    public String editPost(@PathVariable Long id,
                           @ModelAttribute("post") Post updatedPost,
                           @AuthenticationPrincipal UserDetails userDetails) {

        // NOTE: File upload for editing requires updating the file upload logic here,
        // but for simplicity, we focus on text fields for now.

        Post existingPost = postService.findPostById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));

        User currentUser = getCurrentUser(userDetails);

        checkAuthorization(existingPost, currentUser);

        // Update mutable fields
        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setCategory(updatedPost.getCategory());
        existingPost.setJourney(updatedPost.getJourney());
        existingPost.setSolution(updatedPost.getSolution());
        existingPost.setTakeaway(updatedPost.getTakeaway());
        // existingPost.setFileName() would be updated if a new file was uploaded

        postService.savePost(existingPost);

        return "redirect:/post/" + id;
    }


    @PostMapping("/post/{id}/delete")
    public String deletePost(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails userDetails) {

        Post post = postService.findPostById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));

        User currentUser = getCurrentUser(userDetails);

        checkAuthorization(post, currentUser);


        postService.deletePostById(id);

        return "redirect:/explore";
    }
}