package org.opencsd.imdbplus.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import java.util.List;
import org.opencsd.imdbplus.entity.Media;
import org.opencsd.imdbplus.entity.Timeline;
import org.opencsd.imdbplus.entity.User;
import org.opencsd.imdbplus.repository.MediaRepository;
import org.opencsd.imdbplus.repository.TimelineRepository;
import org.opencsd.imdbplus.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TimelineService {
  Logger serviceLogger = LoggerFactory.getLogger(Timeline.class);

  @Autowired
  TimelineRepository timelineRepository;
  @Autowired
  UserRepository userRepository;
  @Autowired
  MediaRepository mediaRepository;

  /**
   *  Checks if user has auth token to post a timeline, then create a timeline object with a timestamp.
   */
  public Timeline save(Timeline timeline, String accessToken) {
    // Check if the user exists and the access token is valid
    String userId = timeline.getUserId();
    String mediaId = timeline.getMediaId();
    User user = userRepository.getUser(userId);
    Media media = mediaRepository.getEntity(mediaId);

    if (user.getAccessToken().equals(accessToken) && media != null) {
      String id = userId+mediaId;
      timelineRepository.save(timeline);
      serviceLogger.info(userId + " Created " + timeline);
      return timeline;
    } else {
      return null;
    }
  }

  /**
   *  Given a timeline id, returns a timeline instance, otherwise null
   */
  public Timeline getTimeline(String timelineId) {
    try {
      Timeline timeline = timelineRepository.getTimeline(timelineId);
      serviceLogger.info(timelineId + " retrieved " + timeline);
      return timeline;
    }catch (DynamoDBMappingException e){
      serviceLogger.warn(e.toString());
      return null;
    }
  }

  /**
   *  Given a valid timeline id and correct access token, deletes a timeline from the database
   */
  public String delete(String timelineId, String accessToken) {
    // Check if the user exists and the access token is valid
    Timeline timeline = getTimeline(timelineId);
    if (timeline != null ){
      User user = userRepository.getUser(timeline.getUserId());
      if (!user.getAccessToken().equals(accessToken)) {
        return "Invalid access token";
      }
      serviceLogger.info(timelineId + " deleted by " + user.getUsername());
      timelineRepository.delete(timelineId);
      return "Timeline deleted successfully";
    }
    return "Timeline does not exist";

  }
  /**
   *  Given a valid timeline object that also exists in the repository, updates the comment, status, or rating
   */
  public Timeline update(Timeline timeline) {
    Timeline oldTimeline = timelineRepository.getTimeline(timeline.getTimelineId());

    if(oldTimeline != null){
      if(oldTimeline.getUserId().equals(timeline.getUserId()) && oldTimeline.getMediaId().equals(timeline.getMediaId()))
        if(timeline.getRating() >= 0 && timeline.getRating() <= 5) {
          timelineRepository.save(timeline);
          serviceLogger.info(timeline.getTimelineId() + " updated by  " + timeline.getUserId());
        }
    }
    return timeline;


  }

  /**
   *  Given a valid user id that also exists in the repository, returns a list of user timelines.
   */
  public List<Timeline> getTimelineByUserId(String userId) {
    // scan the timeline table to get all timelines of the user
    User curUser = userRepository.getUser(userId);
    if(curUser != null){
      serviceLogger.info(userId + " timelines retrieved");
      return  timelineRepository.getTimelineByUserId(curUser.getUserId());
    }else{
      return null;
    }
  }

  /**
   *  Given a valid media id that also exists in the repository, returns a list of timelines.
   */
  public List<Timeline> getTimelineByMediaId(String mediaId) {
    // scan the timeline table to get all timelines of the media
    Media curMedia = mediaRepository.getEntity(mediaId);
    if(curMedia != null){
      serviceLogger.info(mediaId + " timelines retrieved");
      return  timelineRepository.getTimelineByMediaId(mediaId);
    }else{
      return null;
    }
  }

  /**
   *  Given a valid media id and user id that also exists in the repository, returns a list of timelines.
   */
  public Timeline getTimelineByUserIdAndMediaId(String userId, String mediaId) {
    serviceLogger.info(mediaId + " timelines retrieved by " + userId);
    Timeline timeline = timelineRepository.getTimelineByUserIdAndMediaId(userId, mediaId);
    return  timeline;
  }

//  public ResponseEntity getAllTimeline(String accessToken) {
//    PaginatedScanList<Timeline> timelines = dynamoDBMapper.scan(Timeline.class,
//        new DynamoDBScanExpression());
//
//    try {
//      return ResponseEntity.ok(timelines);
//    } catch (DynamoDBMappingException e) {
//      return ResponseEntity.status(HttpStatus.NOT_FOUND)
//          .body("Sorry, our servers are down at the moment.");
//    }
//  }

}