package org.opencsd.imdbplus.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opencsd.imdbplus.entity.Timeline;
import org.opencsd.imdbplus.repository.TimelineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class TimelineServiceTest {

  @MockBean
  private TimelineRepository timelineRepository;

  private TimelineService timelineService;


  @BeforeEach
  void setUp() {
    Timeline t1 = new Timeline("t1-u1-m1", "u1", "m1", new Date(), "DONE", 5, "It was great");
    Timeline t2 = new Timeline("t2-u1-m1", "u1", "m4", new Date(), "DONE", 1, "It was terrible");
    Timeline t3 = new Timeline("t3-u3-m3", "u3", "m3", new Date(), "PROGRESS", 3, "Still in progress.");
    Timeline t4 = new Timeline("t4-u2-m1", "u2", "m1", new Date(), "WISHLIST", 5, "I head this was great.");
    Timeline t5 = new Timeline("t5-u3-m4", "u1", "m5", new Date(), "DONE", 5, "It was great");
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void save() {
  }

  @Test
  void getTimeline() {
  }

  @Test
  void delete() {
  }

  @Test
  void update() {
  }

  @Test
  void getTimelineByUserId() {
  }

  @Test
  void getTimelineByMediaId() {
  }

  @Test
  void getTimelineByUserIdAndMediaId() {
  }
}