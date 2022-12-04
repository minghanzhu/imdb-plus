package org.opencsd.imdbplus.controller;

import static org.mockito.Mockito.when;
import static org.opencsd.imdbplus.controller.UserControllerTest.asJsonString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opencsd.imdbplus.entity.Media;
import org.opencsd.imdbplus.entity.Timeline;
import org.opencsd.imdbplus.entity.User;
import org.opencsd.imdbplus.repository.MediaRepository;
import org.opencsd.imdbplus.repository.TimelineRepository;
import org.opencsd.imdbplus.repository.UserRepository;
import org.opencsd.imdbplus.service.TimelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(TimelineController.class)
@AutoConfigureMockMvc
class TimelineControllerTest {

  @MockBean
  TimelineService timelineService;
  @MockBean
  private UserRepository userRepository;
  @MockBean
  private MediaRepository mediaRepository;
  @MockBean
  TimelineRepository timelineRepository;

  @Autowired
  MockMvc mockMvc;

  private Media testMedia;
  private User testUser;
  private String userToken;
  private Timeline postedTimeline;
  private List<Timeline> userTimelines;
  private List<Timeline> mediaTimelines;
  private List<Timeline> allTimelines;


  @BeforeEach
  void setUp(){

    Timeline t1 = new Timeline("u1-m1", "u1", "m1", new Date(System.currentTimeMillis()) , new Date(System.currentTimeMillis()), "DONE", 5, "It was great");
    Timeline t2 = new Timeline("t2-u1-m1", "u1", "m4", new Date(), new Date(), "DONE", 1, "It was terrible");
    Timeline t3 = new Timeline("t3-u3-m3", "u3", "m3", new Date(), new Date(), "PROGRESS", 3, "Still in progress.");
    Timeline t4 = new Timeline("t4-u2-m1", "u2", "m1", new Date(), new Date(), "WISHLIST", 5, "I head this was great.");
    Timeline t5 = new Timeline("t5-u1-m4", "u1", "m5", new Date(), new Date(), "WISHLIST", 0, "I REALLY WANT TO SEE IT.");

    testMedia = new Media("m1", "movie", "2012-07-17", "Action");
    postedTimeline = t2;

    userTimelines = Arrays.asList(t1, t5);
    mediaTimelines = Arrays.asList(t1, t4);
    allTimelines = Arrays.asList(t1, t2, t3, t4, t5);

    userToken = "6d16c160-715e-4393-b3a4-0d5de90ad7e4";
  }

  void tearDown(){

  }

  @Test
  void saveTimeline() throws Exception {
    Timeline testLine = new Timeline("t2-u1-m1", "u1", "m4", new Date(), new Date(), "DONE", 1, "It was terrible");
    String token = "6d16c160-715e-4393-b3a4-0d5de90ad7e4";

    when(timelineService.save(testLine, token)).thenReturn(allTimelines.get(1));

    RequestBuilder requestBuilderSucess = post("/user/timeline")
        .content(asJsonString(testLine))
        .header("Authorization", token)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON);

    mockMvc.perform(requestBuilderSucess)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.timelineId").value(postedTimeline.getTimelineId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.mediaId").value(postedTimeline.getMediaId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.userId").value(postedTimeline.getUserId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.rating").value(postedTimeline.getRating()));

    String wrongToken = "some-token";
    RequestBuilder requestBuilderFail = post("/user/timeline")
        .content(asJsonString(testLine))
        .header("Authorization", wrongToken)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON);

    mockMvc.perform(requestBuilderFail)
        .andDo(print())
        .andExpect(status().is(401))
        .andExpect(content().string("Unauthorized"));

  }

  @Test
  void deleteTimelineSuccess() throws Exception {

    when(timelineService.delete(allTimelines.get(4).getTimelineId(), userToken)).thenReturn("Timeline deleted successfully");

    RequestBuilder deleteRequestSucess = delete("/user/timeline/{timelineId}", allTimelines.get(4).getTimelineId())
        .content(asJsonString(allTimelines.get(4)))
        .header("Authorization", userToken)
        .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(deleteRequestSucess)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string("Timeline deleted successfully"));

  }

  @Test
  void deleteTimelineFailed() throws Exception {
    when(timelineService.delete(allTimelines.get(3).getTimelineId(), userToken)).thenReturn("Invalid access token");


    RequestBuilder deleteRequestSucess = delete("/user/timeline/{timelineId}", allTimelines.get(3).getTimelineId())
        .content(asJsonString(allTimelines.get(2)))
        .header("Authorization", userToken)
        .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(deleteRequestSucess)
        .andDo(print())
        .andExpect(status().is(401))
        .andExpect(content().string("Invalid access token"));

  }

  @Test
  void deleteTimelineNotFound() throws Exception {
    when(timelineService.delete(allTimelines.get(3).getTimelineId(), userToken)).thenReturn("Timeline not found");


    RequestBuilder deleteRequestSucess = delete("/user/timeline/{timelineId}", allTimelines.get(3).getTimelineId())
        .content(asJsonString(allTimelines.get(2)))
        .header("Authorization", userToken)
        .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(deleteRequestSucess)
        .andDo(print())
        .andExpect(status().is(404))
        .andExpect(content().string("Timeline not found"));

  }

  @Test
  void getTimeline() throws Exception {
    String userId = "u3";
    when(timelineService.getTimelineByUserId(userId)).thenReturn(userTimelines);

    RequestBuilder getRequest = get("/user/timeline/{userId}", userId)
        .header("Authorization", userToken)
        .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(getRequest)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[*].timelineId").isArray())
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[0].timelineId").value(userTimelines.get(0).getTimelineId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[1].timelineId").value(userTimelines.get(1).getTimelineId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[0].userId").value(userTimelines.get(0).getUserId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[1].userId").value(userTimelines.get(1).getUserId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[0].mediaId").value(userTimelines.get(0).getMediaId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[1].mediaId").value(userTimelines.get(1).getMediaId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[0].status").value(userTimelines.get(0).getStatus()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[1].status").value(userTimelines.get(1).getStatus()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[0].comment").value(userTimelines.get(0).getComment()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[1].comment").value(userTimelines.get(1).getComment()));
  }

  @Test
  void getTimelineNotFound() throws Exception {
    String userId = "u3";
    when(timelineService.getTimelineByUserId(userId)).thenReturn(null);

    RequestBuilder getRequest = get("/user/timeline/{userId}", userId)
        .header("Authorization", userToken)
        .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(getRequest)
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(content().string("Timeline not found"));
  }

  @Test
  void getTimelineByMediaId() throws Exception {
    String mediaId = "m1";
    when(timelineService.getTimelineByMediaId(mediaId)).thenReturn(mediaTimelines);

    RequestBuilder getRequest = get("/user/timeline/media/{mediaId}", mediaId)
        .header("Authorization", userToken)
        .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(getRequest)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[*].timelineId").isArray())
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[0].timelineId").value(mediaTimelines.get(0).getTimelineId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[1].timelineId").value(mediaTimelines.get(1).getTimelineId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[0].userId").value(mediaTimelines.get(0).getUserId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[1].userId").value(mediaTimelines.get(1).getUserId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[0].mediaId").value(mediaTimelines.get(0).getMediaId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[1].mediaId").value(mediaTimelines.get(1).getMediaId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[0].status").value(mediaTimelines.get(0).getStatus()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[1].status").value(mediaTimelines.get(1).getStatus()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[0].comment").value(mediaTimelines.get(0).getComment()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[1].comment").value(mediaTimelines.get(1).getComment()));
  }
  @Test
  void getTimelineByMediaNotFound() throws Exception {
    String mediaId = "m3-non";
    when(timelineService.getTimelineByMediaId(mediaId)).thenReturn(null);

    RequestBuilder getRequest = get("/user/timeline/media/{userId}", mediaId)
        .header("Authorization", userToken)
        .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(getRequest)
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(content().string("Timeline not found"));
  }

  @Test
  void getTimelineByUserIdAndMediaId() throws Exception {
    String mediaId = "41";
    String userId = "u1";
    when(timelineService.getTimelineByUserIdAndMediaId(userId, mediaId)).thenReturn(postedTimeline);

    RequestBuilder getRequest = get("/user/timeline/{userId}/{mediaId}", userId, mediaId)
        .header("Authorization", userToken)
        .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(getRequest)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.timelineId").value(postedTimeline.getTimelineId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.userId").value(postedTimeline.getUserId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.mediaId").value(postedTimeline.getMediaId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.status").value(postedTimeline.getStatus()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.rating").value(postedTimeline.getRating()))
        .andExpect(MockMvcResultMatchers.
          jsonPath("$.comment").value(postedTimeline.getComment()));
  }
  @Test
  void getTimelineByUserMediaNotFound() throws Exception {
    String mediaId = "m3-23";
    String userId = "u3-h4";
    when(timelineService.getTimelineByUserIdAndMediaId(userId, mediaId)).thenReturn(null);

    RequestBuilder getRequest = get("/user/timeline/{userId}/{mediaId}", userId, mediaId)
        .header("Authorization", userToken)
        .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(getRequest)
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(content().string("Timeline not found"));
  }

}