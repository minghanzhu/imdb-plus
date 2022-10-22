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
                "\"isPrivate\":true," +
                "\"isAdult\":false}" +
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
                "\"isPrivate\":true," +
                "\"isAdult\":false}" +
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
                "\"isPrivate\":true," +
                "\"isAdult\":false}" +
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
}
