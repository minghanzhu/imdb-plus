package com.example.imdbplus.conroller;

import com.example.imdbplus.entity.Timeline;
import com.example.imdbplus.repository.TimelineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
  public ResponseEntity saveTimeline(@RequestBody() Timeline timeline,
      @RequestHeader("Authorization") String accessToken) {
    return timelineRepository.save(timeline, accessToken);
  }

  @DeleteMapping("/timeline/{userId}/{mediaId}")
  public ResponseEntity deleteTimeline(@PathVariable("userId") String userId,
      @PathVariable("mediaId") String mediaId,
      @RequestHeader("Authorization") String accessToken) {
    return timelineRepository.delete(userId, mediaId, accessToken);
  }
  
}
