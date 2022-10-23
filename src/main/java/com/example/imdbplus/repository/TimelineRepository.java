package com.example.imdbplus.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.xspec.S;
import com.example.imdbplus.entity.Timeline;
import com.example.imdbplus.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

@Repository
public class TimelineRepository {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public ResponseEntity save(Timeline timeline, String accessToken) {
        // Update creation time
        String creationTime = String.valueOf(System.currentTimeMillis());
        timeline.setCreationTime(creationTime);
        // Check if the user exists and the access token is valid
        String userId = timeline.getUserId();
        User user = dynamoDBMapper.load(User.class, userId);
        if (user.getAccessToken().equals(accessToken)) {
            dynamoDBMapper.save(timeline);
            return ResponseEntity.ok(timeline);
        } else {
            return ResponseEntity.status(401).body("Invalid access token");
        }
    }

    public Timeline getTimeline(String userId, String mediaId) {
        return dynamoDBMapper.load(Timeline.class, userId + mediaId);
    }

    public ResponseEntity delete(String userId, String mediaId, String accessToken) {
        // Check if the user exists and the access token is valid
        User user = dynamoDBMapper.load(User.class, userId);
        if (!user.getAccessToken().equals(accessToken)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid access token");
        }
        // Check if the timeline exists
        Timeline timeline = dynamoDBMapper.load(Timeline.class, userId + "-" + mediaId);
        if (timeline == null) {
            return ResponseEntity.status(404).body("Timeline not found");
        }
        dynamoDBMapper.delete(timeline);
        return ResponseEntity.ok().body("Timeline deleted successfully");
    }

    public void update(Timeline timeline) {
        dynamoDBMapper.save(timeline);
    }

}
