package com.example.imdbplus;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.example.imdbplus.entity.AccountSetting;
import com.example.imdbplus.entity.Timeline;
import com.example.imdbplus.entity.User;
import com.example.imdbplus.repository.TimelineRepository;
import com.example.imdbplus.repository.UserRepository;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class ImdbPlusApplicationTests {

  @Autowired
  private DynamoDBMapper dynamoDBMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TimelineRepository timelineRepository;

  public static User testUser;
  public static String testUsername;
  public static AccountSetting testAccountSetting;
  public static String testUserId;
  public static String testAccessToken;

  @Test
  @Order(1)
  void contextLoads() {
  }

  /**
   * Test the user sign up functionality with a single test user. The expected result is that the
   * test user is added to the database.
   */
  @Test
  @Order(2)
  void testUserSave() {
    testUsername = UUID.randomUUID().toString().replace("-", "") + "-testUsername";
    testAccountSetting = new AccountSetting(false, true);
    // Create a test user
    testUser = new User(testUsername, "testEmail", testAccountSetting);
    testUser = userRepository.save(testUser);
    // Record the userId and accessToken of the test user
    testUserId = testUser.getUserId();
    testAccessToken = testUser.getAccessToken();
    // Check testUserId and testAccessToken are not null
    assert !Objects.isNull(testUserId);
    assert !Objects.isNull(testAccessToken);
  }

  /**
   * Test the user sign up functionality with duplicate usernames. The expected behavior is that the
   * second test user with the same username should not be added to the database and an
   * ConditionalCheckFailedException should be thrown.
   */
  @Test
  @Order(3)
  void testUserSaveDuplicatedUsername() {
    // Try to save the second test user to the database and expect an ConditionalCheckFailedException exception to be thrown
    try {
      userRepository.save(testUser);
    } catch (Exception e) {
      assert e.getClass().equals(com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException.class);
      assert e.getMessage().equals("Conditional check failed");
    }
  }

  /**
   * Test the user retrieval functionality with a single test user. The expected result is that the test user is retrieved from the database.
   */
  @Test
  @Order(4)
  void testGetUser() {
    User retrievedUser = userRepository.getUser(testUserId);
    System.out.println();
    assert retrievedUser.equals(testUser);
  }

  @Test
  @Order(5)
  void testGetUserNotFound() {
    User retrievedUser = userRepository.getUser("non-existing-user-id");
    assert retrievedUser == null;
  }

  /**
   * Test the timeline save functionality with a single test user and a single test timeline. The
   * expected behavior is that the test timeline is added to the database.
   */
  @Test
  @Order(6)
  void testTimelineSave() {
      String testMediaId = "tt0000001";
      String testTimelineId = testUserId + "-" + testMediaId;
      String testStatus = "DONE";
      int testRating = 5;
      String testComment = "This is a test comment";
      Timeline testTimeline = new Timeline(testTimelineId, testUserId, testMediaId, testStatus,
          testRating, testComment);
      Timeline response = timelineRepository.save(testTimeline, testAccessToken);
      System.out.println(response);
      System.out.println(testTimeline);
      assert response.equals(testTimeline);
      // Clean up the test timeline
      timelineRepository.delete(testUserId, testMediaId, testAccessToken);
  }

  @Test
  @Order(7)
  void testDeleteUser(){
    String deleteResult = userRepository.delete(testUserId, testAccessToken);
    assert deleteResult.equals("User deleted successfully");
  }
}
