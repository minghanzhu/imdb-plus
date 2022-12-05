package org.opencsd.imdbplus.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opencsd.imdbplus.entity.Timeline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class TimelineRepositoryTest {

  @MockBean
  DynamoDBMapper mockDynamo;

  @Autowired
  private TimelineRepository timelineRepository;


  private Timeline testLine;
  private PaginatedScanList<Timeline> mediaTimelines;

  private PaginatedScanList<Timeline> userTimelines;


  @BeforeEach
  void setUp() {
    timelineRepository = new TimelineRepository();
    mockDynamo = Mockito.mock(DynamoDBMapper.class);
    timelineRepository.setDynamoDBMapper(mockDynamo);

    testLine = new Timeline("t1-u1-m1", "u1", "m1", new Date(), new Date(), "DONE", 5, "It was great");

    Timeline t1 = new Timeline("t1-u1-m1", "u1", "m1", new Date(), new Date(), "DONE", 5, "It was great");
    Timeline t2 = new Timeline("t2-u1-m1", "u1", "m4", new Date(), new Date(), "DONE", 1, "It was terrible");
    Timeline t3 = new Timeline("t3-u3-m3", "u3", "m3", new Date(), new Date(), "PROGRESS", 3, "Still in progress.");
    Timeline t4 = new Timeline("t4-u2-m1", "u2", "m1", new Date(), new Date(), "WISHLIST", 5, "I head this was great.");
    Timeline t5 = new Timeline("t5-u1-m4", "u1", "m5", new Date(), new Date(), "WISHLIST", 0, "I REALLY WANT TO SEE IT.");

  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void save() {
    Timeline testLine = new Timeline("t1-u1-m1", "u1", "m1", new Date(), new Date(), "DONE", 5, "It was great");
    String userToken = "8ed4cea1-eee6-41bc-97f1-12a6095b51aa";
    mockDynamo.save(testLine);

    Timeline result =  timelineRepository.save(testLine);
    assertEquals(testLine, result);
  }

  @Test
  void getTimeline() {
    when(mockDynamo.load(Timeline.class, "t1-u1-m1")).thenReturn(testLine);
    Timeline result = timelineRepository.getTimeline("t1-u1-m1");
    assertEquals(testLine, result);
  }

  @Test
  void delete() {
    mockDynamo.delete(testLine);
    Timeline result = timelineRepository.getTimeline("t1-u1-m1");
    assertEquals(result, null);
  }

  @Test
  void update() {
    Timeline testLine = new Timeline("t1-u1-m1", "u1", "m1", new Date(), new Date(), "DONE", 3, "It was great");
    String userToken = "8ed4cea1-eee6-41bc-97f1-12a6095b51aa";
    mockDynamo.save(testLine);

    Timeline result =  timelineRepository.update(testLine);
    assertEquals(testLine, result);
  }

  @Test
  void getTimelineByUserId() {
    HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
    eav.put(":v1", new AttributeValue().withS("u2"));
    DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
        .withFilterExpression("userId = :v1")
        .withExpressionAttributeValues(eav);

    when(mockDynamo.scan(Timeline.class, scanExpression)).thenReturn(userTimelines);

    List<Timeline> result = timelineRepository.getTimelineByUserId("u2");
    assert result.isEmpty();
    assertEquals(new ArrayList<>(), result);
  }

  @Test
  void getTimelineByMediaId() {
    HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
    eav.put(":v1", new AttributeValue().withS("m1"));
    DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
        .withFilterExpression("mediaId = :v1")
        .withExpressionAttributeValues(eav);

    when(mockDynamo.scan(Timeline.class, scanExpression)).thenReturn(mediaTimelines);

    List<Timeline> result = timelineRepository.getTimelineByMediaId("m1");
    assert result.isEmpty();
    assertEquals(new ArrayList<>(), result);
  }

  @Test
  void getTimelineByUserIdAndMediaId() {
    when(mockDynamo.load(Timeline.class, "u1-m1")).thenReturn(testLine);

    Timeline result = timelineRepository.getTimelineByUserIdAndMediaId("u1", "m1");
    assertEquals(testLine, result);
  }
}