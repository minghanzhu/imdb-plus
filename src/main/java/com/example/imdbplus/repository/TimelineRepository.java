package com.example.imdbplus.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.xspec.S;
import com.example.imdbplus.entity.Media;
import com.example.imdbplus.entity.Timeline;
import com.example.imdbplus.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

@Repository
public class TimelineRepository {
    private static final Logger timelineLogger =  LoggerFactory.getLogger(TimelineRepository.class);

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
            timelineLogger.info("User "+user.getUsername() + " added to their timeline");
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

    // mediaId, status -> [DONE, IN_PROGRESS, WISHLIST]
    public Media mostWatched(){

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        List<Timeline> timelineList = dynamoDBMapper.scan(Timeline.class, scanExpression);
        Map<String, Long> mediaCounter = new HashMap<>();

        // Compile a list of watched Media
        for(Timeline line: timelineList){
            String mediaId = line.getMediaId();
            if(line.getStatus().equals("DONE")) {
                long count = mediaCounter.containsKey(mediaId) ? mediaCounter.get(mediaId) : 0;
                mediaCounter.put(mediaId, count + 1);
            }
        }
        long curMostWatched = Long.MIN_VALUE;
        String mostWatchedMediaId = null;
        for(String mediaId: mediaCounter.keySet()){
            if(mediaCounter.get(mediaId) > curMostWatched) {
                curMostWatched = mediaCounter.get(mediaId);
                mostWatchedMediaId = mediaId;
            }
        }

        return dynamoDBMapper.load(Media.class, mostWatchedMediaId);
    }

    public ResponseEntity getAllEntities() {
        List<Timeline> allTimeline = dynamoDBMapper.scan(Timeline.class, new DynamoDBScanExpression());
        return ResponseEntity.ok(allTimeline);
    }

    public ResponseEntity userPreference(String userId) {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        List<Timeline> timelineList = dynamoDBMapper.scan(Timeline.class, scanExpression);
        Map<String, Long> userPreference = new HashMap<>();
        for(Timeline line: timelineList){
            String lineUserId = line.getUserId();
            if(lineUserId.equals(userId)) {
                if(line.getStatus().equals("DONE") && line.getRating() < 3){
                    continue;
                }
                else {
                    String mediaId = line.getMediaId();
                    try{
                        Media media = dynamoDBMapper.load(Media.class, mediaId);
                        String genre = media.getGenre();
                        long count = userPreference.containsKey(genre) ? userPreference.get(genre) : 0;
                        userPreference.put(genre, count + 1);
                    }catch (DynamoDBMappingException e){
                        timelineLogger.debug(e.toString());
                        break;
                    }
                }
            }
        }

        return ResponseEntity.ok(userPreference);
    }

}
