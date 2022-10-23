package com.example.imdbplus.repository;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.example.imdbplus.entity.Media;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class MediaRepository implements BaseRepository<Media> {
    private static final Logger mediaLogger = LoggerFactory.getLogger(MediaRepository.class);

    @Autowired
    private DynamoDBMapper dbObjMapper;


    public ResponseEntity save(Media content){
        return null;
    }

    @Override
    public Media getEntity(String entityId) {
        return null;
    }

    @Override
    public List<Media> getAllEntity() {
        return null;
    }

    @Override
    public String delete(String entityId, String accessToken) {
        return null;
    }

    @Override
    public String update(String entityId, Media entity) {
        return null;
    }



}