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
import org.opencsd.imdbplus.entity.Media;
import org.opencsd.imdbplus.entity.Timeline;
import org.opencsd.imdbplus.entity.User;
import org.opencsd.imdbplus.repository.MediaRepository;
import org.opencsd.imdbplus.repository.TimelineRepository;
import org.opencsd.imdbplus.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class TimelineServiceTest {

  @MockBean
  private TimelineRepository timelineRepository;
  @MockBean
  private UserRepository userRepository;
  @MockBean
  private MediaRepository mediaRepository;

  @Autowired
  private TimelineService timelineService;

  /* Setting up my objects under test */
  private List<Timeline> userTimelines;
  private List<Timeline> mediaTimelines;
  private List<Timeline> allTimelines;
  private List<Date> dateList;
  private Media testMedia;
  private Media updatedMedia;
  private User testUser;


  @BeforeEach
  void setUp() {

    /**
     *  set up my mock repositories, isolate from the database
     * */
    timelineService = new TimelineService();

    timelineRepository = mock(TimelineRepository.class);
    userRepository = mock(UserRepository.class);
    mediaRepository = mock(MediaRepository.class);

    timelineService.setTimelineRepository(timelineRepository);
    timelineService.setUserRepository(userRepository);
    timelineService.setMediaRepository(mediaRepository);


    Timeline t1 = new Timeline("t1-u1-m1", "u1", "m1", new Date(), "DONE", 5, "It was great");
    Timeline t2 = new Timeline("t2-u1-m1", "u1", "m4", new Date(), "DONE", 1, "It was terrible");
    Timeline t3 = new Timeline("t3-u3-m3", "u3", "m3", new Date(), "PROGRESS", 3, "Still in progress.");
    Timeline t4 = new Timeline("t4-u2-m1", "u2", "m1", new Date(), "WISHLIST", 5, "I head this was great.");
    Timeline t5 = new Timeline("t5-u1-m4", "u1", "m5", new Date(), "WISHLIST", 0, "I REALLY WANT TO SEE IT.");

    testMedia = new Media("m1", "movie", "2012-07-17", "Action");
    updatedMedia = new Media("m5", "Unreleased Movie", "2023-07-17", "Action");

    userTimelines = Arrays.asList(t1, t5);
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
    testUser = new User("u1", token, "newUser1", "newUser1@gmail.com", accountSetting);
    when(userRepository.getUser("u1")).thenReturn(testUser);
    when(mediaRepository.getEntity("m1")).thenReturn(testMedia);

  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void save() {
    Timeline testLine = new Timeline("t1-u1-m1", "u1", "m1", dateList.get(0), "DONE", 5, "It was great");
    String userToken = "8ed4cea1-eee6-41bc-97f1-12a6095b51aa";

    when(timelineRepository.save(testLine)).thenReturn(testLine);

    Timeline result =  timelineService.save(testLine, userToken);
    assertEquals(testLine, result);
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
    String userToken = "8ed4cea1-eee6-41bc-97f1-12a6095b51aa";
    String results = timelineService.delete("t2-u1-m1", userToken);

    assertEquals("Timeline deleted successfully", results);
  }

  @Test
  void deleteFailed() {
    when(timelineRepository.getTimeline("t2-u1-m1")).thenReturn(allTimelines.get(1));
    String userToken = "wrong-user-token";
    String results = timelineService.delete("t2-u1-m1", userToken);

    assertEquals("Invalid access token", results);
  }
  @Test
  void deleteTimelineNotExist() {
    when(timelineRepository.getTimeline("t2-u1-m1")).thenReturn(null);
    String userToken = "8ed4cea1-eee6-41bc-97f1-12a6095b51aa";
    String results = timelineService.delete("t2-u1-m1", userToken);

    assertEquals("Timeline does not exist", results);
  }


  @Test
  void update() {
    Timeline testLine = new Timeline("t5-u1-m4", "u1", "m5", new Date(), "DONE", 3, "It was underwhelming.");
    String userToken = "8ed4cea1-eee6-41bc-97f1-12a6095b51aa";

    when(timelineRepository.getTimeline("t5-u1-m4")).thenReturn(allTimelines.get(4));

    Timeline updatedTimeline = timelineService.update(testLine, userToken);

    assertEquals(testLine, updatedTimeline);


  }

  @Test
  void getTimelineByUserId() {
    when(timelineRepository.getTimelineByUserId("u1")).thenReturn(userTimelines);
    List<Timeline> lines = timelineService.getTimelineByUserId("u1");
    assertEquals(userTimelines, lines);
    assertEquals(userTimelines.size(), lines.size());
  }

  @Test
  void getWrongTimelineByUserId() {
    when(timelineRepository.getTimelineByUserId("u1")).thenReturn(userTimelines);
    List<Timeline> lines = timelineService.getTimelineByUserId("u1");
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
  void getTimelineByUserIdAndMediaId() {
    when(timelineRepository.getTimelineByUserIdAndMediaId("u1", "m5")).thenReturn(allTimelines.get(4));
    Timeline result = timelineService.getTimelineByUserIdAndMediaId("u1", "m5");
    assertEquals(allTimelines.get(4), result);
  }
}