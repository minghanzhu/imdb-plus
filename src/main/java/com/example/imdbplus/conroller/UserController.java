package com.example.imdbplus.conroller;

import com.example.imdbplus.entity.User;
import com.example.imdbplus.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  @Autowired
  private UserRepository userRepository;

  // Add new user to the database
  @PostMapping("/user")
  public ResponseEntity saveUser(@RequestBody User user) {
    return userRepository.save(user);
  }

  // Get user by userId
  @GetMapping("/user/{id}")
  public User getUser(@PathVariable("id") String userId) {
    return userRepository.getUser(userId);
  }

  // Delete user by userId
  @DeleteMapping("/user/{id}")
  public String deleteUser(@PathVariable("id") String userId,
      @RequestHeader("Authorization") String accessToken) {
    return userRepository.delete(userId, accessToken);
  }

  // Update user by userId
  @PutMapping("/user/{id}")
  public String updateUser(@PathVariable("id") String userId, @RequestBody User user) {
    return userRepository.update(userId, user);
  }
}
