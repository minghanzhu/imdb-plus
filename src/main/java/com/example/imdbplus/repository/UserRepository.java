package com.example.imdbplus.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.example.imdbplus.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public class UserRepository {
    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    // Add new user to the database if the username does not exist
    public ResponseEntity save(User user) {
        // scan the table to see if the username exists
        HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":v1", new AttributeValue().withS(user.getUsername()));
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("username = :v1")
                .withExpressionAttributeValues(eav);
        // if the username does not exist, add the user to the database
        List<User> replies = dynamoDBMapper.scan(User.class, scanExpression);
        if (replies.size() == 0) {
            dynamoDBMapper.save(user);
            return ResponseEntity.ok(user);
        } else {
            // otherwise, return a 400 error
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sorry, username already exists");
        }
    }

    public User getUser(String userId) {
        return dynamoDBMapper.load(User.class, userId);
    }

    public String delete(String userId, String accessToken) {
        User user = dynamoDBMapper.load(User.class, userId);
        if (user.getAccessToken().equals(accessToken)) {
            dynamoDBMapper.delete(user);
            return "User deleted successfully";
        } else {
            return "Invalid access token";
        }
    }

    public String update(String userId, User user) {
        dynamoDBMapper.save(user, new DynamoDBSaveExpression()
                .withExpectedEntry("userId",
                        new ExpectedAttributeValue(
                                new AttributeValue().withS(userId)
                        )));
        return userId;
    }
}
