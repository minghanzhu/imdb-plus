package org.opencsd.imdbplus;
import org.opencsd.imdbplus.entity.AccountSetting;
import org.opencsd.imdbplus.entity.Media;
import org.opencsd.imdbplus.entity.Timeline;
import org.opencsd.imdbplus.entity.User;
import org.opencsd.imdbplus.repository.TimelineRepository;
import org.opencsd.imdbplus.repository.UserRepository;
import org.opencsd.imdbplus.repository.MediaRepository;
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
class ImdbPlusApplicationTests {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TimelineRepository timelineRepository;

  @Autowired
  private MediaRepository mediaRepository;

  public static User testUser = new User();
  public static String testUsername = "testUser";
  public static AccountSetting testAccountSetting = new AccountSetting();
  public static String testUserId = "";
  public static String testAccessToken = "";
  public static User retrievedUser = new User();


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
    retrievedUser = userRepository.getUser(testUserId);
    assert retrievedUser.equals(testUser);
  }

  @Test
  @Order(5)
  void testGetUserNotFound() {
    retrievedUser = userRepository.getUser("testUserId");
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
    assert response.equals(testTimeline);
  }

  @Test
  @Order(7)
  void testTimelineSaveInvalidAccessToken() {
    String testMediaId = "tt0000001";
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
    assert response.get(0).getMediaId().equals("tt0000001");
    assert response.get(0).getStatus().equals("DONE");
    assert response.get(0).getRating() == 5;
    assert response.get(0).getComment().equals("This is a test comment");
  }

  /**
   * Test the timeline delete functionality with a single test user and a single test timeline.
   * The expected behavior is that the test timeline is deleted from the database.
   */
  @Test
  @Order(9)
  void testTimelineDelete() {
    String response = timelineRepository.delete(testUserId, "tt0000001", testAccessToken);
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
    assert response == null;
  }

  /**
   * Test the user delete functionality with a single test user.
   * The expected behavior is that the test user is deleted from the database.
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

  @Test
  @Order(13)
  void testMediaSave() {
    // Create a test media
    Media testMedia = new Media("tt0000012", "testTitle", "2017-09-09", "Drama");
    Media response = mediaRepository.saveMedia(testMedia);

    assert response.equals(testMedia);
  }

    @Test
    @Order(14)
    void testMediaSaveDuplicate() {
        // Create a test media
      Media testMedia = new Media("tt0000012", "testTitle", "2017-09-09", "Drama");
      try {
        mediaRepository.saveMedia(testMedia);
      } catch (Exception e) {
        assert e.getClass().equals(com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException.class);
        assert e.getMessage().equals("Conditional check failed");
      }

    }

  @Test
  @Order(15)
  void getMedia() {
    Media response = mediaRepository.getEntity("tt0000012");
    assert response.getMediaId().equals("tt0000012");
    assert response.getGenre().equals("Drama");
    assert response.getTitle().equals("testTitle");
    assert response.getRelease_date().equals("2017-09-09");
  }

  @Test
  @Order(16)
  void testUpdateMedia() {
    Media testMedia = new Media("tt0000012", "testTitleEdited", "2017-09-09", "Drama");
    String updateResult = mediaRepository.update("tt0000012", testMedia);
    assert updateResult.equals("tt0000012");
    Media response = mediaRepository.getEntity("tt0000012");
    assert response.getTitle().equals("testTitleEdited");
  }

  @Test
  @Order(17)
  void testDeleteMedia() {
    String deleteResult = mediaRepository.delete("tt0000012");
    assert deleteResult.equals("Media deleted successfully");
  }

}