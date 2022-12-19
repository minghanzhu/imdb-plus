package org.opencsd.imdbplus.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 Sample client item JSON:
 {
 "clientname": "tester",
 "email": "tester@columbia.edu",
 "accountSetting": {
 "isPrivate": false,
 "isAdult": true
 }
 }
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "client")
public class Client {

  @DynamoDBHashKey(attributeName = "clientId")
  @DynamoDBAutoGeneratedKey
  private String clientId;

  @DynamoDBAutoGeneratedKey
  private String accessToken;
  @DynamoDBAttribute
  private String clientname;
  @DynamoDBAttribute
  private String email;
  @DynamoDBAttribute
  private AccountSetting accountSetting;

  public Client(String clientname, String email, AccountSetting accountSetting) {
    this.clientname = clientname;
    this.email = email;
    this.accountSetting = accountSetting;
  }

  public String getClientname() {
    return clientname;
  }
}