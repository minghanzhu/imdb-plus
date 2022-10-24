package com.example.imdbplus.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
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

import java.util.*;

@Repository
public class TimelineRepository {
    private static final Logger timelineLogger =  LoggerFactory.getLogger(TimelineRepository.class);

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public ResponseEntity save(Timeline timeline, String accessToken) {
        // Update creation time
        try {
            String creationTime = String.valueOf(System.currentTimeMillis());
            timeline.setCreationTime(creationTime);
            // Check if the user exists and the access token is valid
            String userId = timeline.getUserId();
            User user = dynamoDBMapper.load(User.class, userId);
            if (user.getAccessToken().equals(accessToken)) {
                timelineLogger.info("User " + user.getUsername() + " added to their timeline");
                dynamoDBMapper.save(timeline);
                return ResponseEntity.ok(timeline);
            } else {
                return ResponseEntity.status(401).body("Invalid access token");
            }
        }catch (AmazonDynamoDBException e){
            timelineLogger.debug("Error");
            return ResponseEntity.internalServerError().build();
        }
    }

    public Timeline getTimeline(String userId, String mediaId) {
        return dynamoDBMapper.load(Timeline.class, userId + mediaId);
    }


    public PaginatedScanList<Timeline> getAllTimeline(){
        return dynamoDBMapper.scan(Timeline.class, new DynamoDBScanExpression());
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

        List<Timeline> timelineList = dynamoDBMapper.scan(Timeline.class, new DynamoDBScanExpression());
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

        try{
            return dynamoDBMapper.load(Media.class, mostWatchedMediaId);
        }catch (ResourceNotFoundException e){
            return null;
        }

    }

    public Media highestRating(){
        Map<String, List<Long>> ratingMap = new HashMap<>();
        List<Timeline>  allTimelines = dynamoDBMapper.scan(Timeline.class, new DynamoDBScanExpression());

        // Walk down the timeline {id : [count, total]}
        for(Timeline curLine: allTimelines){
            String id = curLine.getMediaId();
            if(curLine.getRating() != null && curLine.getStatus().equals("DONE") && curLine.getMediaId() != null){
                if(ratingMap.containsKey(id)){
                    List<Long> avgRating = ratingMap.get(id);
                    avgRating.set(0, avgRating.get(0)+1);
                    long total = avgRating.get(1);
                    avgRating.set(1, total += curLine.getRating());
                    ratingMap.put(id, avgRating);
                }else{
                    List<Long> avg = new ArrayList<>();
                    avg.add(1l);
                    avg.add((long) curLine.getRating());
                    ratingMap.put(id, avg);
                }

            }
        }

        Double highestAvgRating = Double.MIN_VALUE;
        String highestMediaId = null;
        //Find Avg
        for(String key: ratingMap.keySet()){
            double avg = (double) ratingMap.get(key).get(1)/ratingMap.get(key).get(0);
            if(highestAvgRating < avg){
                highestAvgRating = avg;
                highestMediaId = key;
            }
        }
        timelineLogger.info("Highest Rating Media: "+highestMediaId + " Rating: "+highestAvgRating);

        return dynamoDBMapper.load(Media.class, highestMediaId);
    }

}
