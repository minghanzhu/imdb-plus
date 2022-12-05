package org.opencsd.imdbplus.repository;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import org.opencsd.imdbplus.entity.Media;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MediaRepository {

  private static final Logger mediaLogger = LoggerFactory.getLogger(MediaRepository.class);

  @Autowired
  private DynamoDBMapper dbObjMapper;


  public Media saveMedia(Media item) {
    MediaRepository.mediaLogger.debug("Trying to save entry");
    try {
      if (!itemExists(item, item.getMediaId())) {
        dbObjMapper.save(item);
        MediaRepository.mediaLogger.info("Added new media: " + item.getTitle() + " Id: " + item.getMediaId());
        return item;
      } else
          return null;
    } catch (ResourceNotFoundException e) {
      return null;
    }
  }

  public Media getEntity(String entityId) {
    return dbObjMapper.load(Media.class, entityId);
  }

  public List<Media> getAllMedia(){
    return dbObjMapper.scan(Media.class, new DynamoDBScanExpression());
  }

  public String delete(String entityId) {
    Media media = dbObjMapper.load(Media.class, entityId);
    dbObjMapper.delete(media);
    return "Media deleted successfully";
  }

  public String update(String entityId, Media item) {
    dbObjMapper.save(item, new DynamoDBSaveExpression()
        .withExpectedEntry("mediaId",
            new ExpectedAttributeValue(
                new AttributeValue().withS(entityId)
            )));
    return entityId;
  }

  private boolean itemExists(Media item, String attributeValue) {
    HashMap<String, AttributeValue> attrMap = new HashMap<>();
    attrMap.put(":val1", new AttributeValue().withS(item.getMediaId()));
    DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
        .withFilterExpression("mediaId = :val1")
        .withExpressionAttributeValues(attrMap);

    List<Media> replies = dbObjMapper.scan(Media.class, scanExpression);
    return replies.size() != 0;
  }


}