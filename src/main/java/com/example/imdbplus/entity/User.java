package com.example.imdbplus.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "user")
public class User {

    @DynamoDBHashKey
    @DynamoDBAutoGeneratedKey
    private String userId;

    public String getUsername() {
        return username;
    }

    @DynamoDBAttribute
    private String username;

    @DynamoDBAttribute
    private String email;

//    @DynamoDBAttribute
//    private String access_token;

    @DynamoDBAttribute
    private AccountSetting accountSetting;
}
