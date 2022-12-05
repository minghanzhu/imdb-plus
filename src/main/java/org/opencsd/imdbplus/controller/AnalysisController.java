package org.opencsd.imdbplus.controller;

import java.util.List;
import java.util.Map;
import org.opencsd.imdbplus.entity.Media;
import org.opencsd.imdbplus.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analysis")
public class AnalysisController {

  @Autowired
  private AnalysisService analysisService;

  /**
   * endpoint domain.com/api/v1/analysis/highest-rated
   * returns the highest rated media across timelines.
   * */
  @GetMapping("/highest-rated")
  public ResponseEntity<Media> highestRating() {
    Media highestRated = analysisService.getHighestRating();
    if(highestRated != null)
      return ResponseEntity.ok(highestRated);
    else
      return ResponseEntity.notFound().build();

  }

  /**
   * endpoint domain.com/api/v1/analysis/most/{category}
   * returns the highest rated media across timelines.
   * */
  @GetMapping("/most")
  public ResponseEntity<Media> getMostWatched(
      @RequestParam(name = "status", defaultValue = "DONE") String status) {
    status = (status.equalsIgnoreCase("progress")) ? "IN_PROGRESS" : status.toUpperCase();
    Media mostMediaInCategory = analysisService.getMediaInCategory(status.toUpperCase());
    return ResponseEntity.ok(mostMediaInCategory);

  }

  /**
   * endpoint domain.com/api/v1/analysis/top-ten/{category}
   * returns the top-ten highest rated media across timelines.
   * */
  @GetMapping("/top-ten")
  public ResponseEntity<List<Media>> getTopTen(
      @RequestParam(name = "status", defaultValue = "DONE") String status) {
    status = (status.equalsIgnoreCase("progress")) ? "IN_PROGRESS" : status.toUpperCase();
    List<Media> topTenList = analysisService.getTopTen(status);
    if(!topTenList.isEmpty())
      return ResponseEntity.ok(topTenList);
    else
      return ResponseEntity.notFound().build();

  }

  /**
   * endpoint domain.com/api/v1/analysis/userprofile/{userId}
   * returns  a map of media and the user's genre preference and number of media with that
   * */
  @GetMapping("/userprofile/{id}")
  public ResponseEntity<Map<String, Long>> getUserPreference(@PathVariable("id") String userId){
    Map<String, Long> mediaUserLikes = analysisService.userPreference(userId);
    if(!mediaUserLikes.isEmpty())
      return ResponseEntity.ok(mediaUserLikes);
    else
      return ResponseEntity.badRequest().build();
  }
}
