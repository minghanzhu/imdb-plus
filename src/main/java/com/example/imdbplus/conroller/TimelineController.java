package com.example.imdbplus.conroller;

import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.example.imdbplus.entity.Media;
import com.example.imdbplus.entity.Timeline;
import com.example.imdbplus.repository.TimelineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
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

  @GetMapping("/api/v1/timeline")
  public PaginatedScanList<Timeline> getTimeline() {
    return timelineRepository.getAllTimeline();
  }

  @GetMapping("/api/v1/timeline/analysis/highest-rating")
  public Media highestRating() {
    return timelineRepository.getHighestRating();
  }

  @GetMapping("api/v1/timeline/most")
  public ResponseEntity getMostWatched(@RequestParam("status") String status) {
    status = (status.toLowerCase().equals("progress")) ? "IN_PROGRESS" : status;
    return timelineRepository.getMost(status.toUpperCase());
  }

  @GetMapping("api/v1/timeline/top-ten/")
  public ResponseEntity getTopTenWatched(@RequestParam("status") String status) {
    status = (status.toLowerCase().equals("progress")) ? "IN_PROGRESS" : status;
    return timelineRepository.getTopTenMost(status.toUpperCase());
  }


}
