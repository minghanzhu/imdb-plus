package org.opencsd.imdbplus.controller;

import org.opencsd.imdbplus.entity.Timeline;
import org.opencsd.imdbplus.repository.ClientRepository;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class TimelineController {
  Logger timelineControllerLogger = LoggerFactory.getLogger(TimelineController.class);

  @Autowired
  private TimelineService timelineService;
  @Autowired
  private ClientRepository clientRepository;

  @PostMapping("/timeline")
  public ResponseEntity<Timeline> saveTimeline(@RequestBody() Timeline timeline,
      @RequestHeader("Authorization") String accessToken) {

    Timeline response = timelineService.save(timeline, accessToken);

    if (response == null) {
      timelineControllerLogger.warn("{} wrong", response);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    } else {
      return ResponseEntity.ok(response);
    }
  }

  @DeleteMapping("/timeline/{timelineId}")
  public ResponseEntity<String> deleteTimeline(@PathVariable("timelineId") String timelineId,
      @RequestHeader("Authorization") String accessToken) {
    String response =  timelineService.delete(timelineId, accessToken);
    if (response.equals("Invalid access token")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    } else if (response.equals("Timeline not found")) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    } else {
      return ResponseEntity.ok(response);
    }
  }

  // Get all timelines by clientId
  @GetMapping("/timeline/client/{clientId}")
  public ResponseEntity<List<Timeline>> getTimeline(@PathVariable("clientId") String clientId) {
    List<Timeline> response = timelineService.getTimelineByClientId(clientId);
    if (response != null) {
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  // Get all timelines by mediaId
  @GetMapping("/timeline/media/{mediaId}")
  public ResponseEntity<List<Timeline>> getTimelineByMediaId(@PathVariable("mediaId") String mediaId) {
    List<Timeline> response =  timelineService.getTimelineByMediaId(mediaId);
    if (response == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    } else {
      return ResponseEntity.ok(response);
    }
  }

  // Get a timeline by clientId and mediaId
  @GetMapping("/timeline/{clientId}/{mediaId}")
  public ResponseEntity<Timeline> getTimelineByClientIdAndMediaId(@PathVariable("clientId") String clientId,
      @PathVariable("mediaId") String mediaId) {
    Timeline timeline = timelineService.getTimelineByClientIdAndMediaId(clientId, mediaId);
    if (timeline != null) {
      return ResponseEntity.ok(timeline);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

    }
  }
}