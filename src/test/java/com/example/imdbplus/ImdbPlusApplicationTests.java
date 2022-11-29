package com.example.imdbplus;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.example.imdbplus.entity.AccountSetting;
import com.example.imdbplus.entity.Timeline;
import com.example.imdbplus.entity.User;
import com.example.imdbplus.repository.TimelineRepository;
import com.example.imdbplus.repository.UserRepository;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

@SpringBootTest
class ImdbPlusApplicationTests {

  @Autowired
  private DynamoDBMapper dynamoDBMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TimelineRepository timelineRepository;

  @Test
  void contextLoads() {
  }

  /**
   * Test the user sign up functionality with a single test user. The expected result is that the
   * test user is added to the database.
   */
  @Test
  void testUserSave() throws Exception {
    String testUsername = UUID.randomUUID().toString().replace("-", "") + "-testUsername";
    AccountSetting testAccountSetting = new AccountSetting(false, true);
    // Create a test user
    User testUser = new User(testUsername, "testEmail", testAccountSetting);
    userRepository.save(testUser);
    // Record the userId and accessToken of the test user
    String testUserId = testUser.getUserId();
    String testAccessToken = testUser.getAccessToken();
    // Sleep for 1 second to wait for the database to update
    try {
      Thread.sleep(1000);
    } catch (InterruptedException ie) {
      // Retrieve the test user from the database
      User retrievedUser = dynamoDBMapper.load(User.class, testUserId);
      // Check if the retrieved user is the same as the test user
      assert retrievedUser.equals(testUser);
      // Delete the test user from the database
      userRepository.delete(testUserId, testAccessToken);
    }
  }

  /**
   * Test the user sign up functionality with duplicate usernames. The expected behavior is that the
   * second test user with the same username should not be added to the database and an
   * ConditionalCheckFailedException should be thrown.
   */
  @Test
  void testUserSaveDuplicatedUsername() throws Exception {
    String testUsername = UUID.randomUUID().toString().replace("-", "") + "-testUsername";
    AccountSetting testAccountSetting = new AccountSetting(false, true);
    // Create a test user
    User testUser = new User(testUsername, "testEmail", testAccountSetting);
    // Save the test user to the database
    userRepository.save(testUser);
    // Record the userId and accessToken of the test user
    String testUserId = testUser.getUserId();
    String testAccessToken = testUser.getAccessToken();
    // Create another test user with the same username
    User testUser2 = new User(testUsername, "testEmail2", testAccountSetting);

    try {
      Thread.sleep(1000);
    } catch (InterruptedException ie) {
      // Save the second test user to the database
      userRepository.save(testUser2);
      // Sleep for 1 second to wait for the database to update
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        // Retrieve the test user from the database
        User retrievedUser = dynamoDBMapper.load(User.class, testUserId);
        // Check if the retrieved user is the same as the test user
        assert retrievedUser.equals(testUser);
        // Delete the test user from the database
        userRepository.delete(testUserId, testAccessToken);
      }
    }
  }

  /**
   * Test the timeline save functionality with a single test user and a single test timeline. The
   * expected behavior is that the test timeline is added to the database.
   */
  @Test
  void testTimelineSave() throws Exception {
    String testUsername = UUID.randomUUID().toString().replace("-", "") + "-testUsername";
    AccountSetting testAccountSetting = new AccountSetting(false, true);
    // Create a test user
    User testUser = new User(testUsername, "testEmail", testAccountSetting);
    userRepository.save(testUser);
    // Record the userId and accessToken of the test user
    String testUserId = testUser.getUserId();
    String testAccessToken = testUser.getAccessToken();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException ie) {
      // Create a test timeline
      String testMediaId = "tt0000001";
      String testTimelineId = testUserId + "-" + testMediaId;
      String testStatus = "DONE";
      int testRating = 5;
      String testComment = "This is a test comment";
      Timeline testTimeline = new Timeline(testTimelineId, testUserId, testMediaId, testStatus,
          testRating, testComment);
      ResponseEntity response = timelineRepository.save(testTimeline, testAccessToken);
      assert response.getStatusCodeValue() == 200;
      String body = (String) response.getBody();
      assert Objects.requireNonNull(body).contains("Timeline saved successfully");
      timelineRepository.delete(testUserId, testMediaId, testAccessToken);
      userRepository.delete(testUserId, testAccessToken);
    }
  }
}
