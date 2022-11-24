package com.example.imdbplus.conroller;

import com.example.imdbplus.entity.User;
import com.example.imdbplus.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

  @Autowired
  private UserRepository userRepository;

  // Add new user to the database
  @RequestMapping(value = "/user", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity saveUser(@RequestBody User user) {
    return userRepository.save(user);
  }

  // Get user by userId
  @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
  @ResponseBody
  public User getUser(@PathVariable("id") String userId) {
    return userRepository.getUser(userId);
  }

  // Update user by userId
  @RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
  @ResponseBody
  public String updateUser(@PathVariable("id") String userId, @RequestBody User user) {
    return userRepository.update(userId, user);
  }

  // Delete user by userId
  @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public String deleteUser(@PathVariable("id") String userId,
      @RequestHeader("Authorization") String accessToken) {
    return userRepository.delete(userId, accessToken);
  }
}
