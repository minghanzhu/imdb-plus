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

    public static String postRequest(String jsonInputString, String urlString) throws IOException {
        URL url = new URL(urlString);
        java.net.HttpURLConnection con = (java.net.HttpURLConnection) url.openConnection();
        try {
            con.setRequestMethod("POST");
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        }
        con.setRequestProperty("Content-Type", "application/json");
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

    public static String deleteRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        java.net.HttpURLConnection con = (java.net.HttpURLConnection) url.openConnection();
        try {
            con.setRequestMethod("DELETE");
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        }
        con.setRequestProperty("Content-Type", "application/json");
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

}
