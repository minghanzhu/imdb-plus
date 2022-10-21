package com.example.imdbplus.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.example.imdbplus.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public class UserRepository {
    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public String save(User user) {
        // Add new user to the database if the username does not exist
        HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        // scan the table to see if the username exists
        eav.put(":v1", new AttributeValue().withS(user.getUsername()));
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("username = :v1")
                .withExpressionAttributeValues(eav);
        // if the username does not exist, add the user to the database
        List<User> replies = dynamoDBMapper.scan(User.class, scanExpression);
        if (replies.size() == 0) {
            dynamoDBMapper.save(user);
            return "User saved successfully";
        } else {
            return "Username already exists";
        }
    }

    public User getUser(String userId) {
        return dynamoDBMapper.load(User.class, userId);
    }

    public String delete(String userId) {
        User user = dynamoDBMapper.load(User.class, userId);
        dynamoDBMapper.delete(user);
        return "User deleted successfully";
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
