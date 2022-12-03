package org.opencsd.imdbplus.controller;

import org.opencsd.imdbplus.entity.User;
import org.opencsd.imdbplus.repository.UserRepository;
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
    User response = userRepository.save(user);
    if (response == null) {
      return ResponseEntity.status(400).body("User already exists");
    } else {
      return ResponseEntity.ok(response);
    }
  }

  // Get user by userId
  @GetMapping("/user/{id}")
  public ResponseEntity getUser(@PathVariable("id") String userId) {
    User response = userRepository.getUser(userId);
    if (response == null) {
      return ResponseEntity.status(404).body("User not found");
    } else {
      return ResponseEntity.ok(response);
    }
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
