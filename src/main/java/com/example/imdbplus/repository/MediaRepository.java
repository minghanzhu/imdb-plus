package com.example.imdbplus.repository;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.example.imdbplus.entity.Media;
import com.example.imdbplus.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public class MediaRepository {
    private static final Logger mediaLogger = LoggerFactory.getLogger(MediaRepository.class);

    @Autowired
    private DynamoDBMapper dbObjMapper;


    public ResponseEntity saveMedia(Media item){
        mediaLogger.debug("Trying to save entry");
        try {
            if(!itemExists(item, item.getMediaId())){
                dbObjMapper.save(item);
                mediaLogger.info("Added new media: "+ item.getTitle() + " Id: " + item.getMediaId());
                return ResponseEntity.ok(item);
            }else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sorry, media already exists");
            }
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Sorry, we're working to fix it.");
        }
    }

    public Media getEntity(String entityId) {
        return dbObjMapper.load(Media.class, entityId);
    }

    public List<Media> getAllEntity() {
        return null;
    }


    public String delete(String entityId, String accessToken) {
        return null;
    }

    public String update(String entityId, Media item) {
        return null;
    }

    public boolean itemExists(Media item,  String attributeValue){
        HashMap<String, AttributeValue> attrMap = new HashMap<>();
        attrMap.put(":val1", new AttributeValue().withS(item.getMediaId()));
//        DynamoDBQueryExpression<Media> queryExpression = new DynamoDBQueryExpression<Media>()
//                .withKeyConditionExpression("mediaId = :val1").withExpressionAttributeValues(attrMap);
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("mediaId = :val1")
                .withExpressionAttributeValues(attrMap);

        List<Media> replies = dbObjMapper.scan(Media.class, scanExpression);
        return replies.size() != 0;
    }



}