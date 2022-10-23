package com.example.imdbplus.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.example.imdbplus.entity.User;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;

public class BaseRepository<T> implements BaseRepositoryInterface<T> {

    private DynamoDBMapper entityMapper;

    @Override
    public ResponseEntity save(T entity) {
        return null;
    }

    public boolean entityExists(T entity, String getAttribute){
        HashMap<String, AttributeValue> eav = new HashMap<>();
        eav.put(":v1", new AttributeValue().withS(getAttribute));
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression(getAttribute + " = :v1")
                .withExpressionAttributeValues(eav);
        // if the username does not exist, add the user to the database
        List<User> replies = entityMapper.scan(User.class, scanExpression);
        return replies.size() == 0;
    }

    @Override
    public T getEntity(String entityId) {
        return null;
    }

    @Override
    public List<T> getAllEntity() {
        return null;
    }

    @Override
    public String delete(String entityId, String accessToken) {
        return null;
    }

    @Override
    public String update(String entityId, T entity) {
        return null;
    }
}
