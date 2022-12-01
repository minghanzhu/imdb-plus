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

@DynamoDBTable(tableName = "media")
public class Media {

  @DynamoDBHashKey
  @DynamoDBAttribute
  private String mediaId; // tt0000001 (IMDB ID)
  @DynamoDBAttribute
  private String title; // The Shawshank Redemption
  @DynamoDBAttribute
  private String release_date; // 1994-09-23
  @DynamoDBAttribute
  private String genre; // Drama
}