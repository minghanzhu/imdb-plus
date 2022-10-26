package com.example.imdbplus.repository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.example.imdbplus.entity.Media;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

@SpringBootTest
class AnalysisRepositoryTest {

  private Media popularMedia;
  private Media highestAvgRating;
  private List<Media> topTenListWatched;
  private List<Media> topTenListWished;
  private AnalysisRepository analysisRepository;

  @BeforeEach
  void setUp() {
    analysisRepository = Mockito.mock(AnalysisRepository.class);
    popularMedia = new Media("tt0000001", "The Watcher", "2012-07-17", "Action");
    highestAvgRating = new Media("tt1396484", "Compuational Heaven", "2032-07-17",
        "Drama");
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
    when(analysisRepository.getHighestRating()).thenReturn(ResponseEntity.ok(popularMedia));
    ResponseEntity response = analysisRepository.getHighestRating();
    assertEquals(popularMedia.toString(), response.getBody().toString());
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