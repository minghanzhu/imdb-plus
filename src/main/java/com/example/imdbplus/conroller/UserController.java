package com.example.imdbplus.conroller;

import com.example.imdbplus.entity.User;
import com.example.imdbplus.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;

    // Add new user to the database
    @PostMapping("/user")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    // Get user by userId
    @GetMapping("/user/{id}")
    public User getUser(@PathVariable("id") String userId) {
        return userRepository.getUser(userId);
    }

    // Delete user by userId
    @DeleteMapping("/user/{id}")
    public String deleteUser(@PathVariable("id") String userId) {
        return userRepository.delete(userId);
    }

    // Update user by userId
    @PutMapping("/user/{id}")
    public String updateUser(@PathVariable("id") String userId, @RequestBody User user) {
        return userRepository.update(userId, user);
    }
}
