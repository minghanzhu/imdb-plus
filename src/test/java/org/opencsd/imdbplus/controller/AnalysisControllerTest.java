package org.opencsd.imdbplus.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.opencsd.imdbplus.entity.Media;
import org.opencsd.imdbplus.repository.AnalysisRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;


@WebMvcTest(AnalysisController.class)
@ExtendWith(SpringExtension.class)
class AnalysisControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AnalysisRepository mockAnalysisRepo;

  private StringBuilder jsonString;
  private Media highestAvgRating;
  private Media popularMedia;
  private Media progressMedia;
  private List<Media> topTenListWanted;
  private List<Media> topTenListWished;

  @BeforeEach
  void setUp() {

    jsonString = new StringBuilder();
    jsonString.append("{\"mediaId\":\"tt0000001\",");
    jsonString.append("\"title\":\"The Watcher\",");
    jsonString.append("{\"release_date\":\"2012-07-17\",");
    jsonString.append("\"genre\":\"Action\"}");

    popularMedia = new Media("tt0000001", "The Watcher", "2012-07-17", "Action");
    highestAvgRating = new Media("tt1396484", "Compuational Heaven", "2032-07-17",
        "Drama");
    progressMedia = new Media();
    topTenListWanted = new ArrayList<>();
    topTenListWanted.add(popularMedia);
    topTenListWanted.add(highestAvgRating);

    topTenListWished = new ArrayList<>();
    topTenListWanted.add(popularMedia);
  }

  @AfterEach
  void tearDown() {
  }


  @Test
  void getTimeline() {
  }

  @Test
  void highestRating() throws Exception {
    ResponseEntity media = ResponseEntity.ok(highestAvgRating.toString());
    when(mockAnalysisRepo.getHighestRating()).thenReturn(media);
    RequestBuilder requestBuilder = get("/api/v1/analysis/highest-rated");
    mockMvc.perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(highestAvgRating.toString()));
  }

  @Test
  void getMostWatched() throws Exception {
    ResponseEntity media = ResponseEntity.ok(popularMedia.toString());
    when(mockAnalysisRepo.getMostMediaWith("DONE")).thenReturn(media);
    RequestBuilder request = get("/api/v1/analysis/most/");
    mockMvc.perform(request).andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(popularMedia.toString()));
  }

  @Test
  void getMostWatchedDone() throws Exception {
    ResponseEntity media = ResponseEntity.ok(popularMedia.toString());
    when(mockAnalysisRepo.getMostMediaWith("DONE")).thenReturn(media);
    RequestBuilder request = get("/api/v1/analysis/most/?status=done");
    mockMvc.perform(request).andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(popularMedia.toString()));
  }

  @Test
  void getMostWished() throws Exception {
    ResponseEntity media = ResponseEntity.ok(popularMedia.toString());
    when(mockAnalysisRepo.getMostMediaWith("WISHLIST")).thenReturn(media);
    RequestBuilder request = get("/api/v1/analysis/most/?status=wishlist");
    mockMvc.perform(request).andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(popularMedia.toString()));
  }

  @Test
  void getMostProgress() throws Exception {
    ResponseEntity media = ResponseEntity.ok(null);
    when(mockAnalysisRepo.getMostMediaWith("PROGRESS")).thenReturn(media);
    RequestBuilder request = get("/api/v1/analysis/most/?status=progress");
    mockMvc.perform(request).andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(""));
  }


  @Test
  void getTopTen() throws Exception {
    ResponseEntity topTen = ResponseEntity.ok(topTenListWanted.toString());
    when(mockAnalysisRepo.getTopTen("DONE")).thenReturn(topTen);
    RequestBuilder request = get("/api/v1/analysis/top-ten");
    mockMvc.perform(request).andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(topTenListWanted.toString()));
  }

  @Test
  void getTopTenWatched() throws Exception {
    ResponseEntity topTen = ResponseEntity.ok(topTenListWanted.toString());
    when(mockAnalysisRepo.getTopTen("DONE")).thenReturn(topTen);
    RequestBuilder request = get("/api/v1/analysis/top-ten/?status=done");
    mockMvc.perform(request).andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(topTenListWanted.toString()));
  }

  @Test
  void getTopTenWished() throws Exception {
    ResponseEntity topTen = ResponseEntity.ok(topTenListWished.toString());
    when(mockAnalysisRepo.getTopTen("WISHLIST")).thenReturn(topTen);
    RequestBuilder request = get("/api/v1/analysis/top-ten/?status=wishlist");
    mockMvc.perform(request).andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(topTenListWished.toString()));
  }

  @Test
  void getTopTenProgress() throws Exception {
    ResponseEntity topTen = ResponseEntity.ok(new ArrayList<Media>().toString());
    when(mockAnalysisRepo.getTopTen("IN_PROGRESS")).thenReturn(topTen);
    RequestBuilder request = get("/api/v1/analysis/top-ten/?status=progress");
    mockMvc.perform(request).andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(new ArrayList<Media>().toString()));
  }
}