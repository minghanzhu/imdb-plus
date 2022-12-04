package org.opencsd.imdbplus.controller;

import org.opencsd.imdbplus.entity.Timeline;
import org.opencsd.imdbplus.entity.User;
import org.opencsd.imdbplus.repository.UserRepository;
import org.opencsd.imdbplus.service.TimelineService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/timeline")
public class TimelineController {
  Logger timelineControllerLogger = LoggerFactory.getLogger(TimelineController.class);

  @Autowired
  private TimelineService timelineService;
  @Autowired
  private UserRepository userRepository;

  @PostMapping()
  public ResponseEntity saveTimeline(@RequestBody() Timeline timeline,
      @RequestHeader("Authorization") String accessToken) {

    Timeline response = timelineService.save(timeline, accessToken);

    if (response == null) {
      timelineControllerLogger.warn("{} wrong", response);
      return ResponseEntity.status(401).body("Unauthorized");
    } else {
      return ResponseEntity.ok(response);
    }
  }

  @DeleteMapping("/{timelineId}")
  public ResponseEntity deleteTimeline(@PathVariable("timelineId") String timelineId,
      @RequestHeader("Authorization") String accessToken) {
    String response =  timelineService.delete(timelineId, accessToken);
    if (response.equals("Invalid access token")) {
      return ResponseEntity.status(401).body("Invalid access token");
    } else if (response.equals("Timeline not found")) {
      return ResponseEntity.status(404).body("Timeline not found");
    } else {
      return ResponseEntity.ok(response);
    }
  }

  // Get all timelines by userId
  @GetMapping("/{userId}")
  public ResponseEntity getTimeline(@PathVariable("userId") String userId) {
    List<Timeline> response = timelineService.getTimelineByUserId(userId);
    if (response != null) {
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Timeline not found");
    }
  }

  // Get all timelines by mediaId
  @GetMapping("/media/{mediaId}")
  public ResponseEntity getTimelineByMediaId(@PathVariable("mediaId") String mediaId) {
    List<Timeline> response =  timelineService.getTimelineByMediaId(mediaId);
    if (response == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Timeline not found");
    } else {
      return ResponseEntity.ok(response);
    }
  }

  // Get a timeline by userId and mediaId
  @GetMapping("/{userId}/{mediaId}")
  public ResponseEntity getTimelineByUserIdAndMediaId(@PathVariable("userId") String userId,
      @PathVariable("mediaId") String mediaId) {
    Timeline timeline = timelineService.getTimelineByUserIdAndMediaId(userId, mediaId);
    if (timeline != null) {
      return ResponseEntity.ok(timeline);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Timeline not found");

    }
  }
}