package org.opencsd.imdbplus.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.opencsd.imdbplus.entity.Timeline;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TimelineRepository {

  @Autowired
  private DynamoDBMapper dynamoDBMapper;

  public Timeline save(Timeline timeline) {
    // Check if the user exists and the access token is valid
    try {
      dynamoDBMapper.save(timeline);
      return timeline;
    }catch (DynamoDBMappingException e){
      return null;
    }
  }

  public Timeline getTimeline(String timelineId) {
    return dynamoDBMapper.load(Timeline.class, timelineId);
  }

  public void delete(String timelineId) {
    // Check if the user exists and the access token is valid
    dynamoDBMapper.delete(timelineId);
  }

  public void update(Timeline timeline) {
    dynamoDBMapper.save(timeline);
  }

  public List<Timeline> getTimelineByUserId(String userId) {
    // scan the timeline table to get all timelines of the user
    HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
    eav.put(":v1", new AttributeValue().withS(userId));
    DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
        .withFilterExpression("userId = :v1")
        .withExpressionAttributeValues(eav);
    List<Timeline> timelines = dynamoDBMapper.scan(Timeline.class, scanExpression);
    if (timelines.size() > 0) {
      return timelines;
    } else {
      return null;
    }
  }

  public List<Timeline> getTimelineByMediaId(String mediaId) {
    // scan the timeline table to get all timelines of the media
    HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
    eav.put(":v1", new AttributeValue().withS(mediaId));
    DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
        .withFilterExpression("mediaId = :v1")
        .withExpressionAttributeValues(eav);
    List<Timeline> timelines = dynamoDBMapper.scan(Timeline.class, scanExpression);
    if (timelines.size() > 0) {
      return timelines;
    } else {
      return null;
    }
  }

  public Timeline getTimelineByUserIdAndMediaId(String userId, String mediaId) {
    Timeline timeline = dynamoDBMapper.load(Timeline.class, userId + "-" + mediaId);
    if (timeline != null) {
      return timeline;
    } else {
      return null;
    }
  }
}