package org.opencsd.imdbplus.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TimelineTest {

  private Timeline[] timelines;
  private Date[] postTimes;


  @BeforeEach
  void setUp(){
    Timeline t1 = new Timeline("t1-u1-m1", "u1", "m1", new Date(), "DONE", 5, "It was great");
    Timeline t2 = new Timeline("t2-u1-m1", "u1", "m4", new Date(), "DONE", 1, "It was terrible");
    Timeline t3 = new Timeline("t3-u3-m3", "u3", "m3", new Date(), "PROGRESS", 3, "Still in progress.");
    Timeline t4 = new Timeline("t4-u2-m1", "u2", "m1", new Date(), "WISHLIST", 5, "I head this was great.");
    Timeline t5 = new Timeline("t5-u3-m4", "u1", "m5", new Date(), "DONE", 5, "It was great");

    Timeline[] curTimelines = {t1, t2, t3, t4, t5};
    timelines = curTimelines;

    Date[] curDates = {
        t1.getCreationTime(),
        t2.getCreationTime(),
        t3.getCreationTime(),
        t4.getCreationTime(),
        t5.getCreationTime(),
    };
    postTimes = curDates;

  }

  @AfterEach
  void tearDown(){

  }

  @Test
  void getTimelineId() {

    String[] idList = {"t1-u1-m1", "t2-u1-m1", "t3-u3-m3", "t4-u2-m1", "t5-u3-m4"};

    for(int i=0; i<timelines.length; i++){
      String result = timelines[i].getTimelineId();
      String expectedId = idList[i];
      assertEquals(result, expectedId);
    }

  }

  @Test
  void getWrongTimelineId() {

    String wrongId = "r1-b1-c1";

    for(int i=0; i<timelines.length; i++){
      String timelineId = timelines[i].getTimelineId();
      assertFalse(timelineId.equals(wrongId));
    }

  }

  @Test
  void getUserId() {
    String[] userId = {"u1", "u1", "u3", "u2", "u1"};
    for(int i=0; i<timelines.length; i++){
      String result = timelines[i].getUserId();
      String expectedId = userId[i];
      assertEquals(result, expectedId);
    }
  }

  @Test
  void getMediaId() {
    String[] mediaId = {"m1", "m4", "m3", "m1", "m5"};
    for(int i=0; i<timelines.length; i++){
      String result = timelines[i].getMediaId();
      String expectedId = mediaId[i];
      assertEquals(result, expectedId);
    }
  }

  @Test
  void getCreationTime() {
    DateTime now = new DateTime();
    Date timelineCreation =   timelines[0].getCreationTime();


    for(int i=0; i<timelines.length; i++){
      Date result = timelines[i].getCreationTime();
      Date expectedTime = postTimes[i];
      Date unExpectedTime = new Date();
      assertNotEquals(result, unExpectedTime);
      assertEquals(result, expectedTime);
    }
  }

  @Test
  void getStatus() {
    String[] status = {"DONE", "DONE", "PROGRESS", "WISHLIST", "DONE"};
    for(int i=0; i<timelines.length; i++){
      String result = timelines[i].getStatus();
      String stat = status[i];
      assertEquals(result, stat);
    }

    assertNotEquals(timelines[0].getStatus(), "PROGRESS");

  }

  @Test
  void getRating() {

    for(int i=0; i < timelines.length; i++){
      int rating = timelines[i].getRating();
      assertTrue(rating >= 0);
      assertTrue(rating <= 5);
    }

  }

  @Test
  void getComment() {
    for(int i=0; i<timelines.length; i++){
      String comment = timelines[i].getComment();
      assertTrue(!comment.isEmpty());
    }
  }

  @Test
  void testEquals() {

    Timeline t1 = new Timeline("t1-u1-m1", "u1", "m1", postTimes[0], "DONE", 5, "It was great");
    Timeline t2 = new Timeline("t2-u1-m1", "u1", "m4", postTimes[1], "DONE", 1, "It was terrible");
    Timeline t3 = new Timeline("t3-u3-m3", "u3", "m3", postTimes[2], "PROGRESS", 3, "Still in progress.");
    Timeline t4 = new Timeline("t4-u2-m1", "u2", "m1", postTimes[3], "WISHLIST", 5, "I head this was great.");
    Timeline t5 = new Timeline("t5-u3-m4", "u1", "m5", postTimes[4], "DONE", 5, "It was great");
    Timeline[] timelinesInstances = {t1, t2, t3, t4, t5};

    for(int i = 0; i< timelinesInstances.length; i++){
      Timeline current_instance = timelinesInstances[i];
      Timeline existing_instance = timelines[i];
      assertTrue(current_instance.equals(existing_instance));
    }

  }

  @Test
  void testHashcode(){
    Timeline t1 = new Timeline("t1-u1-m1", "u1", "m1", postTimes[0], "DONE", 5, "It was great");
    Timeline t2 = new Timeline("t2-u1-m1", "u1", "m4", postTimes[1], "DONE", 1, "It was terrible");
    Timeline t3 = new Timeline("t3-u3-m3", "u3", "m3", postTimes[2], "PROGRESS", 3, "Still in progress.");
    Timeline t4 = new Timeline("t4-u2-m1", "u2", "m1", postTimes[3], "WISHLIST", 5, "I head this was great.");
    Timeline t5 = new Timeline("t5-u3-m4", "u1", "m5", postTimes[4], "DONE", 5, "It was great");
    Timeline[] timelinesInstances = {t1, t2, t3, t4, t5};

    for(int i = 0; i< timelinesInstances.length; i++){
      int curHash = timelinesInstances[i].hashCode();
      int existingHash = timelines[i].hashCode();
      assertTrue(curHash == existingHash);
    }
  }

  @Test
  void testToString(){
    Timeline t1 = new Timeline("t1-u1-m1", "u1", "m1", postTimes[0], "DONE", 5, "It was great");
    assertEquals(t1.toString(), timelines[0].toString());
  }


}