package org.opencsd.imdbplus.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import org.opencsd.imdbplus.entity.Media;
import org.opencsd.imdbplus.entity.Timeline;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AnalysisRepository {

  private static final Logger analysisLogger = LoggerFactory.getLogger(AnalysisRepository.class);
  @Autowired
  private DynamoDBMapper dynamoDBMapper;
  public void setDynamoDBMapper(DynamoDBMapper dynamoDBMapper){
    this.dynamoDBMapper = dynamoDBMapper;
  }

  public List<Timeline> getAllTimelines(){
    return dynamoDBMapper.scan(Timeline.class, new DynamoDBScanExpression());
  }

  public Media getMedia(String mediaId){
    return dynamoDBMapper.load(Media.class, mediaId);
  }
}
