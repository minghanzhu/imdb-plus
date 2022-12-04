package org.opencsd.imdbplus;

import org.opencsd.imdbplus.entity.AccountSetting;
import org.opencsd.imdbplus.entity.Timeline;
import org.opencsd.imdbplus.entity.User;
import org.opencsd.imdbplus.repository.TimelineRepository;
import org.opencsd.imdbplus.repository.UserRepository;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class ImdbPlusApplicationIntegrationTests {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TimelineRepository timelineRepository;

  public static User testUser = new User();
  public static String testUsername = "testUser";
  public static AccountSetting testAccountSetting = new AccountSetting();
  public static String testUserId = "";
  public static String testAccessToken = "";
  public static User retrievedUser = new User();
  public static String testMediaId = "";

  static String dynamoDBEndpoint = "http://localhost:8083";

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
      assert e.getClass()
          .equals(com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException.class);
      assert e.getMessage().equals("Conditional check failed");
    }
  }

  /**
   * Test the user retrieval functionality with a single test user. The expected result is that the
   * test user is retrieved from the database.
   */
  @Test
  @Order(4)
  void testGetUser() {
    retrievedUser = userRepository.getUser(testUserId);
    assert retrievedUser.equals(testUser);
  }

  @Test
  @Order(5)
  void testGetUserNotFound() {
    retrievedUser = userRepository.getUser("testUserId");
    assert retrievedUser == null;
  }

  @Test
  @Order(6)
  void testUpdateUser() {
    User testUser = new User(testUsername, "testEmailUpdated", testAccountSetting);
    testUser.setUserId(testUserId);
    testUser.setAccessToken(testAccessToken);
    String userId = userRepository.update(testUserId, testUser);
    assert userId.equals(testUserId);
  }

  /**
   * Test the timeline save functionality with a single test user and a single test timeline. The
   * expected behavior is that the test timeline is added to the database.
   */
  @Test
  @Order(6)
  void testTimelineSave() {
    testMediaId = UUID.randomUUID().toString().replace("-", "");
    String testTimelineId = testUserId + "-" + testMediaId;
    String testStatus = "DONE";
    int testRating = 5;
    String testComment = "This is a test comment";
    Timeline testTimeline = new Timeline(testTimelineId, testUserId, testMediaId, testStatus,
        testRating, testComment);
    Timeline response = timelineRepository.save(testTimeline, testAccessToken);
    assert response.equals(testTimeline);
  }

  @Test
  @Order(7)
  void testTimelineSaveInvalidAccessToken() {
    String testTimelineId = testUserId + "-" + testMediaId;
    String testStatus = "DONE";
    int testRating = 5;
    String testComment = "This is a test comment";
    Timeline testTimeline = new Timeline(testTimelineId, testUserId, testMediaId, testStatus,
        testRating, testComment);
    Timeline response = timelineRepository.save(testTimeline, "testAccessToken");
    assert response == null;
  }

  /**
   * Test the timeline retrieval functionality with a single test user and a single test timeline.
   * The expected behavior is that the test timeline is retrieved from the database.
   */
  @Test
  @Order(8)
  void testTimelineGetTimelineByUserId() {
    List<Timeline> response = timelineRepository.getTimelineByUserId(testUserId);
    assert response.size() == 1;
    assert response.get(0).getUserId().equals(testUserId);
    assert response.get(0).getMediaId().equals(testMediaId);
    assert response.get(0).getStatus().equals("DONE");
    assert response.get(0).getRating() == 5;
    assert response.get(0).getComment().equals("This is a test comment");
  }

  @Test
  @Order(8)
  void testTimelineGetTimelineByMediaId() {
    List<Timeline> response = timelineRepository.getTimelineByMediaId(testMediaId);
    assert response.size() == 1;
    assert response.get(0).getUserId().equals(testUserId);
    assert response.get(0).getMediaId().equals(testMediaId);
    assert response.get(0).getStatus().equals("DONE");
    assert response.get(0).getRating() == 5;
    assert response.get(0).getComment().equals("This is a test comment");
  }

  /**
   * Test the timeline delete functionality with a single test user and a single test timeline. The
   * expected behavior is that the test timeline is deleted from the database.
   */
  @Test
  @Order(9)
  void testTimelineDelete() {
    String response = timelineRepository.delete(testUserId, testMediaId, testAccessToken);
    assert response.equals("Timeline deleted successfully");
  }

  /**
   * Test the timeline retrieval functionality with a single test user with no associated timelines.
   * The expected behavior is that no timelines are retrieved from the database.
   */
  @Test
  @Order(10)
  void testTimelineGetTimelineByUserIdNotFound() {
    List<Timeline> response = timelineRepository.getTimelineByUserId(testUserId);
    assert response.isEmpty();
  }

  /**
   * Test the user delete functionality with a single test user. The expected behavior is that the
   * test user is deleted from the database.
   */
  @Test
  @Order(11)
  void testDeleteUserInvalidAccessToken() {
    String deleteResult = userRepository.delete(testUserId, "testAccessToken");
    assert deleteResult.equals("Invalid access token");
  }

  @Test
  @Order(12)
  void testDeleteUser() {
    String deleteResult = userRepository.delete(testUserId, testAccessToken);
    assert deleteResult.equals("User deleted successfully");
  }
}