package com.example.imdbplus.conroller;

import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.example.imdbplus.entity.Media;
import com.example.imdbplus.entity.Timeline;
import com.example.imdbplus.repository.TimelineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
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
    public PaginatedScanList<Timeline> getTimeline(){
        return timelineRepository.getAllTimeline();
    }

    @GetMapping("/api/v1/timeline/analysis/popular")
    public Media getMostWatched(){
        return timelineRepository.mostWatched();
    }

    @GetMapping("/api/v1/timeline/analysis/rating")
    public Media highestRating(){
        return timelineRepository.highestRating();
    }

}
