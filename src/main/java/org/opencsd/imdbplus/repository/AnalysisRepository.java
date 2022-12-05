package org.opencsd.imdbplus.repository;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.opencsd.imdbplus.entity.Media;
import org.opencsd.imdbplus.entity.Timeline;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

@Repository
public class AnalysisRepository {

  private static final Logger analysisLogger = LoggerFactory.getLogger(AnalysisRepository.class);
  @Autowired
  private DynamoDBMapper dynamoDBMapper;
  public void setDynamoDBMapper(DynamoDBMapper dynamoDBMapper){
    this.dynamoDBMapper = dynamoDBMapper;
  }
  public List<Timeline> getTimelineListByByFilter(String filterKey) {
    // scan the timeline table to get all timelines of the media
    HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
    eav.put(":v1", new AttributeValue().withS(filterKey));
    StringBuilder filter = new StringBuilder(filterKey);
    filter.append(" = :v1");

    DynamoDBScanExpression expression = new DynamoDBScanExpression()
        .withFilterExpression(filter.toString())
        .withExpressionAttributeValues(eav);
    List<Timeline> timelines = dynamoDBMapper.scan(Timeline.class, expression);
    if (timelines != null && !timelines.isEmpty()) {
      analysisLogger.info("retrieved: {size} timelines that matched {filter_key}", timelines.size(), filterKey);
      return timelines;
    } else {
      return new ArrayList<>();
    }
  }

  public List<Timeline> getAllTimelines(){
    return dynamoDBMapper.scan(Timeline.class, new DynamoDBScanExpression());
  }

  public Media getMedia(String mediaId){
    return dynamoDBMapper.load(Media.class, mediaId);
  }
}
