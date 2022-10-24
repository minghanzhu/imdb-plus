package com.example.imdbplus.conroller;

import com.example.imdbplus.entity.Timeline;
import com.example.imdbplus.repository.TimelineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TimelineController {

    @Autowired
    private TimelineRepository timelineRepository;

    @PostMapping("/timeline")
    public ResponseEntity saveTimeline(@RequestBody() Timeline timeline, @RequestHeader("Authorization") String accessToken) {
        return timelineRepository.save(timeline, accessToken);
    }

    @DeleteMapping("/timeline/{userId}/{mediaId}")
    public ResponseEntity deleteTimeline(@PathVariable("userId") String userId,
                                         @PathVariable("mediaId") String mediaId,
                                         @RequestHeader("Authorization") String accessToken) {
        return timelineRepository.delete(userId, mediaId, accessToken);
    }

    // Get all timelines by userId
    @GetMapping("/timeline/user/{userId}")
    public ResponseEntity getTimeline(@PathVariable("userId") String userId) {
        return timelineRepository.getTimelineByUserId(userId);
    }

    // Get all timelines by mediaId
    @GetMapping("/timeline/media/{mediaId}")
    public ResponseEntity getTimelineByMediaId(@PathVariable("mediaId") String mediaId) {
        return timelineRepository.getTimelineByMediaId(mediaId);
    }

    // Get a timeline by userId and mediaId
    @GetMapping("/timeline/{userId}/{mediaId}")
    public ResponseEntity getTimelineByUserIdAndMediaId(@PathVariable("userId") String userId,
                                                        @PathVariable("mediaId") String mediaId) {
        return timelineRepository.getTimelineByUserIdAndMediaId(userId, mediaId);
    }
}
