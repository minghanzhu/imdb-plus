package org.opencsd.imdbplus.service;

import java.util.ArrayList;
import java.util.List;
import org.opencsd.imdbplus.entity.Client;
import org.opencsd.imdbplus.entity.Media;
import org.opencsd.imdbplus.entity.Timeline;
import org.opencsd.imdbplus.repository.MediaRepository;
import org.opencsd.imdbplus.repository.TimelineRepository;
import org.opencsd.imdbplus.repository.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TimelineService {
  Logger serviceLogger = LoggerFactory.getLogger(TimelineService.class);

  @Autowired
  TimelineRepository timelineRepository;
  @Autowired
  ClientRepository clientRepository;
  @Autowired
  MediaRepository mediaRepository;
  public void setTimelineRepository(TimelineRepository timelineRepository){
    this.timelineRepository = timelineRepository;
  }
  public void setClientRepository(ClientRepository clientRepository){
    this.clientRepository = clientRepository;
  }
  public void setMediaRepository(MediaRepository mediaRepository){
    this.mediaRepository = mediaRepository;
  }

  /**
   *  Checks if client has auth token to post a timeline, then create a timeline object with a timestamp.
   */
  public Timeline save(Timeline timeline, String accessToken) {
    // Check if the client exists and the access token is valid
    String clientId = timeline.getClientId();
    String mediaId = timeline.getMediaId();
    Client client = clientRepository.getClient(clientId);
    Media media = mediaRepository.getEntity(mediaId);
    serviceLogger.debug("client : {} \n media: {}", client, media);
    if (client.getAccessToken().equals(accessToken)) {
      String id = clientId+"-"+mediaId;
      timeline.setTimelineId(id);
      timeline.getCreationTime();
      timeline.getLastUpdate();
      Timeline saveTimeline = timelineRepository.save(timeline);
      serviceLogger.info("{} created {}",clientId, timeline.getTimelineId() );
      if(saveTimeline != null){
        serviceLogger.info(saveTimeline.toString());
        return saveTimeline;
      }
    } else {
      return null;
    }
    return null;
  }

  /**
   *  Given a timeline id, returns a timeline instance, otherwise null
   */
  public Timeline getTimeline(String timelineId) {
    try {
      Timeline timeline = timelineRepository.getTimeline(timelineId);
      serviceLogger.info("{} retrieved {}", timeline.getClientId(), timeline.getTimelineId());
      return timeline;
    }catch (Exception e){
      serviceLogger.warn(e.toString());
      return null;
    }
  }

  /**
   *  Given a valid timeline id and correct access token, deletes a timeline from the database
   */
  public String delete(String timelineId, String accessToken) {
    // Check if the client exists and the access token is valid
    Timeline timeline = getTimeline(timelineId);
    if (timeline != null ){
      Client client = clientRepository.getClient(timeline.getClientId());
      if (!client.getAccessToken().equals(accessToken)) {
        return "Invalid access token";
      }
      serviceLogger.info("{} deleted by {}", timelineId, client.getClientname());
      timelineRepository.delete(timeline.getClientId()+"-"+timeline.getMediaId());
      return "Timeline deleted successfully";
    }
    return "Timeline does not exist";

  }
  /**
   *  Given a valid timeline object that also exists in the repository, updates the comment, status, or rating
   */
  public Timeline update(Timeline timeline, String accessToken) {
    Timeline oldTimeline = timelineRepository.getTimeline(timeline.getTimelineId());

    if(oldTimeline != null){
      Client curClient = clientRepository.getClient(timeline.getClientId());
      if(curClient.getAccessToken().equals(accessToken)) {
        if (oldTimeline.getClientId().equals(timeline.getClientId()) &&
            oldTimeline.getMediaId().equals(timeline.getMediaId())){
          if (timeline.getRating() >= 0 && timeline.getRating() <= 5)
            timelineRepository.save(timeline);
            serviceLogger.info(timeline.getTimelineId() + " updated by  " + timeline.getClientId());
          }
        }else{
          serviceLogger.warn("Wrong Access Token");
          return null;
      }
    }
    return timeline;
  }

  /**
   *  Given a valid client id that also exists in the repository, returns a list of client timelines.
   */
  public List<Timeline> getTimelineByClientId(String clientId) {
    // scan the timeline table to get all timelines of the client
    Client curClient = clientRepository.getClient(clientId);
    if(curClient != null){
      serviceLogger.info("{}'s timelines retrieved", clientId);
      return  timelineRepository.getTimelineByClientId(curClient.getClientId());
    }else{
      return new ArrayList<>();
    }
  }

  /**
   *  Given a valid media id that also exists in the repository, returns a list of timelines.
   */
  public List<Timeline> getTimelineByMediaId(String mediaId) {
    // scan the timeline table to get all timelines of the media
//    Media curMedia = mediaRepository.getEntity(mediaId);
//    if(curMedia != null){
//      serviceLogger.info("{}'s timelines retrieved", mediaId);
//      return  timelineRepository.getTimelineByMediaId(mediaId);
//    }else{
//      return new ArrayList<>();
//    }
    serviceLogger.info("{}'s timelines retrieved", mediaId);
    return  timelineRepository.getTimelineByMediaId(mediaId);
  }

  /**
   *  Given a valid media id and client id that also exists in the repository, returns a list of timelines.
   */
  public Timeline getTimelineByClientIdAndMediaId(String clientId, String mediaId) {
    Timeline timeline = timelineRepository.getTimelineByClientIdAndMediaId(clientId, mediaId);
    serviceLogger.info("timeline retrieved by {} & {}", clientId, mediaId);
    return  timeline;
  }

}
