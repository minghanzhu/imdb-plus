package org.opencsd.imdbplus.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.opencsd.imdbplus.entity.Media;
import org.opencsd.imdbplus.entity.Timeline;

import java.util.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

@SpringBootTest
class AnalysisRepositoryTest {
  private Logger analysisLoggertest = LoggerFactory.getLogger(AnalysisRepository.class);

  @Autowired
  private AnalysisRepository analysisRepository;
  @MockBean
  private DynamoDBMapper mockDynamo;
  private List<Timeline> allTimelines;
  private List<Timeline> doneTimelines;
  private List<Timeline> progressTimelines;
  private List<Timeline> wishTimelines;
  private PaginatedScanList<Timeline> emptyList;
  private Media m1;

  @BeforeEach
  void setUp() {
    analysisRepository = new AnalysisRepository();
    mockDynamo = mock(DynamoDBMapper.class);
    analysisRepository.setDynamoDBMapper(mockDynamo);

    m1 =  new Media("m1", "Movie 1", "2012-07-17", "Action");

    Timeline t1 = new Timeline("t1-u1-m1", "u1", "m1", new Date(), new Date(), "DONE", 5, "It was great");
    Timeline t2 = new Timeline("t2-u1-m1", "u1", "m4", new Date(), new Date(), "DONE", 1, "It was terrible");
    Timeline t6 = new Timeline("t1-u1-m1", "u1", "m3", new Date(), new Date(), "DONE", 5, "It was great");
    Timeline t7 = new Timeline("t2-u1-m1", "u1", "m1", new Date(), new Date(), "DONE", 1, "It was terrible");
    Timeline t4 = new Timeline("t4-u2-m1", "u2", "m1", new Date(), new Date(), "WISHLIST", 5, "I head this was great.");
    Timeline t5 = new Timeline("t5-u1-m4", "u1", "m5", new Date(), new Date(), "WISHLIST", 0, "I REALLY WANT TO SEE IT.");
    Timeline t9 = new Timeline("t4-u2-m1", "u2", "m5", new Date(), new Date(), "WISHLIST", 5, "I head this was great.");
    Timeline t10 = new Timeline("t5-u1-m4", "u1", "m5", new Date(), new Date(), "WISHLIST", 0, "I REALLY WANT TO SEE IT.");
    Timeline t3 = new Timeline("t3-u3-m3", "u3", "m2", new Date(), new Date(), "PROGRESS", 5, "Still in progress.");
    Timeline t8 = new Timeline("t3-u3-m3", "u3", "m1", new Date(), new Date(), "PROGRESS", 2, "Still in progress.");
    Timeline t11 = new Timeline("t3-u3-m3", "u4", "m2", new Date(), new Date(), "PROGRESS", 5, "Still in progress.");

    allTimelines = Arrays.asList(t1, t2, t3, t4, t5, t5, t6, t7, t8, t9, t10, t11);
    doneTimelines = Arrays.asList(t1, t2, t6, t7);
    progressTimelines = Arrays.asList(t3, t8, t11);
    wishTimelines = Arrays.asList(t4,t5, t9, t10);
  }

  @AfterEach
  void tearDown() {

  }

  @Test
  void getTimelineListByByFilter() {
    HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
    eav.put(":v1", new AttributeValue().withS("DONE"));
    DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
        .withFilterExpression("mediaId = :v1")
        .withExpressionAttributeValues(eav);

    when(mockDynamo.scan(Timeline.class, scanExpression)).thenReturn(emptyList);
    List<Timeline> result = analysisRepository.getTimelineListByByFilter("statue","DONE");
    assertEquals(new ArrayList<>(), result);
  }

  @Test
  void getAllTimelines(){
    when(mockDynamo.scan(Timeline.class, new DynamoDBScanExpression())).thenReturn(emptyList);
    List<Timeline> result = analysisRepository.getAllTimelines();
    assertEquals(emptyList, result);
  }
  @Test
  void getMedia(){
    when(mockDynamo.load(Media.class, "m1")).thenReturn(m1);

    Media media = analysisRepository.getMedia("u1");
    assertEquals(null, media);
  }

}

