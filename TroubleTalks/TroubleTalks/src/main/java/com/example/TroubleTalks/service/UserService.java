package com.example.TroubleTalks.service;

import com.example.TroubleTalks.entity.User;
import com.example.TroubleTalks.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // NEW: Method to find user along with their posts
    public Optional<User> findByEmailWithPosts(String email) {
        return userRepository.findByEmailWithPosts(email);
    }

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // NEW: Method to update a user's profile/bio
    public User saveUser(User user) {
        return userRepository.save(user);
    }
}