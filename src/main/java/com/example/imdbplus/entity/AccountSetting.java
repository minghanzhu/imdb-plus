package com.example.imdbplus.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@DynamoDBDocument
public class AccountSetting {

  @DynamoDBAttribute
  private Boolean isPrivate;
  @DynamoDBAttribute
  private Boolean isAdult;

  public AccountSetting(Boolean isPrivate, Boolean isAdult) {
    this.isPrivate = isPrivate;
    this.isAdult = isAdult;
  }
}
