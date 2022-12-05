package org.opencsd.imdbplus.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opencsd.imdbplus.entity.Media;
import org.opencsd.imdbplus.entity.Timeline;
import org.opencsd.imdbplus.repository.AnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class AnalysisServiceTest {

  @Autowired
  private AnalysisService analysisService;
  @MockBean
  private AnalysisRepository analysisRepository;

  private Media highestAvgRating;
  private Media popularMedia;
  private Media progressMedia;
  private Media wishedMedia;
  private List<Media> topTenListWatched;
  private List<Media> topTenListProgress;
  private List<Media> topTenListWished;
  private List<Timeline> allTimelines;
  private List<Timeline> doneTimelines;
  private List<Timeline> progressTimelines;
  private List<Timeline> wishTimelines;


  @BeforeEach
  void setUp() {
    analysisService = new AnalysisService();
    analysisRepository = mock(AnalysisRepository.class);
    analysisService.setAnalysisRepository(analysisRepository);

    Media m1 =  new Media("m1", "Movie 1", "2012-07-17", "Action");
    Media m2 =  new Media("m2", "Movie 2", "2012-07-17", "Action");
    Media m3 =  new Media("m3", "Movie 3", "2012-07-17", "Action");
    Media m4 =  new Media("m4", "Movie 4", "2012-07-17", "Action");
    Media m5 =  new Media("m5", "Movie 5", "2012-07-17", "Action");

    Timeline t1 = new Timeline("t1-u1-m1", "u1", "m1", new Date(), new Date(), "DONE", 5, "It was great");
    Timeline t2 = new Timeline("t2-u1-m1", "u1", "m4", new Date(), new Date(), "DONE", 1, "It was terrible");
    Timeline t6 = new Timeline("t1-u1-m1", "u1", "m3", new Date(), new Date(), "DONE", 5, "It was great");
    Timeline t7 = new Timeline("t2-u1-m1", "u1", "m1", new Date(), new Date(), "DONE", 1, "It was terrible");
    Timeline t4 = new Timeline("t4-u2-m1", "u2", "m1", new Date(), new Date(), "WISHLIST", 5, "I head this was great.");
    Timeline t5 = new Timeline("t5-u1-m4", "u1", "m5", new Date(), new Date(), "WISHLIST", 0, "I REALLY WANT TO SEE IT.");
    Timeline t9 = new Timeline("t4-u2-m1", "u2", "m5", new Date(), new Date(), "WISHLIST", 5, "I head this was great.");
    Timeline t10 = new Timeline("t5-u1-m4", "u1", "m5", new Date(), new Date(), "WISHLIST", 0, "I REALLY WANT TO SEE IT.");
    Timeline t3 = new Timeline("t3-u3-m3", "u3", "m2", new Date(), new Date(), "PROGRESS", 5, "Still in progress.");
    Timeline t8 = new Timeline("t3-u3-m3", "u3", "m1", new Date(), new Date(), "PROGRESS", 2, "Still in progress.");
    Timeline t11 = new Timeline("t3-u3-m3", "u4", "m2", new Date(), new Date(), "PROGRESS", 5, "Still in progress.");

    popularMedia = m1;
    highestAvgRating = m3;
    progressMedia = m2;
    wishedMedia = m5;

    topTenListWatched = Arrays.asList(m3, m4, m1);
    topTenListProgress = Arrays.asList(m1, m2);
    topTenListWished = Arrays.asList(m1, m5);

    allTimelines = Arrays.asList(t1, t2, t3, t4, t5, t5, t6, t7, t8, t9, t10, t11);
    doneTimelines = Arrays.asList(t1, t2, t6, t7);
    progressTimelines = Arrays.asList(t3, t8, t11);
    wishTimelines = Arrays.asList(t4,t5, t9, t10);
  }

  @AfterEach
  void tearDown() {
  }


  @Test
  void highestRatingHelper() {
    when(analysisRepository.getAllTimelines()).thenReturn(allTimelines);
    String mediaId = analysisService.highestRatingHelper();
    assertEquals(mediaId, highestAvgRating.getMediaId());
  }

  @Test
  void getHighestRating() {
    when(analysisRepository.getAllTimelines()).thenReturn(allTimelines);
    when(analysisRepository.getMedia(highestAvgRating.getMediaId())).thenReturn(popularMedia);
    Media highestAvg = analysisService.getHighestRating();
    assertEquals(popularMedia, highestAvg);
  }

  @Test
  void getHighestNotFound() {
    when(analysisRepository.getAllTimelines()).thenReturn(allTimelines);
    when(analysisRepository.getMedia(highestAvgRating.getMediaId())).thenReturn(null);
    Media highestAvg = analysisService.getHighestRating();
    assertEquals(null, highestAvg);
  }

  @Test
  void getMediaInDone() {
    when(analysisRepository.getTimelineListByByFilter("DONE")).thenReturn(doneTimelines);
    when(analysisRepository.getMedia(popularMedia.getMediaId())).thenReturn(popularMedia);

    Media media = analysisService.getMediaInCategory("DONE");
    assertEquals(popularMedia, media);
  }

  @Test
  void getMediaInWish() {
    when(analysisRepository.getTimelineListByByFilter("WISHLIST")).thenReturn(wishTimelines);
    when(analysisRepository.getMedia(wishedMedia.getMediaId())).thenReturn(wishedMedia);

    Media media = analysisService.getMediaInCategory("WISHLIST");
    assertEquals(wishedMedia, media);
  }

  @Test
  void getMediaInProgress() {
    when(analysisRepository.getTimelineListByByFilter("IN_PROGRESS")).thenReturn(progressTimelines);
    when(analysisRepository.getMedia(progressMedia.getMediaId())).thenReturn(progressMedia);
    Media media = analysisService.getMediaInCategory("IN_PROGRESS");
    assertEquals(progressMedia, media);
  }

  @Test
  void getMediaNotFound() {
    when(analysisRepository.getTimelineListByByFilter("IN_PROGRESS")).thenReturn(allTimelines);
    when(analysisRepository.getMedia(progressMedia.getMediaId())).thenReturn(null);
    Media media = analysisService.getMediaInCategory("IN_PROGRESS");
    assertEquals(null, media);
  }

  @Test
  void getTopTenDone() {
    String id1  = topTenListWatched.get(0).getMediaId();
    String id2  = topTenListWatched.get(1).getMediaId();
    String id3  = topTenListWatched.get(2).getMediaId();
    when(analysisRepository.getTimelineListByByFilter("DONE")).thenReturn(doneTimelines);
    when(analysisRepository.getMedia(id1)).thenReturn(topTenListWatched.get(0));
    when(analysisRepository.getMedia(id2)).thenReturn(topTenListWatched.get(1));
    when(analysisRepository.getMedia(id3)).thenReturn(topTenListWatched.get(2));

    List<Media> mediaList = analysisService.getTopTen("DONE");
    assertEquals(topTenListWatched, mediaList);
  }

  @Test
  void getTopTenWished() {
    String id1  = wishTimelines.get(0).getMediaId();
    String id2  = wishTimelines.get(1).getMediaId();


    when(analysisRepository.getTimelineListByByFilter("WISHLIST")).thenReturn(wishTimelines);
    when(analysisRepository.getMedia(id1)).thenReturn(topTenListWished.get(0));
    when(analysisRepository.getMedia(id2)).thenReturn(topTenListWished.get(1));

    List<Media> mediaList = analysisService.getTopTen("WISHLIST");
    assertEquals(topTenListWished, mediaList);
  }

  @Test
  void getTopTenProgress() {
    String id1  = progressTimelines.get(0).getMediaId();
    String id2  = progressTimelines.get(1).getMediaId();

    when(analysisRepository.getTimelineListByByFilter("IN_PROGRESS")).thenReturn(progressTimelines);
    when(analysisRepository.getMedia(id1)).thenReturn(topTenListProgress.get(0));
    when(analysisRepository.getMedia(id2)).thenReturn(topTenListProgress.get(1));

    List<Media> mediaList = analysisService.getTopTen("IN_PROGRESS");

    assertEquals(topTenListProgress.size(), mediaList.size());
  }

  @Test
  void userPreference() {
  }
}