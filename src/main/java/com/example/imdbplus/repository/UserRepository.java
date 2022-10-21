package com.example.imdbplus.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.example.imdbplus.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public class UserRepository {
    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public String save(User user) {
        // Add new user to the database if the username does not exist
        HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":v1", new AttributeValue().withS(user.getUsername()));
        DynamoDBQueryExpression<User> queryExpression = new DynamoDBQueryExpression<User>()
                .withKeyConditionExpression("username = :v1")
                .withExpressionAttributeValues(eav);
        if (dynamoDBMapper.query(User.class, queryExpression).isEmpty()) {
            dynamoDBMapper.save(user);
            return "User saved successfully";
        } else {
            return "Username already exists";
        }

//        if (dynamoDBMapper.load(User.class, user.getUsername()) == null) {
//            dynamoDBMapper.save(user);
//            return new String(user.getUsername() + " has been added to the database.");
//        } else {
//            return "Username already exists";
//        }
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
