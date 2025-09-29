package com.example.TroubleTalks.controller;

import com.example.TroubleTalks.entity.Comment;
import com.example.TroubleTalks.entity.Post;
import com.example.TroubleTalks.entity.User;
// ... other imports
import com.example.TroubleTalks.service.CommentService;
import com.example.TroubleTalks.service.LikeService;
import com.example.TroubleTalks.service.PostService;
import com.example.TroubleTalks.service.UserService;
// ... other imports
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // New Import

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;
    private final LikeService likeService;

    public PostController(PostService postService, UserService userService, CommentService commentService, LikeService likeService) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
        this.likeService = likeService;
    }

    // Utility method to get the current authenticated user
    private User getCurrentUser(UserDetails userDetails) {
        // We now use findByEmail to prevent lazy-load errors when getting the ID
        return userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Utility method to get the current authenticated user with their posts
    private User getCurrentUserWithPosts(UserDetails userDetails) {
        return userService.findByEmailWithPosts(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private void checkAuthorization(Post post, User user) {
        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to perform this action.");
        }
    }

    // ... New Post Creation, Explore Posts, View Post, Comment Handlers, Like/Unlike, Edit/Delete (All previous code remains)

    // ----------------------------------------------------
    // NEW: My Profile Page (GET /profile)
    // ----------------------------------------------------
    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Fetch user with posts for the profile view
        User user = getCurrentUserWithPosts(userDetails);
        List<Post> userPosts = user.getPosts();

        // Prepare data for post snippets (like/comment counts)
        List<Long> postIds = userPosts.stream().map(Post::getId).collect(Collectors.toList());
        Map<Long, Long> likeCounts = likeService.getLikeCountsForPosts(postIds);
        Map<Long, Long> commentCounts = commentService.getCommentCountsForPosts(postIds);

        model.addAttribute("user", user);
        model.addAttribute("posts", userPosts);
        model.addAttribute("likeCounts", likeCounts);
        model.addAttribute("commentCounts", commentCounts);

        return "profile";
    }

    // ----------------------------------------------------
    // NEW: Update Bio Action (POST /profile)
    // ----------------------------------------------------
    @PostMapping("/profile")
    public String updateBio(@RequestParam("bio") String bio,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(userDetails);

        // Update the bio field
        user.setBio(bio);
        userService.saveUser(user);

        redirectAttributes.addFlashAttribute("successMessage", "Your profile bio has been updated!");

        return "redirect:/profile";
    }

    // ... other methods from previous steps follow here ...

    // This method is just a placeholder for the final controller structure.
    // You must ensure all the previous methods (newPost, explorePosts, viewPost, addComment, likePost, etc.)
    // are included above this point in your actual PostController.java file.

    // ...
    // New Post Creation... (No changes)
    @GetMapping("/new-post")
    public String newPost(Model model) {
        model.addAttribute("post", new Post());
        return "new_post";
    }

    @PostMapping("/new-post")
    public String savePost(@ModelAttribute("post") Post post,
                           @AuthenticationPrincipal UserDetails userDetails) {

        User author = getCurrentUser(userDetails);

        post.setAuthor(author);
        postService.savePost(post);

        return "redirect:/explore";
    }

    // Explore Posts Page... (No changes)
    @GetMapping("/explore")
    public String explorePosts(Model model) {
        List<Post> posts = postService.findAllPosts();
        List<Long> postIds = posts.stream().map(Post::getId).collect(Collectors.toList());

        Map<Long, Long> likeCounts = likeService.getLikeCountsForPosts(postIds);
        Map<Long, Long> commentCounts = commentService.getCommentCountsForPosts(postIds);

        model.addAttribute("posts", posts);
        model.addAttribute("likeCounts", likeCounts);
        model.addAttribute("commentCounts", commentCounts);
        model.addAttribute("categories", List.of("Coding", "DIY", "Health", "Finance", "Life Hacks", "Other"));
        return "explore";
    }

    // ----------------------------------------------------
    // Single Post View Page (Updated to pass current user)
    // ----------------------------------------------------
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

        // Pass the current user ID to the view for authorization checks
        model.addAttribute("currentUserId", currentUser.getId());

        return "view_post";
    }

    // Comment Submission Handler... (No changes)
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

    // Like/Unlike Handlers... (No changes)
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

    // ----------------------------------------------------
    // NEW: Post Edit Form (GET)
    // ----------------------------------------------------
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

    // ----------------------------------------------------
    // NEW: Post Edit Submission (POST)
    // ----------------------------------------------------
    @PostMapping("/post/{id}/edit")
    public String editPost(@PathVariable Long id,
                           @ModelAttribute("post") Post updatedPost,
                           @AuthenticationPrincipal UserDetails userDetails) {

        Post existingPost = postService.findPostById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));

        User currentUser = getCurrentUser(userDetails);

        // Authorization check
        checkAuthorization(existingPost, currentUser);

        // Update only the mutable fields (title, category, journey, solution, takeaway)
        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setCategory(updatedPost.getCategory());
        existingPost.setJourney(updatedPost.getJourney());
        existingPost.setSolution(updatedPost.getSolution());
        existingPost.setTakeaway(updatedPost.getTakeaway());

        postService.savePost(existingPost);

        return "redirect:/post/" + id;
    }

    // ----------------------------------------------------
    // NEW: Post Delete Action (POST)
    // ----------------------------------------------------
    @PostMapping("/post/{id}/delete")
    public String deletePost(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails userDetails) {

        Post post = postService.findPostById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));

        User currentUser = getCurrentUser(userDetails);

        // Authorization check
        checkAuthorization(post, currentUser);

        // Note: For a robust app, you should delete related likes/comments first or configure CASCADE DELETE in SQL.
        // Assuming your JPA/DB setup handles dependent entities (Likes, Comments) correctly for now.
        postService.deletePostById(id);

        // Redirect to the explore page after deletion
        return "redirect:/explore";
    }
}