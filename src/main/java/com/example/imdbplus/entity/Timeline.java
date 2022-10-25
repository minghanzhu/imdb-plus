package com.example.imdbplus.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

/**
 Sample timeline item JSON (don't include creationTime):
 {
 "timelineId": "0b5dedb5-4b04-4c36-adda-e748e60bc20a-tt0000001",
 "userId": "ff5428e8-ae95-4b7c-ab16-9a602df182e4",
 "mediaId": "tt0000001",
 "status": "DONE",
 "rating": 5,
 "comment": "This is a comment"
 }
 */
@DynamoDBTable(tableName = "timeline")
public class Timeline {

  // timelineId is the primary key, it is a combination of userId and mediaId (e.g. ff5428e8-ae95-4b7c-ab16-9a602df182e4-tt0000001)
  // userId is the sort key to get all the timeline items of a user
  @DynamoDBHashKey
  private String timelineId; // userId-mediaId: 0b5dedb5-4b04-4c36-adda-e748e60bc20a-tt0000001
  @DynamoDBAttribute
  private String userId; // ff5428e8-ae95-4b7c-ab16-9a602df182e4 (UUID)
  @DynamoDBAttribute
  private String mediaId; // tt0000001 (IMDB ID)
  @DynamoDBAttribute
  private String creationTime; // 1600000000 (epoch time)
  @DynamoDBAttribute
  private String status; // DONE, IN_PROGRESS, WISHLIST (enum)
  @DynamoDBAttribute
  private Integer rating; // 1-5 (optional, only for DONE)
  @DynamoDBAttribute
  private String comment; // (optional, only for DONE)
}