package com.foodorder.order.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodorder.order.entity.User;
import com.foodorder.order.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public String register(@RequestBody User user) {

        if (user.getName() == null || user.getName().isBlank() ||
                user.getEmail() == null || user.getEmail().isBlank() ||
                user.getPassword() == null || user.getPassword().isBlank()) {
            log.warn("[UserController] Register failed: All fields are required");
            return "All fields are required";
        }

        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            log.warn("[UserController] Register failed: Invalid email format ({})", user.getEmail());
            return "enter a valid email ";
        }

        User existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser != null) {
            log.warn("[UserController] Register failed: Email already registered ({})", user.getEmail());
            return "Email already registered";
        }

        // Securely hash the password using BCrypt
        user.setPassword(passwordEncoder.encode(user.getPassword().trim()));
        userRepository.save(user);

        log.info("[UserController] User registered successfully with email: {}", user.getEmail());
        return "Account created successfully";
    }

    @PostMapping("/login")
    public User login(@RequestBody User user) {

        if (user.getEmail() == null || user.getEmail().isBlank() ||
                user.getPassword() == null || user.getPassword().isBlank()) {
            log.warn("[UserController] Login failed: Empty email or password");
            return null;
        }

        User existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser == null) {
            log.warn("[UserController] Login failed: User not found with email: {}", user.getEmail());
            return null;
        }

        // Validate password against hashed hash in DB
        if (passwordEncoder.matches(user.getPassword().trim(), existingUser.getPassword())) {
            log.info("[UserController] Login successful for email: {}", user.getEmail());
            // Clear password payload before sending to client for security
            User responseUser = new User();
            responseUser.setId(existingUser.getId());
            responseUser.setName(existingUser.getName());
            responseUser.setEmail(existingUser.getEmail());
            return responseUser;
        }

        log.warn("[UserController] Login failed: Password mismatch for email: {}", user.getEmail());
        return null;
    }
}
