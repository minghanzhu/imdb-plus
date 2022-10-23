package com.example.imdbplus;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@SpringBootTest
class ImdbPlusApplicationTests {

    static String dynamoDBEndpoint = "http://localhost:8083";

    public static String postRequest(String jsonInputString, String urlString, String accessToken) throws IOException {
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

    @Test
    void contextLoads() {
    }

    /**
     * Test the user sign up functionality with a single test user.
     * The expected result is that the test user is added to the database.
     */
    @Test
    void testUserSave() throws Exception {

        String testUsername = UUID.randomUUID().toString().replace("-", "") + "-testUsername";

        // POST request to add a new user
        String jsonInputString = "{\"" +
                "username\":\"" + testUsername + "\"," +
                "\"email\":\"   \"," +
                "\"accountSetting\":{" +
                "\"isPrivate\":false," +
                "\"isAdult\":true}" +
                "}";
        String response = postRequest(jsonInputString, dynamoDBEndpoint + "/user", null);

        JSONObject jsonObject = new JSONObject(response);
        String username = jsonObject.getString("username");
        assert username.equals(testUsername);

        // DELETE the added test user to clean up
        String userId = jsonObject.getString("userId");
        String accessToken = jsonObject.getString("accessToken");
        deleteRequest(dynamoDBEndpoint + "/user/" + userId, accessToken);
    }

    /**
     * Test the user sign up functionality with duplicate usernames.
     * The expected behavior is that the second test user with the same username should not be added to the database.
     */
    @Test
    void testUserSaveDuplicatedUsername() throws Exception {

        String testUsername = UUID.randomUUID().toString().replace("-", "") + "-testUsername";

        // POST request to add a new user
        String jsonInputString = "{\"" +
                "username\":\"" + testUsername + "\"," +
                "\"email\":\"   \"," +
                "\"accountSetting\":{" +
                "\"isPrivate\":false," +
                "\"isAdult\":true}" +
                "}";
        String response = postRequest(jsonInputString, dynamoDBEndpoint + "/user", null);

        // Assert POST request to add a new user with the same username should return HTTP 400
        String response2 = null;
        try {
            response2 = postRequest(jsonInputString, dynamoDBEndpoint + "/user", null);
        } catch (IOException e) {
            response2 = e.getMessage();
            assert response2.contains("400");
        }

        // DELETE the added test user to clean up
        JSONObject jsonObject = new JSONObject(response);
        String userId = jsonObject.getString("userId");
        String accessToken = jsonObject.getString("accessToken");
        deleteRequest(dynamoDBEndpoint + "/user/" + userId, accessToken);
    }

    /**
     * Test the timeline save functionality with a single test user and a single test timeline.
     * The expected behavior is that the test timeline is added to the database.
     */
    @Test
    void testTimelineSave() throws Exception {

        String testUsername = UUID.randomUUID().toString().replace("-", "") + "-testUsername";

        // POST request to add a new user
        String jsonInputString = "{\"" +
                "username\":\"" + testUsername + "\"," +
                "\"email\":\"   \"," +
                "\"accountSetting\":{" +
                "\"isPrivate\":false," +
                "\"isAdult\":true}" +
                "}";
        String response = postRequest(jsonInputString, dynamoDBEndpoint + "/user", null);

        // POST request to add a new timeline
        JSONObject jsonObject = new JSONObject(response);
        String userId = jsonObject.getString("userId");
        String accessToken = jsonObject.getString("accessToken");
        jsonInputString = "{\"" +
                "timelineId\":\"" + userId + "-tt0000001\"," +
                "\"userId\":\"" + userId + "\"," +
                "\"mediaId\":\"tt0000001\"," +
                "\"status\":\"DONE\"," +
                "\"rating\":5," +
                "\"comment\":\"This is a comment\"" +
                "}";
        String response2 = postRequest(jsonInputString, dynamoDBEndpoint + "/timeline", accessToken);
        assert response2.contains("tt0000001");

        // DELETE the added test user and timeline item to clean up
        deleteRequest(dynamoDBEndpoint + "/timeline/" + userId + "/" + "tt0000001", accessToken);
        deleteRequest(dynamoDBEndpoint + "/user/" + userId, accessToken);
    }

    /**
     * Test the timeline get functionality with a single test user and multiple test timelines.
     * The expected behavior is that all test timelines can be retrieved from the database by userId
     */
    @Test
    void testTimelineGetByUserId() throws Exception {

        String testUsername = UUID.randomUUID().toString().replace("-", "") + "-testUsername";

        // POST request to add a new user
        String jsonInputString = "{\"" +
                "username\":\"" + testUsername + "\"," +
                "\"email\":\"   \"," +
                "\"accountSetting\":{" +
                "\"isPrivate\":false," +
                "\"isAdult\":true}" +
                "}";
        String response = postRequest(jsonInputString, dynamoDBEndpoint + "/user", null);

        // POST request to add a new timeline
        JSONObject jsonObject = new JSONObject(response);
        String userId = jsonObject.getString("userId");
        String accessToken = jsonObject.getString("accessToken");
        jsonInputString = "{\"" +
                "timelineId\":\"" + userId + "-tt0000001\"," +
                "\"userId\":\"" + userId + "\"," +
                "\"mediaId\":\"tt0000001\"," +
                "\"status\":\"DONE\"," +
                "\"rating\":5," +
                "\"comment\":\"This is a comment\"" +
                "}";
        String response2 = postRequest(jsonInputString, dynamoDBEndpoint + "/timeline", accessToken);
        assert response2.contains("tt0000001");

        // POST request to add another timeline
        jsonInputString = "{\"" +
                "timelineId\":\"" + userId + "-tt0000002\"," +
                "\"userId\":\"" + userId + "\"," +
                "\"mediaId\":\"tt0000002\"," +
                "\"status\":\"DONE\"," +
                "\"rating\":1," +
                "\"comment\":\"This is a comment\"" +
                "}";
        String response3 = postRequest(jsonInputString, dynamoDBEndpoint + "/timeline", accessToken);
        assert response3.contains("tt0000002");

        // GET request to get all timelines by userId
        String response4 = getRequest(dynamoDBEndpoint + "/timeline/user/" + userId, accessToken);
        // Expected to return two timeline items with mediaId tt0000001 and tt0000002
        assert response4.contains("tt0000001");
        assert response4.contains("tt0000002");

        // DELETE the added test user and timeline item to clean up
        deleteRequest(dynamoDBEndpoint + "/timeline/" + userId + "/" + "tt0000001", accessToken);
        deleteRequest(dynamoDBEndpoint + "/timeline/" + userId + "/" + "tt0000002", accessToken);
        deleteRequest(dynamoDBEndpoint + "/user/" + userId, accessToken);
    }

    /**
     * Test the timeline get functionality with multiple test users add timelines for the same media (mediaId).
     * For example, user1 adds a timeline for mediaId tt0000001, and user2 adds a timeline for mediaId tt0000001.
     * The expected behavior is that all test timelines can be retrieved from the database by mediaId
     */
    @Test
    void testTimelineGetByMediaId() throws Exception {

        String testUsername1 = UUID.randomUUID().toString().replace("-", "") + "-testUsername";
        String testUsername2 = UUID.randomUUID().toString().replace("-", "") + "-testUsername";

        // POST request to add a new user
        String jsonInputString = "{\"" +
                "username\":\"" + testUsername1 + "\"," +
                "\"email\":\"   \"," +
                "\"accountSetting\":{" +
                "\"isPrivate\":false," +
                "\"isAdult\":true}" +
                "}";
        String response = postRequest(jsonInputString, dynamoDBEndpoint + "/user", null);

        // POST request to add a new timeline for user1
        JSONObject jsonObject = new JSONObject(response);
        String userId1 = jsonObject.getString("userId");
        String accessToken1 = jsonObject.getString("accessToken");
        jsonInputString = "{\"" +
                "timelineId\":\"" + userId1 + "-tt0000001\"," +
                "\"userId\":\"" + userId1 + "\"," +
                "\"mediaId\":\"tt0000001\"," +
                "\"status\":\"DONE\"," +
                "\"rating\":5," +
                "\"comment\":\"This is a comment\"" +
                "}";
        String response2 = postRequest(jsonInputString, dynamoDBEndpoint + "/timeline", accessToken1);
        assert response2.contains("tt0000001");

        // POST request to add a new user
        jsonInputString = "{\"" +
                "username\":\"" + testUsername2 + "\"," +
                "\"email\":\"   \"," +
                "\"accountSetting\":{" +
                "\"isPrivate\":false," +
                "\"isAdult\":true}" +
                "}";
        String response3 = postRequest(jsonInputString, dynamoDBEndpoint + "/user", null);

        // POST request to add a new timeline for user2
        JSONObject jsonObject2 = new JSONObject(response3);
        String userId2 = jsonObject2.getString("userId");
        String accessToken2 = jsonObject2.getString("accessToken");
        jsonInputString = "{\"" +
                "timelineId\":\"" + userId2 + "-tt0000002\"," +
                "\"userId\":\"" + userId2 + "\"," +
                "\"mediaId\":\"tt0000002\"," +
                "\"status\":\"DONE\"," +
                "\"rating\":1," +
                "\"comment\":\"This is a comment\"" +
                "}";
        String response4 = postRequest(jsonInputString, dynamoDBEndpoint + "/timeline", accessToken2);

        String response5 = getRequest(dynamoDBEndpoint + "/timeline/media/" + "tt0000001", null);
        String response6 = getRequest(dynamoDBEndpoint + "/timeline/media/" + "tt0000002", null);
        assert response5.contains("tt0000001") && response5.contains(userId1)
                && response6.contains("tt0000002") && response6.contains(userId2);

        // DELETE the added test user and timeline item to clean up
        deleteRequest(dynamoDBEndpoint + "/timeline/" + userId1 + "/" + "tt0000001", accessToken1);
        deleteRequest(dynamoDBEndpoint + "/timeline/" + userId2 + "/" + "tt0000002", accessToken2);
        deleteRequest(dynamoDBEndpoint + "/user/" + userId1, accessToken1);
        deleteRequest(dynamoDBEndpoint + "/user/" + userId2, accessToken2);
    }

    @Test
    void testTimelineGetTimelineByUserIdAndMediaId() throws Exception {

        String testUsername = UUID.randomUUID().toString().replace("-", "") + "-testUsername";

        // POST request to add a new user
        String jsonInputString = "{\"" +
                "username\":\"" + testUsername + "\"," +
                "\"email\":\"   \"," +
                "\"accountSetting\":{" +
                "\"isPrivate\":false," +
                "\"isAdult\":true}" +
                "}";
        String response = postRequest(jsonInputString, dynamoDBEndpoint + "/user", null);

        // POST request to add a new timeline
        JSONObject jsonObject = new JSONObject(response);
        String userId = jsonObject.getString("userId");
        String accessToken = jsonObject.getString("accessToken");
        jsonInputString = "{\"" +
                "timelineId\":\"" + userId + "-tt0000001\"," +
                "\"userId\":\"" + userId + "\"," +
                "\"mediaId\":\"tt0000001\"," +
                "\"status\":\"DONE\"," +
                "\"rating\":5," +
                "\"comment\":\"This is a comment\"" +
                "}";
        String response2 = postRequest(jsonInputString, dynamoDBEndpoint + "/timeline", accessToken);

        // GET request to retrieve the added timeline by userId and mediaId
        String response3 = getRequest(dynamoDBEndpoint + "/timeline/" + userId + "/" + "tt0000001", accessToken);
        assert response3.contains("tt0000001");

        // DELETE the added test user and timeline item to clean up
        deleteRequest(dynamoDBEndpoint + "/timeline/" + userId + "/" + "tt0000001", accessToken);
        deleteRequest(dynamoDBEndpoint + "/user/" + userId, accessToken);
    }
}
