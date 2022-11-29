package org.opencsd.imdbplus.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.opencsd.imdbplus.entity.Media;
import org.opencsd.imdbplus.entity.Timeline;

import java.util.*;

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

  private Timeline highestRating1;

  private Timeline highestRating2;

  private Timeline highestRating3;

  private Timeline highestRating4;

  private Map<String, Long> topTenDone;

  private Map<String, Long> topTenWishList;

  private Map<String, Long> topTenInProgress;

  @Autowired
  private AnalysisRepository analysisRepository;

  @BeforeEach
  void setUp() {
    analysisRepository = new AnalysisRepository();
    popularMedia = new Media("m1", "Movie 1", "2012-07-17", "Action");
    highestAvgRating = new Media("m2", "Movie 2", "2032-07-17",
        "Drama");
    highestRating1 = new Timeline("u1-m1", "u1", "m1"," ", "DONE", 5, "It was great");
    highestRating2 = new Timeline("u1-m2", "u1", "m2"," ", "DONE", 3, "It was good");
    highestRating3 = new Timeline("u1-m1", "u1", "m1"," ", "DONE", 5, "It was great");
    highestRating4 = new Timeline("u1-m2", "u1", "m2"," ", "DONE", 3, "It was good");
    highestRating4 = new Timeline("u1-m2", "u1", "m2"," ", "DONE", 3, "It was good");

    highestRated = new ArrayList<>();
    highestRated.add(highestRating1);
    highestRated.add(highestRating2);
    highestRated.add(highestRating3);
    highestRated.add(highestRating4);

    topTenDone.put("m1", 5L);
    topTenDone.put("m2", 4L);
    topTenDone.put("m3", 4L);
    topTenDone.put("m4", 6L);
    topTenDone.put("m5", 5L);
    topTenDone.put("m6", 7L);
    topTenDone.put("m7", 8L);
    topTenDone.put("m8", 9L);
    topTenDone.put("m9", 10L);
    topTenDone.put("m10", 11L);
    topTenDone.put("m11", 12L);
    topTenDone.put("m12", 12L);


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

//  @Test
//  void calculateMostDone() {
//    when(analysisRepository.calculateMost("DONE")).thenReturn(popularMedia);
//    Media result = analysisRepository.calculateMost("DONE");
//    assertEquals(result, popularMedia);
//    when(analysisRepository.calculateMost("WISHLIST")).thenReturn(new Media());
//    result = analysisRepository.calculateMost("WISHLIST");
//    assertEquals(result, new Media());
//    when(analysisRepository.calculateMost("PROGRESS")).thenReturn(new Media());
//    result = analysisRepository.calculateMost("PROGRESS");
//    assertEquals(result, new Media());
//  }

  @Test
  void getTopTenMost() {
//    PriorityQueue<MediaWrapper> topTenQueue = analysisRepository.getTopTenListHelper(topTenDone);
//    PriorityQueue<MediaWrapper> topTenQueueResult = new PriorityQueue<MediaWrapper>(10);
//
//    assertEquals(topTenQueueResult, topTenQueue);
//    when(analysisRepository.getTopTenList("WISHLIST")).thenReturn(topTenListWished);
//    result = analysisRepository.getTopTenList("WISHLIST");
//    assertEquals(result, topTenListWished);
//    when(analysisRepository.getTopTenList("PROGRESS")).thenReturn(new ArrayList<>());
//    result = analysisRepository.getTopTenList("PROGRESS");
//    assertEquals(result, new ArrayList<>());
  }
}

