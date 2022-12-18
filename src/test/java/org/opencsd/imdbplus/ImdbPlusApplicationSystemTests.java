package org.opencsd.imdbplus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.opencsd.imdbplus.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class ImdbPlusApplicationSystemTests {

  static String dynamoDBEndpoint = "http://localhost:8083";

  @Autowired
  private MediaRepository mediaRepository;

  ImdbPlusApplicationSystemTests() throws IOException {
  }

  public static String postRequest(String jsonInputString, String urlString, String accessToken)
      throws IOException {
    URL url = new URL(urlString);
    java.net.HttpURLConnection con = (java.net.HttpURLConnection) url.openConnection();
    try {
      con.setRequestMethod("POST");
    } catch (ProtocolException e) {
      throw new RuntimeException(e);
    }
    con.setRequestProperty("Content-Type", "application/json");
    con.setRequestProperty("Authorization", accessToken);
    con.setDoOutput(true);
    try (java.io.OutputStream os = con.getOutputStream()) {
      byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
      os.write(input, 0, input.length);
    }
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
      StringBuilder response = new StringBuilder();
      String responseLine = null;
      while ((responseLine = br.readLine()) != null) {
        response.append(responseLine.trim());
      }
      return response.toString();
    }
  }

  public static String getRequest(String urlString, String accessToken) throws IOException {
    URL url = new URL(urlString);
    java.net.HttpURLConnection con = (java.net.HttpURLConnection) url.openConnection();
    try {
      con.setRequestMethod("GET");
    } catch (ProtocolException e) {
      throw new RuntimeException(e);
    }
    con.setRequestProperty("Content-Type", "application/json");
    con.setRequestProperty("Authorization", accessToken);
    con.setDoOutput(true);
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
      StringBuilder response = new StringBuilder();
      String responseLine = null;
      while ((responseLine = br.readLine()) != null) {
        response.append(responseLine.trim());
      }
      return response.toString();
    }
  }

  public static String deleteRequest(String urlString, String accessToken) throws IOException {
    URL url = new URL(urlString);
    java.net.HttpURLConnection con = (java.net.HttpURLConnection) url.openConnection();
    try {
      con.setRequestMethod("DELETE");
    } catch (ProtocolException e) {
      throw new RuntimeException(e);
    }
    con.setRequestProperty("Content-Type", "application/json");
    con.setRequestProperty("Authorization", accessToken);
    con.setDoOutput(true);
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
      StringBuilder response = new StringBuilder();
      String responseLine = null;
      while ((responseLine = br.readLine()) != null) {
        response.append(responseLine.trim());
      }
      return response.toString();
    }
  }

  @BeforeAll
  public static void setup() {
    SpringApplication.run(Main.class);
  }

  /**
   * Test the client sign up functionality with a single test client. The expected result is that the
   * test client is added to the database.
   */
  @Test
  @Order(1)
  void testClientSave() throws Exception {

    String testClientname = UUID.randomUUID().toString().replace("-", "") + "-testClientname";

    // POST request to add a new client
    String jsonInputString = "{\"" +
        "clientname\":\"" + testClientname + "\"," +
        "\"email\":\"   \"," +
        "\"accountSetting\":{" +
        "\"isPrivate\":false," +
        "\"isAdult\":true}" +
        "}";
    String response = postRequest(jsonInputString, dynamoDBEndpoint + "/client", null);

    JSONObject jsonObject = new JSONObject(response);
    String clientname = jsonObject.getString("clientname");
    assertThat(clientname).isEqualTo(testClientname);

    // DELETE the added test client to clean up
    String clientId = jsonObject.getString("clientId");
    String accessToken = jsonObject.getString("accessToken");
    deleteRequest(dynamoDBEndpoint + "/client/" + clientId, accessToken);
  }

  /**
   * Test the client sign up functionality with duplicate clientnames. The expected behavior is that the
   * second test client with the same clientname should not be added to the database.
   */
  @Test
  @Order(2)
  void testClientSaveDuplicatedClientname() throws Exception {

    String testClientname = UUID.randomUUID().toString().replace("-", "") + "-testClientname";

    // POST request to add a new client
    String jsonInputString = "{\"" +
        "clientname\":\"" + testClientname + "\"," +
        "\"email\":\"   \"," +
        "\"accountSetting\":{" +
        "\"isPrivate\":false," +
        "\"isAdult\":true}" +
        "}";
    String response = postRequest(jsonInputString, dynamoDBEndpoint + "/client", null);

    // Assert POST request to add a new client with the same clientname should return HTTP 400
    String response2 = null;
    try {
      response2 = postRequest(jsonInputString, dynamoDBEndpoint + "/client", null);
    } catch (IOException e) {
      response2 = e.getMessage();
      assertThat(response2).contains("400");
    }

    // DELETE the added test client to clean up
    JSONObject jsonObject = new JSONObject(response);
    String clientId = jsonObject.getString("clientId");
    String accessToken = jsonObject.getString("accessToken");
    deleteRequest(dynamoDBEndpoint + "/client/" + clientId, accessToken);
  }

  /**
   * Test the timeline save functionality with a single test client and a single test timeline. The
   * expected behavior is that the test timeline is added to the database.
   */
  @Test
  @Order(3)
  void testTimelineSave() throws Exception {

    String testClientname = UUID.randomUUID().toString().replace("-", "") + "-testClientname";

    // POST request to add a new client
    String jsonInputString = "{\"" +
        "clientname\":\"" + testClientname + "\"," +
        "\"email\":\"   \"," +
        "\"accountSetting\":{" +
        "\"isPrivate\":false," +
        "\"isAdult\":true}" +
        "}";
    String response = postRequest(jsonInputString, dynamoDBEndpoint + "/client", null);

    // POST request to add a new timeline
    JSONObject jsonObject = new JSONObject(response);
    String clientId = jsonObject.getString("clientId");
    String accessToken = jsonObject.getString("accessToken");
    jsonInputString = "{\"" +
        "timelineId\":\"" + clientId + "-tt0000001\"," +
        "\"clientId\":\"" + clientId + "\"," +
        "\"mediaId\":\"tt0000001\"," +
        "\"status\":\"DONE\"," +
        "\"rating\":5," +
        "\"comment\":\"This is a comment\"" +
        "}";
    String response2 = postRequest(jsonInputString, dynamoDBEndpoint + "/timeline", accessToken);
    assertThat(response2).contains("tt0000001");

    // DELETE the added test client and timeline item to clean up
    deleteRequest(dynamoDBEndpoint + "/timeline/" + clientId + "-tt0000001", accessToken);
    deleteRequest(dynamoDBEndpoint + "/client/" + clientId, accessToken);
  }

  /**
   * Test the timeline get functionality with a single test client and multiple test timelines. The
   * expected behavior is that all test timelines can be retrieved from the database by clientId
   */
  @Test
  @Order(4)
  void testTimelineGetByClientId() throws Exception {

    String testClientname = UUID.randomUUID().toString().replace("-", "") + "-testClientname";

    // POST request to add a new client
    String jsonInputString = "{\"" +
        "clientname\":\"" + testClientname + "\"," +
        "\"email\":\"   \"," +
        "\"accountSetting\":{" +
        "\"isPrivate\":false," +
        "\"isAdult\":true}" +
        "}";
    String response = postRequest(jsonInputString, dynamoDBEndpoint + "/client", null);

    // POST request to add a new timeline
    JSONObject jsonObject = new JSONObject(response);
    String clientId = jsonObject.getString("clientId");
    String accessToken = jsonObject.getString("accessToken");
    jsonInputString = "{\"" +
        "timelineId\":\"" + clientId + "-tt0000001\"," +
        "\"clientId\":\"" + clientId + "\"," +
        "\"mediaId\":\"tt0000001\"," +
        "\"status\":\"DONE\"," +
        "\"rating\":5," +
        "\"comment\":\"This is a comment\"" +
        "}";
    String response2 = postRequest(jsonInputString, dynamoDBEndpoint + "/timeline", accessToken);
    assert response2.contains("tt0000001");

    // POST request to add another timeline
    jsonInputString = "{\"" +
        "timelineId\":\"" + clientId + "-tt0000002\"," +
        "\"clientId\":\"" + clientId + "\"," +
        "\"mediaId\":\"tt0000002\"," +
        "\"status\":\"DONE\"," +
        "\"rating\":1," +
        "\"comment\":\"This is a comment\"" +
        "}";
    String response3 = postRequest(jsonInputString, dynamoDBEndpoint + "/timeline", accessToken);
    assertThat(response3).contains("tt0000002");

    // GET request to get all timelines by clientId
    String response4 = getRequest(dynamoDBEndpoint + "/timeline/client/" + clientId, accessToken);
    // Expected to return two timeline items with mediaId tt0000001 and tt0000002
    assertThat(response4).contains("tt0000001").contains("tt0000002");

    // DELETE the added test client and timeline item to clean up
    deleteRequest(dynamoDBEndpoint + "/timeline/" + clientId + "-tt0000001", accessToken);
    deleteRequest(dynamoDBEndpoint + "/timeline/" + clientId +  "-tt0000002", accessToken);
    deleteRequest(dynamoDBEndpoint + "/client/" + clientId, accessToken);
  }

  /**
   * Test the timeline get functionality with multiple test clients add timelines for the same media
   * (mediaId). For example, client1 adds a timeline for mediaId tt0000001, and client2 adds a timeline
   * for mediaId tt0000001. The expected behavior is that all test timelines can be retrieved from
   * the database by mediaId
   */
  @Test
  @Order(5)
  void testTimelineGetByMediaId() throws Exception {

    String testClientname1 = UUID.randomUUID().toString().replace("-", "") + "-testClientname";
    String testClientname2 = UUID.randomUUID().toString().replace("-", "") + "-testClientname";

    // POST request to add a new client
    String jsonInputString = "{\"" +
        "clientname\":\"" + testClientname1 + "\"," +
        "\"email\":\"   \"," +
        "\"accountSetting\":{" +
        "\"isPrivate\":false," +
        "\"isAdult\":true}" +
        "}";
    String response = postRequest(jsonInputString, dynamoDBEndpoint + "/client", null);

    // POST request to add a new timeline for client1
    JSONObject jsonObject = new JSONObject(response);
    String clientId1 = jsonObject.getString("clientId");
    String accessToken1 = jsonObject.getString("accessToken");
    jsonInputString = "{\"" +
        "timelineId\":\"" + clientId1 + "-tt0000001\"," +
        "\"clientId\":\"" + clientId1 + "\"," +
        "\"mediaId\":\"tt0000001\"," +
        "\"status\":\"DONE\"," +
        "\"rating\":5," +
        "\"comment\":\"This is a comment\"" +
        "}";
    String response2 = postRequest(jsonInputString, dynamoDBEndpoint + "/timeline", accessToken1);
    assertThat(response2).contains("tt0000001");

    // POST request to add a new client
    jsonInputString = "{\"" +
        "clientname\":\"" + testClientname2 + "\"," +
        "\"email\":\"   \"," +
        "\"accountSetting\":{" +
        "\"isPrivate\":false," +
        "\"isAdult\":true}" +
        "}";
    String response3 = postRequest(jsonInputString, dynamoDBEndpoint + "/client", null);

    // POST request to add a new timeline for client2
    JSONObject jsonObject2 = new JSONObject(response3);
    String clientId2 = jsonObject2.getString("clientId");
    String accessToken2 = jsonObject2.getString("accessToken");
    jsonInputString = "{\"" +
        "timelineId\":\"" + clientId2 + "-tt0000002\"," +
        "\"clientId\":\"" + clientId2 + "\"," +
        "\"mediaId\":\"tt0000002\"," +
        "\"status\":\"DONE\"," +
        "\"rating\":1," +
        "\"comment\":\"This is a comment\"" +
        "}";
    String response4 = postRequest(jsonInputString, dynamoDBEndpoint + "/timeline", accessToken2);

    String response5 = getRequest(dynamoDBEndpoint + "/timeline/media/" + "tt0000001", null);
    String response6 = getRequest(dynamoDBEndpoint + "/timeline/media/" + "tt0000002", null);
    assertThat(response5).contains("tt0000001").contains(clientId1);
    assertThat(response6).contains("tt0000002").contains(clientId2);

    // DELETE the added test client and timeline item to clean up
    deleteRequest(dynamoDBEndpoint + "/timeline/" + clientId1 + "-tt0000001", accessToken1);
    deleteRequest(dynamoDBEndpoint + "/timeline/" + clientId2 + "-tt0000002", accessToken2);
    deleteRequest(dynamoDBEndpoint + "/client/" + clientId1, accessToken1);
    deleteRequest(dynamoDBEndpoint + "/client/" + clientId2, accessToken2);
  }

  @Test
  @Order(6)
  void testTimelineGetTimelineByClientIdAndMediaId() throws Exception {

    String testClientname = UUID.randomUUID().toString().replace("-", "") + "-testClientname";

    // POST request to add a new client
    String jsonInputString = "{\"" +
        "clientname\":\"" + testClientname + "\"," +
        "\"email\":\"   \"," +
        "\"accountSetting\":{" +
        "\"isPrivate\":false," +
        "\"isAdult\":true}" +
        "}";
    String response = postRequest(jsonInputString, dynamoDBEndpoint + "/client", null);

    // POST request to add a new timeline
    JSONObject jsonObject = new JSONObject(response);
    String clientId = jsonObject.getString("clientId");
    String accessToken = jsonObject.getString("accessToken");
    jsonInputString = "{\"" +
        "timelineId\":\"" + clientId + "-tt0000001\"," +
        "\"clientId\":\"" + clientId + "\"," +
        "\"mediaId\":\"tt0000001\"," +
        "\"status\":\"DONE\"," +
        "\"rating\":5," +
        "\"comment\":\"This is a comment\"" +
        "}";
    String response2 = postRequest(jsonInputString, dynamoDBEndpoint + "/timeline", accessToken);

    // GET request to retrieve the added timeline by clientId and mediaId
    String response3 = getRequest(dynamoDBEndpoint + "/timeline/" + clientId + "/" + "tt0000001",
        accessToken);
    assertThat(response3).contains("tt0000001");

    // DELETE the added test client and timeline item to clean up
    deleteRequest(dynamoDBEndpoint + "/timeline/" + clientId + "-tt0000001", accessToken);
    deleteRequest(dynamoDBEndpoint + "/client/" + clientId, accessToken);
  }
}