package com.example.imdbplus.conroller;

import com.example.imdbplus.entity.Media;
import com.example.imdbplus.entity.Timeline;
import com.example.imdbplus.repository.TimelineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/api/v1/timeline")
    public ResponseEntity getAllTimeline(){
        return timelineRepository.getAllEntities();
    }

    @GetMapping("/api/v1/timeline/analysis/")
    public Media getMostWatched(){
        return timelineRepository.mostWatched();
    }

    @GetMapping("/api/v1/timeline/analysis/userprofile/{id}")
    public ResponseEntity getUserPreference(@PathVariable("id") String userId){
        return timelineRepository.userPreference(userId);
    }

}
