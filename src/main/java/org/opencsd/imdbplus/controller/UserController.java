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
  public ResponseEntity<User> save(@RequestBody User user) {
    User response = userRepository.save(user);
    if (response == null) {
      return ResponseEntity.badRequest().body(null);
    } else {
      return ResponseEntity.ok(response);
    }
  }

  // Get user by userId
  @GetMapping("/user/{id}")
  public ResponseEntity<User> getUser(@PathVariable("id") String userId) {
    User response = userRepository.getUser(userId);
    if (response == null) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok(response);
    }
  }

  // Delete user by userId
  @DeleteMapping("/user/{id}")
  public ResponseEntity<String> deleteUser(@PathVariable("id") String userId,
      @RequestHeader("Authorization") String accessToken) {
    String response = userRepository.delete(userId, accessToken);
    return ResponseEntity.ok(response);
  }

  // Update user by userId
  @PutMapping("/user/{id}")
  public ResponseEntity<String> updateUser(@PathVariable("id") String userId, @RequestBody User user) {
    String response = userRepository.update(userId, user);
    return ResponseEntity.ok(response);
  }
}