package com.example.imdbplus.conroller;

import com.example.imdbplus.repository.AnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalysisController {

  @Autowired
  private AnalysisRepository analysisRepository;

  @GetMapping("api/v1/analysis/timelines")
  public ResponseEntity<?> getTimeline() {
    return analysisRepository.getAllTimeline();
  }

  @GetMapping("api/v1/analysis/highest-rated")
  public ResponseEntity highestRating() {
    try {
      return analysisRepository.getHighestRating();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Sorry, something went wrong.");
    }
  }

  @GetMapping("api/v1/analysis/most")
  public ResponseEntity getMostWatched(
      @RequestParam(name = "status", defaultValue = "DONE") String status) {
    status = (status.toLowerCase().equals("progress")) ? "IN_PROGRESS" : status.toUpperCase();
    return analysisRepository.getMostMediaWith(status.toUpperCase());

  }

  @GetMapping("api/v1/analysis/top-ten")
  public ResponseEntity getTopTen(
      @RequestParam(name = "status", defaultValue = "DONE") String status) {
    status = (status.toLowerCase().equals("progress")) ? "IN_PROGRESS" : status.toUpperCase();
    return analysisRepository.getTopTen(status);

  }
}
