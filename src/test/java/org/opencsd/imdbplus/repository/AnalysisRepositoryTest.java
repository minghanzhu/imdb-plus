package org.opencsd.imdbplus.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.opencsd.imdbplus.entity.Media;
import org.opencsd.imdbplus.entity.Timeline;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

@SpringBootTest
class AnalysisRepositoryTest {
  private Logger analysisLoggertest = LoggerFactory.getLogger(AnalysisRepository.class);

  private Media popularMedia;
  private Media highestAvgRating;
  private List<Media> topTenListWatched;
  private List<Media> topTenListWished;

  private List<Timeline> highestRated;

  private Timeline t1;

  private Timeline t2;

  private Timeline t3;

  private Timeline t4;

  @Autowired
  private AnalysisRepository analysisRepository;

  @BeforeEach
  void setUp() {
    analysisRepository = new AnalysisRepository();
    popularMedia = new Media("m1", "Movie 1", "2012-07-17", "Action");
    highestAvgRating = new Media("m2", "Movie 2", "2032-07-17",
        "Drama");
    t1 = new Timeline("u1-m1", "u1", "m1"," ", "DONE", 5, "It was great");
    t2 = new Timeline("u1-m2", "u1", "m2"," ", "DONE", 3, "It was good");
    t3 = new Timeline("u1-m1", "u1", "m1"," ", "DONE", 5, "It was great");
    t4 = new Timeline("u1-m2", "u1", "m2"," ", "DONE", 3, "It was good");

    highestRated = new ArrayList<>();
    highestRated.add(t1);
    highestRated.add(t2);
    highestRated.add(t3);
    highestRated.add(t4);


    topTenListWatched = new ArrayList<>();
    topTenListWatched.add(popularMedia);
    topTenListWatched.add(highestAvgRating);

    topTenListWished = new ArrayList<>();
    topTenListWished.add(popularMedia);
  }

  @AfterEach
  void tearDown() {
    analysisRepository = null;
    popularMedia = null;
    highestAvgRating = null;
    topTenListWatched.clear();
    topTenListWished.clear();
  }

  @Test
  void getAllTimeline() {

  }



  @Test
  void getHighestRating() {
    String highestRatedId = analysisRepository.getHighestRatingHelper(highestRated);
    assertEquals(popularMedia.getMediaId(), highestRatedId);
  }

  @Test
  void calculateMostDone() {
    when(analysisRepository.calculateMost("DONE")).thenReturn(popularMedia);
    Media result = analysisRepository.calculateMost("DONE");
    assertEquals(result, popularMedia);
    when(analysisRepository.calculateMost("WISHLIST")).thenReturn(new Media());
    result = analysisRepository.calculateMost("WISHLIST");
    assertEquals(result, new Media());
    when(analysisRepository.calculateMost("PROGRESS")).thenReturn(new Media());
    result = analysisRepository.calculateMost("PROGRESS");
    assertEquals(result, new Media());
  }

  @Test
  void getTopTenMost() {
    when(analysisRepository.getTopTenList("DONE")).thenReturn(topTenListWatched);
    List<Media> result = analysisRepository.getTopTenList("DONE");
    assertEquals(result, topTenListWatched);
    when(analysisRepository.getTopTenList("WISHLIST")).thenReturn(topTenListWished);
    result = analysisRepository.getTopTenList("WISHLIST");
    assertEquals(result, topTenListWished);
    when(analysisRepository.getTopTenList("PROGRESS")).thenReturn(new ArrayList<>());
    result = analysisRepository.getTopTenList("PROGRESS");
    assertEquals(result, new ArrayList<>());
  }
}