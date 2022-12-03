package org.opencsd.imdbplus.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.opencsd.imdbplus.entity.Timeline;
import org.opencsd.imdbplus.entity.User;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TimelineRepository {

  @Autowired
  private DynamoDBMapper dynamoDBMapper;

  @Autowired
  private UserRepository userRepository;

  public Timeline save(Timeline timeline, String accessToken) {
    // Update creation time
    String creationTime = String.valueOf(System.currentTimeMillis());
    timeline.setCreationTime(creationTime);
    // Check if the user exists and the access token is valid
    String userId = timeline.getUserId();
    User user = userRepository.getUser(userId);
    if (user.getAccessToken().equals(accessToken)) {
      dynamoDBMapper.save(timeline);
      return timeline;
    } else {
      return null;
    }
  }

  public String delete(String userId, String mediaId, String accessToken) {
    // Check if the user exists and the access token is valid
    User user = userRepository.getUser(userId);
    if (!user.getAccessToken().equals(accessToken)) {
      return "Invalid access token";
    }
    // Check if the timeline exists
    Timeline timeline = getTimelineByUserIdAndMediaId(userId, mediaId);
    if (timeline == null) {
      return "Timeline does not exist";
    }
    dynamoDBMapper.batchDelete(timeline);
    return "Timeline deleted successfully";
  }

  public void update(Timeline timeline) {
    dynamoDBMapper.save(timeline);
  }

  public List<Timeline> getTimelineByUserId(String userId) {
    // scan the timeline table to get all timelines of the user
    HashMap<String, AttributeValue> eav = new HashMap<>();
    eav.put(":v1", new AttributeValue().withS(userId));
    DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
        .withFilterExpression("userId = :v1")
        .withExpressionAttributeValues(eav);
    return dynamoDBMapper.scan(Timeline.class, scanExpression);
  }

  public List<Timeline> getTimelineByMediaId(String mediaId) {
    // scan the timeline table to get all timelines of the media
    HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
    eav.put(":v1", new AttributeValue().withS(mediaId));
    DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
        .withFilterExpression("mediaId = :v1")
        .withExpressionAttributeValues(eav);
    return dynamoDBMapper.scan(Timeline.class, scanExpression);
  }

  public Timeline getTimelineByUserIdAndMediaId(String userId, String mediaId) {
    String timelineId = userId + "-" + mediaId;
    HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
    eav.put(":v1", new AttributeValue().withS(timelineId));
    DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
        .withFilterExpression("timelineId = :v1")
        .withExpressionAttributeValues(eav);
    List<Timeline> replies = dynamoDBMapper.scan(Timeline.class, scanExpression);
    if (replies.isEmpty()) {
      return null;
    } else {
      return replies.get(0);
    }
  }
}