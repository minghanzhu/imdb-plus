package org.opencsd.imdbplus.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opencsd.imdbplus.entity.AccountSetting;
import org.opencsd.imdbplus.entity.Client;
import org.opencsd.imdbplus.entity.Media;
import org.opencsd.imdbplus.entity.Timeline;
import org.opencsd.imdbplus.repository.MediaRepository;
import org.opencsd.imdbplus.repository.TimelineRepository;
import org.opencsd.imdbplus.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class TimelineServiceTest {

  @MockBean
  private TimelineRepository timelineRepository;
  @MockBean
  private ClientRepository clientRepository;
  @MockBean
  private MediaRepository mediaRepository;

  @Autowired
  private TimelineService timelineService;

  /* Setting up my objects under test */
  private List<Timeline> clientTimelines;
  private List<Timeline> mediaTimelines;
  private List<Timeline> allTimelines;
  private List<Date> dateList;
  private Media testMedia;
  private Media updatedMedia;
  private Client testClient;


  @BeforeEach
  void setUp() {

    /**
     *  set up my mock repositories, isolate from the database
     * */
    timelineService = new TimelineService();

    timelineRepository = mock(TimelineRepository.class);
    clientRepository = mock(ClientRepository.class);
    mediaRepository = mock(MediaRepository.class);

    timelineService.setTimelineRepository(timelineRepository);
    timelineService.setClientRepository(clientRepository);
    timelineService.setMediaRepository(mediaRepository);


    Timeline t1 = new Timeline("t1-u1-m1", "u1", "m1", new Date(), new Date(), "DONE", 5, "It was great");
    Timeline t2 = new Timeline("t2-u1-m1", "u1", "m4", new Date(), new Date(), "DONE", 1, "It was terrible");
    Timeline t3 = new Timeline("t3-u3-m3", "u3", "m3", new Date(), new Date(), "PROGRESS", 3, "Still in progress.");
    Timeline t4 = new Timeline("t4-u2-m1", "u2", "m1", new Date(), new Date(), "WISHLIST", 5, "I head this was great.");
    Timeline t5 = new Timeline("t5-u1-m4", "u1", "m5", new Date(), new Date(), "WISHLIST", 0, "I REALLY WANT TO SEE IT.");

    testMedia = new Media("m1", "movie", "2012-07-17", "Action");
    updatedMedia = new Media("m5", "Unreleased Movie", "2023-07-17", "Action");

    clientTimelines = Arrays.asList(t1, t5);
    mediaTimelines = Arrays.asList(t1, t4);
    allTimelines = Arrays.asList(t1, t2, t3, t4, t5);
    dateList = Arrays.asList(
        t1.getCreationTime(),
        t2.getCreationTime(),
        t1.getCreationTime(),
        t1.getCreationTime(),
        t1.getCreationTime());

    String token = "8ed4cea1-eee6-41bc-97f1-12a6095b51aa";
    AccountSetting accountSetting = new AccountSetting(true, true);
    testClient = new Client("u1", token, "newClient1", "newClient1@gmail.com", accountSetting);
    when(clientRepository.getClient("u1", "8ed4cea1-eee6-41bc-97f1-12a6095b51aa")).thenReturn(testClient);
    when(mediaRepository.getEntity("m1")).thenReturn(testMedia);

  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void save() {
    Timeline testLine = new Timeline("t1-u1-m1", "u1", "m1", dateList.get(0),  dateList.get(0), "DONE", 5, "It was great");
    String clientToken = "8ed4cea1-eee6-41bc-97f1-12a6095b51aa";

    when(timelineRepository.save(testLine)).thenReturn(testLine);

    Timeline result =  timelineService.save(testLine, clientToken);
    assertEquals(testLine, result);
  }
  @Test
  void saveFailed() {
    Timeline testLine = new Timeline("t1-u1-m1", "u1", "m1", dateList.get(0),  dateList.get(0), "DONE", 5, "It was great");
    String clientToken = "8ed4cea1-eee6-41bc-97f1-12a6095b51aa";

    when(timelineRepository.save(testLine)).thenReturn(null);

    Timeline result =  timelineService.save(testLine, clientToken);
    assertEquals(null, result);
  }

  @Test
  void saveBadAccessToken() {
    Timeline testLine = new Timeline("t1-u1-m1", "u1", "m1", dateList.get(0),  dateList.get(0), "DONE", 5, "It was great");
    String clientToken = "8ed4cea1-eee6-41bc-97f1-12a6095b51aa";

    when(timelineRepository.save(testLine)).thenReturn(null);

    Timeline result =  timelineService.save(testLine, null);
    assertEquals(null, result);
  }

  @Test
  void saveBadAccessTimeline() {
    Timeline testLine = new Timeline("t1-u1-m1", "u1", "m1", dateList.get(0),  dateList.get(0), "DONE", 5, "It was great");
    String clientToken = "8ed4cea1-eee6-41bc-97f1-12a6095b51aa";

    when(timelineRepository.save(null)).thenReturn(null);

    Timeline result =  timelineService.save(testLine, clientToken);
    assertEquals(null, result);
  }

  @Test
  void getTimeline() {
    when(timelineRepository.getTimeline("t3-u3-m3")).thenReturn(allTimelines.get(2));

    Timeline result = timelineService.getTimeline("t3-u3-m3");
    assertEquals(result, allTimelines.get(2));
  }

  @Test
  void getNoneExistentTimeline(){
    Timeline result = timelineService.getTimeline("fake-id");
    assertEquals(result, null);
  }


  @Test
  void deleteSuccess() {
    when(timelineRepository.getTimeline("t2-u1-m1")).thenReturn(allTimelines.get(1));
    String clientToken = "8ed4cea1-eee6-41bc-97f1-12a6095b51aa";
    String results = timelineService.delete("t2-u1-m1", clientToken);

    assertEquals("Timeline deleted successfully", results);
  }

  @Test
  void deleteFailed() {
    when(timelineRepository.getTimeline("t2-u1-m1")).thenReturn(allTimelines.get(1));
    String clientToken = "wrong-client-token";
    String results = timelineService.delete("t2-u1-m1", clientToken);

    assertEquals("Invalid access token", results);
  }
  @Test
  void deleteTimelineNotExist() {
    when(timelineRepository.getTimeline("t2-u1-m1")).thenReturn(null);
    String clientToken = "8ed4cea1-eee6-41bc-97f1-12a6095b51aa";
    String results = timelineService.delete("t2-u1-m1", clientToken);

    assertEquals("Timeline does not exist", results);
  }


  @Test
  void update() {
    Timeline testLine = new Timeline("t5-u1-m4", "u1", "m5", new Date(), new Date(), "DONE", 3, "It was underwhelming.");
    String clientToken = "8ed4cea1-eee6-41bc-97f1-12a6095b51aa";

    when(timelineRepository.getTimeline("t5-u1-m4")).thenReturn(allTimelines.get(4));

    Timeline updatedTimeline = timelineService.update(testLine, clientToken);

    assertEquals(testLine, updatedTimeline);
  }

  @Test
  void getTimelineByClientId() {
    when(timelineRepository.getTimelineByClientId("u1")).thenReturn(clientTimelines);
    List<Timeline> lines = timelineService.getTimelineByClientId("u1");
    assertEquals(clientTimelines, lines);
    assertEquals(clientTimelines.size(), lines.size());
  }

  @Test
  void getWrongTimelineByClientId() {
    when(timelineRepository.getTimelineByClientId("u1")).thenReturn(clientTimelines);
    List<Timeline> lines = timelineService.getTimelineByClientId("u1");
    assertNotEquals(mediaTimelines, lines);

  }

  @Test
  void getTimelineByMediaId() {
    when(timelineRepository.getTimelineByMediaId("m1")).thenReturn(mediaTimelines);
    List<Timeline> lines = timelineService.getTimelineByMediaId("m1");
    assertEquals(mediaTimelines, lines);
    assertEquals(mediaTimelines.size(), lines.size());
  }

  @Test
  void getTimelineByClientIdAndMediaId() {
    when(timelineRepository.getTimelineByClientIdAndMediaId("u1", "m5")).thenReturn(allTimelines.get(4));
    Timeline result = timelineService.getTimelineByClientIdAndMediaId("u1", "m5");
    assertEquals(allTimelines.get(4), result);
  }
}