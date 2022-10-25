package com.example.imdbplus.repository;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.example.imdbplus.entity.Media;
import com.example.imdbplus.entity.Timeline;
import com.example.imdbplus.entity.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

@Repository
public class TimelineRepository {

  private static final Logger timelineLogger = LoggerFactory.getLogger(TimelineRepository.class);

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
        TimelineRepository.timelineLogger.info(
            "User " + user.getUsername() + " added to their timeline");
        dynamoDBMapper.save(timeline);
        return ResponseEntity.ok(timeline);
      } else
        return ResponseEntity.status(401).body("Invalid access token");
    } catch (NullPointerException e) {
      TimelineRepository.timelineLogger.debug("Error");
      return ResponseEntity.badRequest().body("User doesn't exist. Check userId and try again");
    }
  }

  public Timeline getTimeline(String userId, String mediaId) {
    return dynamoDBMapper.load(Timeline.class, userId + mediaId);
  }


  public ResponseEntity getAllTimeline() {
    PaginatedScanList<Timeline> timelines = dynamoDBMapper.scan(Timeline.class,
        new DynamoDBScanExpression());
    try {
      return ResponseEntity.ok(timelines);
    } catch (DynamoDBMappingException e) {
      return ResponseEntity.status(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED)
          .body("Sorry, our servers are down at the moment.");
    }
  }

  public ResponseEntity delete(String userId, String mediaId, String accessToken) {
    // Check if the user exists and the access token is valid
    User user = dynamoDBMapper.load(User.class, userId);
    if (!user.getAccessToken().equals(accessToken))
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid access token");
    // Check if the timeline exists
    Timeline timeline = dynamoDBMapper.load(Timeline.class, userId + "-" + mediaId);
    if (timeline == null)
      return ResponseEntity.status(404).body("Timeline not found");
    dynamoDBMapper.delete(timeline);
    return ResponseEntity.ok().body("Timeline deleted successfully");
  }

  public void update(Timeline timeline) {
    dynamoDBMapper.save(timeline);
  }

  public ResponseEntity getHighestRating() {
    Map<String, List<Long>> ratingMap = new HashMap<>();
    List<Timeline> allTimelines = dynamoDBMapper.scan(Timeline.class, new DynamoDBScanExpression());

    // Walk down the timeline {id : [count, total]}
    for (Timeline curLine : allTimelines) {
      String id = curLine.getMediaId();
      if (curLine.getRating() != null && curLine.getStatus().equals("DONE")
          && curLine.getMediaId() != null)
        if (ratingMap.containsKey(id)) {
          List<Long> avgRating = ratingMap.get(id);
          avgRating.set(0, avgRating.get(0) + 1);
          long total = avgRating.get(1);
          avgRating.set(1, total += curLine.getRating());
          ratingMap.put(id, avgRating);
        } else {
          List<Long> avg = new ArrayList<>();
          avg.add(1L);
          avg.add((long) curLine.getRating());
          ratingMap.put(id, avg);
        }
    }

    Double highestAvgRating = Double.MIN_VALUE;
    String highestMediaId = null;
    //Find Avg
    for (String key : ratingMap.keySet()) {
      double avg = (double) ratingMap.get(key).get(1) / ratingMap.get(key).get(0);
      if (highestAvgRating < avg) {
        highestAvgRating = avg;
        highestMediaId = key;
      }
    }

    try {
      TimelineRepository.timelineLogger.info(
          "Highest Rating Media: " + highestMediaId + " Rating: " + highestAvgRating);

      return ResponseEntity.ok(dynamoDBMapper.load(Media.class, highestMediaId));
    } catch (DynamoDBMappingException e) {
      TimelineRepository.timelineLogger.debug(e.toString());
      return ResponseEntity.status(500).body("Sorry our we're computing incorrectly.");
    }
  }

  public ResponseEntity getMost(String status) {
    try {
      return ResponseEntity.ok(calculateMost(status));
    } catch (NullPointerException e) {
      return ResponseEntity.badRequest().body("Status doesn't exist.");
    }
  }

  private Map<String, Long> countMedia(String category) {
    List<Timeline> timelineList = dynamoDBMapper.scan(Timeline.class, new DynamoDBScanExpression());
    Map<String, Long> mediaCounter = new HashMap<>();

    // Compile a list of watched Media
    for (Timeline line : timelineList) {
      String mediaId = line.getMediaId();
      if (line.getStatus().equals(category) && line.getMediaId() != null) {
        long count = mediaCounter.containsKey(mediaId) ? mediaCounter.get(mediaId) : 0;
        mediaCounter.put(mediaId, count + 1);
      }
    }
    return mediaCounter;
  }

  // mediaId, status -> [DONE, IN_PROGRESS, WISHLIST]
  public Media calculateMost(String category) {
    Map<String, Long> mediaCounter = countMedia(category);
    long curMostWatched = Long.MIN_VALUE;
    String mostWatchedMediaId = null;
    for (String mediaId : mediaCounter.keySet())
      if (mediaCounter.get(mediaId) > curMostWatched) {
        curMostWatched = mediaCounter.get(mediaId);
        mostWatchedMediaId = mediaId;
      }

    try {
      return dynamoDBMapper.load(Media.class, mostWatchedMediaId);
    } catch (DynamoDBMappingException e) {
      TimelineRepository.timelineLogger.debug(e.toString());
      return new Media();
    }
  }

  public ResponseEntity getTopTenMost(String category) {
    PriorityQueue<MediaWrapper> topTenQueue = new PriorityQueue<>(10);
    Map<String, Long> validMedia = countMedia(category);

    //Build check the top ten contenders
    for (String id : validMedia.keySet())
      if (topTenQueue.size() < 10) {
        MediaWrapper media = new MediaWrapper(id, validMedia.get(id).intValue());
        topTenQueue.offer(media);
      } else if (topTenQueue.peek().count < validMedia.get(id)) {
        topTenQueue.poll();
        MediaWrapper media = new MediaWrapper(id, validMedia.get(id).intValue());
        topTenQueue.offer(media);
      }
    // Pop off the queue and return a list
    List<Media> topTenList = new ArrayList<>(10);
    while (!topTenQueue.isEmpty()) {
      Media top = dynamoDBMapper.load(Media.class, topTenQueue.poll().mediaId);
      topTenList.add(top);
    }
    return ResponseEntity.ok(topTenList);
  }

  private class MediaWrapper implements Comparable<MediaWrapper> {

    String mediaId;
    int count;

    MediaWrapper(String mediaId, int count) {
      this.mediaId = mediaId;
      this.count = count;
    }

    @Override
    public int compareTo(MediaWrapper other) {
      return count - other.count;
    }
  }


}
