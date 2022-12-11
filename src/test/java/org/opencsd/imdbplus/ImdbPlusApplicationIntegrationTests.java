package org.opencsd.imdbplus;

import static org.assertj.core.api.Assertions.assertThat;

import org.opencsd.imdbplus.entity.AccountSetting;
import org.opencsd.imdbplus.entity.Client;
import org.opencsd.imdbplus.entity.Media;
import org.opencsd.imdbplus.entity.Timeline;
import org.opencsd.imdbplus.repository.ClientRepository;
import org.opencsd.imdbplus.repository.MediaRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.opencsd.imdbplus.service.TimelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class ImdbPlusApplicationIntegrationTests {

  @Autowired
  private ClientRepository clientRepository;

  @Autowired
  private TimelineService timelineService;

  @Autowired
  private MediaRepository mediaRepository;

  public static Client testClient = new Client();
  public static String testClientname = "testClient";
  public static AccountSetting testAccountSetting = new AccountSetting();
  public static String testClientId = "";
  public static String testAccessToken = "";
  public static Client retrievedClient = new Client();
  public static String testMediaId = "";

  static String dynamoDBEndpoint = "http://localhost:8083";


  /**
   * Test the client sign up functionality with a single test client. The expected result is that the
   * test client is added to the database.
   */
  @Test
  @Order(1)
  void testClientSave() {
    testClientname = UUID.randomUUID().toString().replace("-", "") + "-testClientname";
    testAccountSetting = new AccountSetting(false, true);
    // Create a test client
    testClient = new Client(testClientname, "testEmail", testAccountSetting);
    testClient = clientRepository.save(testClient);
    // Record the clientId and accessToken of the test client
    testClientId = testClient.getClientId();
    testAccessToken = testClient.getAccessToken();
    // Check testClientId and testAccessToken are not null
    assertThat(testClientId).isNotNull();
    assertThat(testAccessToken).isNotNull();
  }

  /**
   * Test the client sign up functionality with duplicate clientnames. The expected behavior is that the
   * second test client with the same clientname should not be added to the database and an
   * ConditionalCheckFailedException should be thrown.
   */
  @Test
  @Order(2)
  void testClientSaveDuplicatedClientname() {
    // Try to save the second test client to the database and expect an ConditionalCheckFailedException exception to be thrown
    try {
      clientRepository.save(testClient);
    } catch (Exception e) {
      assert e.getClass()
          .equals(com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException.class);
      assertThat(e.getMessage()).contains("ConditionalCheckFailedException");
    }
  }

  /**
   * Test the client retrieval functionality with a single test client. The expected result is that the
   * test client is retrieved from the database.
   */
  @Test
  @Order(3)
  void testGetClient() {
    retrievedClient = clientRepository.getClient(testClientId, testAccessToken);
    assertThat(retrievedClient.getClientId()).isEqualTo(testClientId);
  }

  @Test
  @Order(4)
  void testGetClientNotFound() {
    retrievedClient = clientRepository.getClient("testClientId", "testAccessToken");
    assertThat(retrievedClient).isNull();
  }

  @Test
  @Order(5)
  void testUpdateClient() {
    Client testClient = new Client(testClientname, "testEmailUpdated", testAccountSetting);
    testClient.setClientId(testClientId);
    testClient.setAccessToken(testAccessToken);
    String clientId = clientRepository.update(testClientId, testClient);
    assertThat(clientId).isEqualTo(testClientId);
  }

  /**
   * Test the timeline save functionality with a single test client and a single test timeline. The
   * expected behavior is that the test timeline is added to the database.
   */
  @Test
  @Order(6)
  void testTimelineSave() {
    testMediaId = UUID.randomUUID().toString().replace("-", "");
    String testTimelineId = testClientId + "-" + testMediaId;
    String testStatus = "DONE";
    int testRating = 5;
    String testComment = "This is a test comment";
    Timeline testTimeline = new Timeline(testTimelineId, testClientId, testMediaId, testStatus,
        testRating, testComment);
    Timeline response = timelineService.save(testTimeline, testAccessToken);
    assertThat(response).isNotNull();
  }

  @Test
  @Order(7)
  void testTimelineSaveInvalidAccessToken() {
    String testTimelineId = testClientId + "-" + testMediaId;
    String testStatus = "DONE";
    int testRating = 5;
    String testComment = "This is a test comment";
    Timeline testTimeline = new Timeline(testTimelineId, testClientId, testMediaId, testStatus,
        testRating, testComment);
    Timeline response = timelineService.save(testTimeline, "testAccessToken");
    assertThat(response).isNull();
  }

  /**
   * Test the timeline retrieval functionality with a single test client and a single test timeline.
   * The expected behavior is that the test timeline is retrieved from the database.
   */
  @Test
  @Order(8)
  void testTimelineGetTimelineByClientId() {
    List<Timeline> response = timelineService.getTimelineByClientId(testClientId);
    assertThat(response).isNotNull().hasSize(1);
    assertThat(response.get(0).getTimelineId()).isEqualTo(testClientId + "-" + testMediaId);
    assertThat(response.get(0).getClientId()).isEqualTo(testClientId);
    assertThat(response.get(0).getMediaId()).isEqualTo(testMediaId);
    assertThat(response.get(0).getStatus()).isEqualTo("DONE");
    assertThat(response.get(0).getRating()).isEqualTo(5);
    assertThat(response.get(0).getComment()).isEqualTo("This is a test comment");
  }

  @Test
  @Order(9)
  void testTimelineGetTimelineByMediaId() {
    List<Timeline> response = timelineService.getTimelineByMediaId(testMediaId);
    assertThat(response).isNotNull().hasSize(1);
    assertThat(response.get(0).getTimelineId()).isEqualTo(testClientId + "-" + testMediaId);
    assertThat(response.get(0).getClientId()).isEqualTo(testClientId);
    assertThat(response.get(0).getMediaId()).isEqualTo(testMediaId);
    assertThat(response.get(0).getStatus()).isEqualTo("DONE");
    assertThat(response.get(0).getRating()).isEqualTo(5);
    assertThat(response.get(0).getComment()).isEqualTo("This is a test comment");
  }

  /**
   * Test the timeline delete functionality with a single test client and a single test timeline. The
   * expected behavior is that the test timeline is deleted from the database.
   */
  @Test
  @Order(10)
  void testTimelineDelete() {
    String response = timelineService.delete(testClientId+"-"+testMediaId, testAccessToken);
    assertThat(response).isEqualTo("Timeline deleted successfully");
  }

  /**
   * Test the timeline retrieval functionality with a single test client with no associated timelines.
   * The expected behavior is that no timelines are retrieved from the database.
   */
  @Test
  @Order(11)
  void testTimelineGetTimelineByClientIdNotFound() {
    List<Timeline> response = timelineService.getTimelineByClientId(testClientId);
    assertThat(response).isEmpty();
  }

  /**
   * Test the client delete functionality with a single test client. The expected behavior is that the
   * test client is deleted from the database.
   */
  @Test
  @Order(12)
  void testDeleteClientInvalidAccessToken() {
    String deleteResult = clientRepository.delete(testClientId, "testAccessToken");
    assertThat(deleteResult).isEqualTo("Invalid access token");
  }

  @Test
  @Order(13)
  void testDeleteClient() {
    String deleteResult = clientRepository.delete(testClientId, testAccessToken);
    assertThat(deleteResult).isEqualTo("Client deleted successfully");
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