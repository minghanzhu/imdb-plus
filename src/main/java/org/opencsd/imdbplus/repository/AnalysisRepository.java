package org.opencsd.imdbplus.repository;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import org.opencsd.imdbplus.entity.Media;
import org.opencsd.imdbplus.entity.Timeline;
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
public class AnalysisRepository {

  private static final Logger analysisLogger = LoggerFactory.getLogger(AnalysisRepository.class);

  @Autowired
  private DynamoDBMapper dynamoDBMapper;


  public String getHighestRatingHelper(List<Timeline> allTimelines) {
    Map<String, List<Long>> ratingMap = new HashMap<>();

    // Walk down the timeline {id : [count, total]}
    for (Timeline curLine : allTimelines) {
      String id = curLine.getMediaId();
      if (curLine.getRating() != null && curLine.getStatus().equals("DONE")
              && curLine.getMediaId() != null)
        if (ratingMap.containsKey(id)) {
          List<Long> avgRating = ratingMap.get(id);
          avgRating.set(0, avgRating.get(0) + 1);
          long total = avgRating.get(1);
          total += curLine.getRating();
          avgRating.set(1, total);
          ratingMap.put(id, avgRating);
        } else {
          List<Long> avg = new ArrayList<>();
          avg.add(1L);
          avg.add((long) curLine.getRating());
          ratingMap.put(id, avg);
        }
    }
    AnalysisRepository.analysisLogger.info("Rating map: " + ratingMap);
    Double highestAvgRating = Double.MIN_VALUE;
    String highestMediaId = null;
    //Find Avg
    for (String key : ratingMap.keySet()) {
      AnalysisRepository.analysisLogger.info("key:" + key + "value: " + ratingMap.get(key));
      double avg = (double) ratingMap.get(key).get(1) / ratingMap.get(key).get(0);
      if (highestAvgRating < avg) {
        highestAvgRating = avg;
        highestMediaId = key;
      }
    }
    AnalysisRepository.analysisLogger.info("Highest Rated Media: " + highestMediaId);
    return highestMediaId;
  }


  public ResponseEntity getHighestRating() {
    Map<String, List<Long>> ratingMap = new HashMap<>();
    List<Timeline> allTimelines = dynamoDBMapper.scan(Timeline.class,
        new DynamoDBScanExpression());

    String highestMediaId = getHighestRatingHelper(allTimelines);
    AnalysisRepository.analysisLogger.info("Highest Rated Media: " + highestMediaId);

    try {
//      AnalysisRepository.analysisLogger.info(
//          "Highest Rated Media: " + highestMediaId + " Rating: " + highestAvgRating);
      return ResponseEntity.ok(dynamoDBMapper.load(Media.class, highestMediaId));
    } catch (DynamoDBMappingException e) {
      AnalysisRepository.analysisLogger.debug(e.toString());
      return ResponseEntity.badRequest().body("Sorry, something went wrong.");
    }
  }

  public ResponseEntity getMostMediaWith(String status) {
    try {
      return ResponseEntity.ok(calculateMost(status));
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  private Map<String, Long> countMedia(String category) {
    List<Timeline> timelineList = dynamoDBMapper.scan(Timeline.class,
        new DynamoDBScanExpression());
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
  Media calculateMost(String category) {
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
      AnalysisRepository.analysisLogger.debug(e.toString());
      return null;
    }
  }

  public ResponseEntity getTopTen(String status) {
    try {
      return ResponseEntity.ok(getTopTenList(status));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Sorry, something went wrong.");
    }
  }

  List<Media> getTopTenList(String category) {
    Map<String, Long> validMedia = countMedia(category);

    //Build check the top ten contenders
    List<String> topTenListId = getTopTenListHelper(validMedia);

    // Pop off the queue and return a list
    List<Media> topTenList = new ArrayList<>(10);
    for (String mediaId: topTenListId) {
      Media top = dynamoDBMapper.load(Media.class, mediaId);
      topTenList.add(top);
    }
    return topTenList;

  }

  List<String> getTopTenListHelper(Map<String, Long> validMedia){
    PriorityQueue<MediaWrapper> topTenQueue = new PriorityQueue<>(10);
    for (String id : validMedia.keySet())
      if (topTenQueue.size() < 10) {
        MediaWrapper media = new MediaWrapper(id, validMedia.get(id).intValue());
        topTenQueue.offer(media);
      } else if (topTenQueue.peek().count < validMedia.get(id)) {
        topTenQueue.poll();
        MediaWrapper media = new MediaWrapper(id, validMedia.get(id).intValue());
        topTenQueue.offer(media);
      }
    List<String> topTenList = new ArrayList<>(10);
    while (!topTenQueue.isEmpty()) {
      String mediaId = topTenQueue.poll().mediaId;
      topTenList.add(mediaId);
    }

    return topTenList;
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
            return ResponseEntity.badRequest().body("Sorry, something went wrong.");
          }
        }
      }
    }

    return ResponseEntity.ok(userPreference);
  }

  private class MediaWrapper implements Comparable<MediaWrapper> {

    String mediaId;
    int count;
    Media media;

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
