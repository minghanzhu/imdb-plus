package org.opencsd.imdbplus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TestHelpers {

  public String postRequest(
      String jsonInputString, String urlString, String accessToken)
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
      while ((responseLine = br.readLine()) != null)
        response.append(responseLine.trim());
      return response.toString();
    }
  }

  public String getRequest(String urlString, String accessToken) throws IOException {
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
      while ((responseLine = br.readLine()) != null)
        response.append(responseLine.trim());
      return response.toString();
    }
  }

  public String deleteRequest(String urlString, String accessToken) throws IOException {
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
      while ((responseLine = br.readLine()) != null)
        response.append(responseLine.trim());
      return response.toString();
    }
  }

}