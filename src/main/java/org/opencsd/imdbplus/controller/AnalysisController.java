package org.opencsd.imdbplus.controller;

import org.opencsd.imdbplus.entity.Media;
import org.opencsd.imdbplus.entity.Timeline;
import org.opencsd.imdbplus.repository.AnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analysis")
public class AnalysisController {

  @Autowired
  private AnalysisRepository analysisRepository;

  @GetMapping("/highest-rated")
  public ResponseEntity highestRating() {
    try {
      return analysisRepository.getHighestRating();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Sorry, something went wrong.");
    }
  }

  @GetMapping("/most")
  public ResponseEntity<Media> getMostWatched(
      @RequestParam(name = "status", defaultValue = "DONE") String status) {
    status = (status.equalsIgnoreCase("progress")) ? "IN_PROGRESS" : status.toUpperCase();
    return analysisRepository.getMostMediaWith(status.toUpperCase());

  }

  @GetMapping("/top-ten")
  public ResponseEntity<Media> getTopTen(
      @RequestParam(name = "status", defaultValue = "DONE") String status) {
    status = (status.equalsIgnoreCase("progress")) ? "IN_PROGRESS" : status.toUpperCase();
    return analysisRepository.getTopTen(status);

  }

  @GetMapping("/userprofile/{id}")
  public ResponseEntity getUserPreference(@PathVariable("id") String userId){
    return analysisRepository.userPreference(userId);
  }
}
