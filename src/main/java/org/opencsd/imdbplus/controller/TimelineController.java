package org.opencsd.imdbplus.controller;

import org.opencsd.imdbplus.entity.Timeline;
import org.opencsd.imdbplus.repository.TimelineRepository;
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
  private TimelineRepository timelineRepository;

  @PostMapping("/timeline")
  public ResponseEntity<Timeline> saveTimeline(@RequestBody() Timeline timeline,
      @RequestHeader("Authorization") String accessToken) {
    Timeline response = timelineRepository.save(timeline, accessToken);
    if (response == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    } else {
      return ResponseEntity.ok(response);
    }
  }

  @DeleteMapping("/timeline/{userId}/{mediaId}")
  public ResponseEntity<String> deleteTimeline(@PathVariable("userId") String userId,
      @PathVariable("mediaId") String mediaId,
      @RequestHeader("Authorization") String accessToken) {
    String response = timelineRepository.delete(userId, mediaId, accessToken);
    if (response.equals("Invalid access token")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    } else if (response.equals("Timeline not found")) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    } else {
      return ResponseEntity.ok(response);
    }
  }

  // Get all timelines by userId
  @GetMapping("/timeline/user/{userId}")
  public ResponseEntity<List<Timeline>> getTimeline(@PathVariable("userId") String userId) {
    List<Timeline> response = timelineRepository.getTimelineByUserId(userId);
    if (response.size() > 0) {
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  // Get all timelines by mediaId
  @GetMapping("/timeline/media/{mediaId}")
  public ResponseEntity<List<Timeline>> getTimelineByMediaId(
      @PathVariable("mediaId") String mediaId) {
    List<Timeline> response = timelineRepository.getTimelineByMediaId(mediaId);
    if (response == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    } else {
      return ResponseEntity.ok(response);
    }
  }

  // Get a timeline by userId and mediaId
  @GetMapping("/timeline/{userId}/{mediaId}")
  public ResponseEntity<Timeline> getTimelineByTimelineId(@PathVariable("userId") String userId,
      @PathVariable("mediaId") String mediaId) {
    Timeline response = timelineRepository.getTimelineByTimelineId(userId, mediaId);
    if (response == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    } else {
      return ResponseEntity.ok(response);
    }
  }
}