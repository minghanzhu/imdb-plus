package org.opencsd.imdbplus.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGenerateStrategy;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedTimestamp;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 Sample timeline item JSON (don't include creationTime):
 {
 "timelineId": "0b5dedb5-4b04-4c36-adda-e748e60bc20a-tt0000001",
 "clientId": "ff5428e8-ae95-4b7c-ab16-9a602df182e4",
 "mediaId": "tt0000001",
 "status": "DONE",
 "rating": 5,
 "comment": "This is a comment"
 }
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "timeline")
public class Timeline {

  // timelineId is the primary key, it is a combination of clientId and mediaId (e.g. ff5428e8-ae95-4b7c-ab16-9a602df182e4-tt0000001)
  // clientId is the sort key to get all the timeline items of a client
  @DynamoDBHashKey
  private String timelineId; // clientId-mediaId: 0b5dedb5-4b04-4c36-adda-e748e60bc20a-tt0000001
  @DynamoDBAttribute
  private String clientId; // ff5428e8-ae95-4b7c-ab16-9a602df182e4 (UUID)
  @DynamoDBAttribute
  private String mediaId; // tt0000001 (IMDB ID)
  private Date creationTime; // 1600000000 (epoch time)
  @DynamoDBAutoGeneratedTimestamp(strategy = DynamoDBAutoGenerateStrategy.CREATE)
  public Date getCreationTime(){ return creationTime; }
  public void setCreationTime(Date creationTime){ this.creationTime = creationTime; }
  private Date lastUpdate; // 1600000000 (epoch time)
  @DynamoDBAutoGeneratedTimestamp(strategy = DynamoDBAutoGenerateStrategy.ALWAYS)
  public Date getLastUpdate(){ return lastUpdate; }
  public void setLastUpdate(Date update){ this.lastUpdate = update; }



  @DynamoDBAttribute
  private String status; // DONE, PROGRESS, WISHLIST (enum)
  @DynamoDBAttribute
  private Integer rating; // 1-5 (optional, only for DONE)
  @DynamoDBAttribute
  private String comment; // (optional, only for DONE)
  public Timeline(String timelineId, String clientId, String mediaId, String status,
      int rating, String comment) {
    this.timelineId = timelineId;
    this.clientId = clientId;
    this.mediaId = mediaId;
    this.status = status;
    this.rating = rating;
    this.comment = comment;
  }
}