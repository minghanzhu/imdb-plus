package org.opencsd.imdbplus.controller;

import static org.mockito.Mockito.when;
import static org.opencsd.imdbplus.controller.UserControllerTest.asJsonString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import org.opencsd.imdbplus.entity.Media;
import org.opencsd.imdbplus.entity.Timeline;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opencsd.imdbplus.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@WebMvcTest(AnalysisController.class)
@ExtendWith(SpringExtension.class)
class AnalysisControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AnalysisService mockAnalysisService;

  private Media highestAvgRating;
  private Media popularMedia;
  private Media progressMedia;
  private Media wishedMedia;
  private List<Media> topTenListWanted;
  private List<Media> topTenListProgress;
  private List<Media> topTenListWished;


  @BeforeEach
  void setUp() throws IOException {

    popularMedia = new Media("tt0000001", "The Watcher", "2012-07-17", "Action");
    progressMedia = new Media("tt0000020", "The Arrival", "2022-09-17", "Adventure");
    wishedMedia = new Media("tt003031", "Hounds of Love", "2022-07-17", "Action");
    highestAvgRating = new Media("tt1396484", "Comp. Heaven", "2032-07-17", "Drama");
    Media other = new Media("tt0000020", "The Arrival", "2022-09-17", "Adventure");

    topTenListWanted = Arrays.asList(popularMedia, wishedMedia, progressMedia);
    topTenListWished = Arrays.asList(popularMedia, wishedMedia);
    topTenListProgress = Arrays.asList(progressMedia, other);

  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void highestRating() throws Exception {
    Media testMedia = new Media("tt1396484", "Comp. Heaven", "2032-07-17", "Drama");
    when(mockAnalysisService.getHighestRating()).thenReturn(testMedia);
    RequestBuilder requestBuilder = get("/api/v1/analysis/highest-rated");
    mockMvc.perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.mediaId").value(highestAvgRating.getMediaId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.title").value(highestAvgRating.getTitle()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.release_date").value(highestAvgRating.getRelease_date()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.genre").value(highestAvgRating.getGenre()));
  }
  @Test
  void highestRatingNotFound() throws Exception {
    when(mockAnalysisService.getHighestRating()).thenReturn(null);
    RequestBuilder requestBuilder = get("/api/v1/analysis/highest-rated");
    mockMvc.perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  void getMostWatched() throws Exception {
    when(mockAnalysisService.getMediaInCategory("DONE")).thenReturn(popularMedia);
    RequestBuilder request = get("/api/v1/analysis/most/");
    mockMvc.perform(request)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.mediaId").value(popularMedia.getMediaId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.title").value(popularMedia.getTitle()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.release_date").value(popularMedia.getRelease_date()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.genre").value(popularMedia.getGenre()));
  }

  @Test
  void getMostWatchedDone() throws Exception {
    when(mockAnalysisService.getMediaInCategory("DONE")).thenReturn(popularMedia);
    RequestBuilder request = get("/api/v1/analysis/most/?status=done");
    mockMvc.perform(request)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.mediaId").value(popularMedia.getMediaId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.title").value(popularMedia.getTitle()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.release_date").value(popularMedia.getRelease_date()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.genre").value(popularMedia.getGenre()));
  }

  @Test
  void getMostWished() throws Exception {
    when(mockAnalysisService.getMediaInCategory("WISHLIST")).thenReturn(wishedMedia);
    RequestBuilder request = get("/api/v1/analysis/most?status=wishlist");
    mockMvc.perform(request)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.mediaId").value(wishedMedia.getMediaId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.title").value(wishedMedia.getTitle()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.release_date").value(wishedMedia.getRelease_date()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.genre").value(wishedMedia.getGenre()));

  }

  @Test
  void getMostProgress() throws Exception {
    when(mockAnalysisService.getMediaInCategory("IN_PROGRESS")).thenReturn(progressMedia);
    RequestBuilder request = get("/api/v1/analysis/most/?status=progress");
    mockMvc.perform(request)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.mediaId").value(progressMedia.getMediaId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.title").value(progressMedia.getTitle()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.release_date").value(progressMedia.getRelease_date()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.genre").value(progressMedia.getGenre()));
  }


  @Test
  void getTopTen() throws Exception {
    when(mockAnalysisService.getTopTen("DONE")).thenReturn(topTenListWanted);
    RequestBuilder request = get("/api/v1/analysis/top-ten");
    mockMvc.perform(request).andDo(print())
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[*].mediaId").isArray())
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[0].mediaId").value(topTenListWanted.get(0).getMediaId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[1].mediaId").value(topTenListWanted.get(1).getMediaId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[0].title").value(topTenListWanted.get(0).getTitle()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[1].title").value(topTenListWanted.get(1).getTitle()));
  }

  @Test
  void getTopTenWatched() throws Exception {
    when(mockAnalysisService.getTopTen("DONE")).thenReturn(topTenListWanted);
    RequestBuilder request = get("/api/v1/analysis/top-ten/?status=done");
    mockMvc.perform(request).andDo(print())
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[*].mediaId").isArray())
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[0].mediaId").value(topTenListWanted.get(0).getMediaId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[1].mediaId").value(topTenListWanted.get(1).getMediaId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[0].title").value(topTenListWanted.get(0).getTitle()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[1].title").value(topTenListWanted.get(1).getTitle()));
  }

  @Test
  void getEmptyList() throws Exception {
    when(mockAnalysisService.getTopTen("DONE")).thenReturn(new ArrayList<>());
    RequestBuilder request = get("/api/v1/analysis/top-ten/?status=done");
    mockMvc.perform(request).
        andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  void getTopTenWished() throws Exception {
    when(mockAnalysisService.getTopTen("WISHLIST")).thenReturn(topTenListWished);
    RequestBuilder request = get("/api/v1/analysis/top-ten/?status=wishlist");
    mockMvc.perform(request).andDo(print())
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[*].mediaId").isArray())
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[0].mediaId").value(topTenListWished.get(0).getMediaId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[1].mediaId").value(topTenListWished.get(1).getMediaId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[0].title").value(topTenListWished.get(0).getTitle()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[1].title").value(topTenListWished.get(1).getTitle()));
  }

  @Test
  void getTopTenProgress() throws Exception {
    when(mockAnalysisService.getTopTen("IN_PROGRESS")).thenReturn(topTenListProgress);
    RequestBuilder request = get("/api/v1/analysis/top-ten/?status=progress");
    mockMvc.perform(request).andDo(print())
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[*].mediaId").isArray())
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[0].mediaId").value(topTenListProgress.get(0).getMediaId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[1].mediaId").value(topTenListProgress.get(1).getMediaId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[0].title").value(topTenListProgress.get(0).getTitle()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[1].title").value(topTenListProgress.get(1).getTitle()));
  }

  /**
   * Simple file reader that returns a list of media
   * */
  public List<Media> readMediaFile(String path) throws IOException {
    FileInputStream inputStream = new FileInputStream(path);
    List<Media> readList = new ArrayList<>();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

    while(bufferedReader.ready()){
      String line = bufferedReader.readLine();
      String[] lineValues = line.split(",");
      if(lineValues.length == 4){
        Media media = new Media(lineValues[0], lineValues[1], lineValues[2], lineValues[3]);
        readList.add(media);
      }
    }
    return readList;
  }

  public List<Timeline> readTimelineFile(String path) throws IOException {
    FileInputStream inputStream = new FileInputStream(path);
    List<Timeline> readTimeList = new ArrayList<>();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

    while(bufferedReader.ready()){
      String line = bufferedReader.readLine();
      String[] lineValues = line.split(",");
      if(lineValues.length == 6){
        Date timeStamp = new Date();
        Timeline timeline = new Timeline(lineValues[0], lineValues[1], lineValues[2],
            timeStamp, timeStamp,lineValues[3], Integer.parseInt(lineValues[4]), lineValues[5]);
        readTimeList.add(timeline);
      }
    }
    return readTimeList;
  }
}