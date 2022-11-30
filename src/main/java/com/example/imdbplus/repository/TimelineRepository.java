package com.example.imdbplus.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.example.imdbplus.entity.Timeline;
import com.example.imdbplus.entity.User;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

@Repository
public class TimelineRepository {

  @Autowired
  private DynamoDBMapper dynamoDBMapper;

  public Timeline save(Timeline timeline, String accessToken) {
    // Update creation time
    String creationTime = String.valueOf(System.currentTimeMillis());
    timeline.setCreationTime(creationTime);
    // Check if the user exists and the access token is valid
    String userId = timeline.getUserId();
    User user = dynamoDBMapper.load(User.class, userId);
    if (user.getAccessToken().equals(accessToken)) {
      dynamoDBMapper.save(timeline);
      return timeline;
    } else {
      return null;
    }
  }

  public Timeline getTimeline(String userId, String mediaId) {
    return dynamoDBMapper.load(Timeline.class, userId + mediaId);
  }

  public ResponseEntity delete(String userId, String mediaId, String accessToken) {
    // Check if the user exists and the access token is valid
    User user = dynamoDBMapper.load(User.class, userId);
    if (!user.getAccessToken().equals(accessToken)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid access token");
    }
    // Check if the timeline exists
    Timeline timeline = dynamoDBMapper.load(Timeline.class, userId + "-" + mediaId);
    if (timeline == null) {
      return ResponseEntity.status(404).body("Timeline not found");
    }
    dynamoDBMapper.delete(timeline);
    return ResponseEntity.ok().body("Timeline deleted successfully");
  }

  public void update(Timeline timeline) {
    dynamoDBMapper.save(timeline);
  }

  public ResponseEntity getTimelineByUserId(String userId) {
    // scan the timeline table to get all timelines of the user
    HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
    eav.put(":v1", new AttributeValue().withS(userId));
    DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
        .withFilterExpression("userId = :v1")
        .withExpressionAttributeValues(eav);
    List<Timeline> timelines = dynamoDBMapper.scan(Timeline.class, scanExpression);
    if (timelines.size() > 0) {
      return ResponseEntity.ok(timelines);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Timeline not found");
    }
  }

  public ResponseEntity getTimelineByMediaId(String mediaId) {
    // scan the timeline table to get all timelines of the media
    HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
    eav.put(":v1", new AttributeValue().withS(mediaId));
    DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
        .withFilterExpression("mediaId = :v1")
        .withExpressionAttributeValues(eav);
    List<Timeline> timelines = dynamoDBMapper.scan(Timeline.class, scanExpression);
    if (timelines.size() > 0) {
      return ResponseEntity.ok(timelines);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Timeline not found");
    }
  }

  public ResponseEntity getTimelineByUserIdAndMediaId(String userId, String mediaId) {
    Timeline timeline = dynamoDBMapper.load(Timeline.class, userId + "-" + mediaId);
    if (timeline != null) {
      return ResponseEntity.ok(timeline);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Timeline not found");
    }
  }
}
