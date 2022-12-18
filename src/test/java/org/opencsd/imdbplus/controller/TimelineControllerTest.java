package org.opencsd.imdbplus.controller;

import static org.mockito.Mockito.when;
import static org.opencsd.imdbplus.controller.ClientControllerTest.asJsonString;
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
import org.opencsd.imdbplus.entity.Client;
import org.opencsd.imdbplus.repository.MediaRepository;
import org.opencsd.imdbplus.repository.TimelineRepository;
import org.opencsd.imdbplus.repository.ClientRepository;
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
  private ClientRepository clientRepository;
  @MockBean
  private MediaRepository mediaRepository;
  @MockBean
  TimelineRepository timelineRepository;

  @Autowired
  MockMvc mockMvc;

  private Media testMedia;
  private Client testClient;
  private String clientToken;
  private Timeline postedTimeline;
  private List<Timeline> clientTimelines;
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

    clientTimelines = Arrays.asList(t1, t5);
    mediaTimelines = Arrays.asList(t1, t4);
    allTimelines = Arrays.asList(t1, t2, t3, t4, t5);

    clientToken = "6d16c160-715e-4393-b3a4-0d5de90ad7e4";
  }

  void tearDown(){

  }

  @Test
  void saveTimeline() throws Exception {
    Timeline testLine = new Timeline("t2-u1-m1", "u1", "m4", new Date(), new Date(), "DONE", 1, "It was terrible");
    String token = "6d16c160-715e-4393-b3a4-0d5de90ad7e4";

    when(timelineService.save(testLine, token)).thenReturn(allTimelines.get(1));

    RequestBuilder requestBuilderSucess = post("/timeline")
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
            jsonPath("$.clientId").value(postedTimeline.getClientId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.rating").value(postedTimeline.getRating()));

    String wrongToken = "some-token";
    RequestBuilder requestBuilderFail = post("/timeline")
        .content(asJsonString(testLine))
        .header("Authorization", wrongToken)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON);

    mockMvc.perform(requestBuilderFail)
        .andDo(print())
        .andExpect(status().is(401))
        .andExpect(content().string(""));

  }

  @Test
  void deleteTimelineSuccess() throws Exception {

    when(timelineService.delete(allTimelines.get(4).getTimelineId(), clientToken)).thenReturn("Timeline deleted successfully");
//    String clientId = allTimelines.get(4).getClientId();
    String timelineId = allTimelines.get(4).getTimelineId();
    RequestBuilder deleteRequestSucess = delete("/timeline/{timelineId}", timelineId)
        .content(asJsonString(allTimelines.get(4)))
        .header("Authorization", clientToken)
        .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(deleteRequestSucess)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string("Timeline deleted successfully"));

  }

  @Test
  void deleteTimelineFailed() throws Exception {
    when(timelineService.delete(allTimelines.get(3).getTimelineId(), clientToken)).thenReturn("Invalid access token");


    RequestBuilder deleteRequestSucess = delete("/timeline/{timelineId}", allTimelines.get(3).getTimelineId())
        .content(asJsonString(allTimelines.get(2)))
        .header("Authorization", clientToken)
        .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(deleteRequestSucess)
        .andDo(print())
        .andExpect(status().is(401))
        .andExpect(content().string("Invalid access token"));

  }

  @Test
  void deleteTimelineNotFound() throws Exception {
    when(timelineService.delete(allTimelines.get(3).getTimelineId(), clientToken)).thenReturn("Timeline not found");


    RequestBuilder deleteRequestSucess = delete("/timeline/{timelineId}", allTimelines.get(3).getTimelineId())
        .content(asJsonString(allTimelines.get(2)))
        .header("Authorization", clientToken)
        .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(deleteRequestSucess)
        .andDo(print())
        .andExpect(status().is(404))
        .andExpect(content().string("Timeline not found"));

  }

  @Test
  void getTimeline() throws Exception {
    String clientId = "u3";
    when(timelineService.getTimelineByClientId(clientId)).thenReturn(clientTimelines);

    RequestBuilder getRequest = get("/timeline/client/{clientId}", clientId)
        .header("Authorization", clientToken)
        .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(getRequest)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[*].timelineId").isArray())
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[0].timelineId").value(clientTimelines.get(0).getTimelineId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[1].timelineId").value(clientTimelines.get(1).getTimelineId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[0].clientId").value(clientTimelines.get(0).getClientId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[1].clientId").value(clientTimelines.get(1).getClientId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[0].mediaId").value(clientTimelines.get(0).getMediaId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[1].mediaId").value(clientTimelines.get(1).getMediaId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[0].status").value(clientTimelines.get(0).getStatus()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[1].status").value(clientTimelines.get(1).getStatus()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[0].comment").value(clientTimelines.get(0).getComment()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[1].comment").value(clientTimelines.get(1).getComment()));
  }

  @Test
  void getTimelineNotFound() throws Exception {
    String clientId = "u3";
    when(timelineService.getTimelineByClientId(clientId)).thenReturn(null);

    RequestBuilder getRequest = get("/timeline/client/{clientId}", clientId)
        .header("Authorization", clientToken)
        .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(getRequest)
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(content().string(""));
  }

  @Test
  void getTimelineByMediaId() throws Exception {
    String mediaId = "m1";
    when(timelineService.getTimelineByMediaId(mediaId)).thenReturn(mediaTimelines);

    RequestBuilder getRequest = get("/timeline/media/{mediaId}", mediaId)
        .header("Authorization", clientToken)
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
            jsonPath("$[0].clientId").value(mediaTimelines.get(0).getClientId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$[1].clientId").value(mediaTimelines.get(1).getClientId()))
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

    RequestBuilder getRequest = get("/timeline/media/{clientId}", mediaId)
        .header("Authorization", clientToken)
        .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(getRequest)
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(content().string(""));
  }

  @Test
  void getTimelineByClientIdAndMediaId() throws Exception {
    String mediaId = "41";
    String clientId = "u1";
    when(timelineService.getTimelineByClientIdAndMediaId(clientId, mediaId)).thenReturn(postedTimeline);

    RequestBuilder getRequest = get("/timeline/{clientId}/{mediaId}", clientId, mediaId)
        .header("Authorization", clientToken)
        .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(getRequest)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.timelineId").value(postedTimeline.getTimelineId()))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.clientId").value(postedTimeline.getClientId()))
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
  void getTimelineByClientMediaNotFound() throws Exception {
    String mediaId = "m3-23";
    String clientId = "u3-h4";
    when(timelineService.getTimelineByClientIdAndMediaId(clientId, mediaId)).thenReturn(null);

    RequestBuilder getRequest = get("/timeline/{clientId}/{mediaId}", clientId, mediaId)
        .header("Authorization", clientToken)
        .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(getRequest)
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(content().string(""));
  }

}