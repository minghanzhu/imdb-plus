package com.example.imdbplus.AnalysisTests;

import static org.springframework.test.util.AssertionErrors.assertEquals;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.example.imdbplus.entity.Media;
import com.example.imdbplus.entity.Timeline;
import com.example.imdbplus.repository.TimelineRepository;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TimelineAnalysisSuite {

  private static final Logger timelineSuiteLogger = LoggerFactory.getLogger(
      TimelineAnalysisSuite.class);
  @Autowired
  private DynamoDBMapper dbInstance;
  private static final String serviceBaseAddress = "http://localhost:8083";

  @MockBean
  private TimelineRepository mockTimeline;

  private List<Timeline> timelineList;
  private final Media media1 = new Media("tt0000001", "The Watcher", "2012-07-17", "Action");
  private final Media media2 = new Media("tt1396484", "Compuational Heaven", "2032-07-17", "Drama");

  /*
   * Test Highest Rating Function
   * */
  @Test
  public void testHighestRating() throws Exception {
    ResponseEntity result = mockTimeline.getHighestRating();
    Mockito.when(mockTimeline.getHighestRating()).thenReturn(result);
    JSONObject expected = new JSONObject(result.getBody().toString());
    assertEquals("Should be: ", expected.getString("mediaId"), media1.getMediaId());
  }

  @Test
  public void testGetMostDone() throws JSONException {
    ResponseEntity result = mockTimeline.getMost("DONE");
    JSONObject expected = new JSONObject(result.toString());
    assertEquals("Should be: ", expected.getString("mediaId"), media1.getMediaId());
  }

  @Test
  public void testGetMostWished() throws JSONException {
    ResponseEntity result = mockTimeline.getMost("WISHLIST");
    JSONObject expected = new JSONObject(result.toString());
    assertEquals("Should be: ", expected.getString("mediaId"), media1.getMediaId());
  }

  @Test
  public void testGetMostProgress() throws JSONException {
    ResponseEntity result = mockTimeline.getMost("PROGRESS");
    JSONObject expected = new JSONObject(result.toString());
    assertEquals("Should be: ", expected.getString("mediaId"), null);
  }

  @Test
  public void testCountMedia() throws Exception {
    //TODO
  }

  @Test
  public void testCalculateMost() {
    Media result = mockTimeline.calculateMost("DONE");
    assertEquals("Expected medai", result.getMediaId(), media1.getMediaId());
  }

  @Test
  public void testTopTenWatched() throws JSONException {
    ResponseEntity result = mockTimeline.getTopTenMost("DONE");
    StringBuilder actual = new StringBuilder(media1.toString());
    actual.append(media2.toString());
    JSONObject expected = new JSONObject(result.toString());
    assertEquals("Should be: ", expected.toString(), actual.toString());
  }

  @Test
  public void testTopTenWished() throws JSONException {
    ResponseEntity result = mockTimeline.getTopTenMost("WISHLIST");
    JSONObject expected = new JSONObject(result.toString());
    assertEquals("Should be: ", expected.toString(), media1.getMediaId());
  }

  @Test
  public void testTopTenProgress() throws JSONException {
    ResponseEntity result = mockTimeline.getTopTenMost("WISHLIST");
    JSONObject expected = new JSONObject(result.toString());
    assertEquals("Should be: ", expected.toString(), null);
  }


}
