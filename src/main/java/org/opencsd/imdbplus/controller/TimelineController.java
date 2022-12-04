package org.opencsd.imdbplus.controller;

import org.opencsd.imdbplus.entity.Timeline;
import org.opencsd.imdbplus.service.TimelineService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TimelineController {

  @Autowired
  private TimelineService timelineService;

  @PostMapping("/timeline")
  public ResponseEntity saveTimeline(@RequestBody() Timeline timeline,
      @RequestHeader("Authorization") String accessToken) {
    Timeline response = timelineService.save(timeline, accessToken);
    if (response == null) {
      return ResponseEntity.status(401).body("Unauthorized");
    } else {
      return ResponseEntity.ok(response);
    }
  }

  @DeleteMapping("/timeline/{timelineId}")
  public ResponseEntity deleteTimeline(@PathVariable("userId") String timelineId,
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
  @GetMapping("/timeline/user/{userId}")
  public ResponseEntity getTimeline(@PathVariable("userId") String userId) {
    List<Timeline> response = timelineService.getTimelineByUserId(userId);
    if (response != null) {
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Timeline not found");
    }
  }

  // Get all timelines by mediaId
  @GetMapping("/timeline/media/{mediaId}")
  public ResponseEntity getTimelineByMediaId(@PathVariable("mediaId") String mediaId) {
    List<Timeline> response =  timelineService.getTimelineByMediaId(mediaId);
    if (response == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Timeline not found");
    } else {
      return ResponseEntity.ok(response);
    }
  }

  // Get a timeline by userId and mediaId
  @GetMapping("/timeline/{userId}/{mediaId}")
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