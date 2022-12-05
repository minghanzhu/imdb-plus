package org.opencsd.imdbplus.service;

import org.opencsd.imdbplus.entity.Media;
import org.opencsd.imdbplus.entity.Timeline;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import org.opencsd.imdbplus.repository.AnalysisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalysisService {

  private static final Logger serviceAnalysisLogger = LoggerFactory.getLogger(AnalysisService.class);

  @Autowired
  private AnalysisRepository analysisRepository;

  //Set my repository manager
  public void setAnalysisRepository(AnalysisRepository analysisRepository){
    this.analysisRepository = analysisRepository;
  }

  public String highestRatingHelper() {
    List<Timeline> allTimelines = analysisRepository.getAllTimelines();

    Map<String, List<Long>> ratingMap = new HashMap<>();
    // Walk down the timeline {id : [count, total]}
    for (Timeline curLine : allTimelines) {
      String id = curLine.getMediaId();
      if (curLine.getRating() != null && curLine.getStatus().equals("DONE")
          && curLine.getMediaId() != null)
        if (ratingMap.containsKey(id)) {
          List<Long> avgRating = ratingMap.get(id);
          avgRating.set(0, avgRating.get(0) + 1);
          long total = avgRating.get(1);
          total += curLine.getRating();
          avgRating.set(1, total);
          ratingMap.put(id, avgRating);
        } else {
          List<Long> avg = new ArrayList<>();
          avg.add(1L);
          avg.add((long) curLine.getRating());
          ratingMap.put(id, avg);
        }
    }

    Double highestAvgRating = Double.MIN_VALUE;
    String highestMediaId = null;
    //Find Avg
    for (String key : ratingMap.keySet()) {
      double avg = (double) ratingMap.get(key).get(1) / ratingMap.get(key).get(0);
      if (highestAvgRating < avg) {
        highestAvgRating = avg;
        highestMediaId = key;
      }
    }
    serviceAnalysisLogger.info("Highest Rated Media: {}", highestMediaId);
    return highestMediaId;
  }


  public Media getHighestRating() {
    String highestRatedMediaId = highestRatingHelper();
    serviceAnalysisLogger.info("Highest Rated Media: " + highestRatedMediaId);
    if(highestRatedMediaId != null){
      Media highestMedia = analysisRepository.getMedia(highestRatedMediaId);
      return highestMedia;
    }else {
      return null;
    }
  }


  private Map<String, Long> countMedia(String status) {
    List<Timeline> timelineList = analysisRepository.getTimelineListByByFilter(status);
    Map<String, Long> mediaCounter = new HashMap<>();
    // Compile a list of watched Media
    for (Timeline line : timelineList) {
      String mediaId = line.getMediaId();
        long count = mediaCounter.containsKey(mediaId) ? mediaCounter.get(mediaId) : 0;
        mediaCounter.put(mediaId, count + 1);
    }
    return mediaCounter;
  }

  // mediaId, status -> [DONE, IN_PROGRESS, WISHLIST]
  public Media getMediaInCategory(String category) {
    Map<String, Long> mediaCounter = countMedia(category);
    long curMostWatched = Long.MIN_VALUE;
    String mostWatchedMediaId = "";
    for (String mediaId : mediaCounter.keySet()){
      if (mediaCounter.get(mediaId) > curMostWatched) {
        curMostWatched = mediaCounter.get(mediaId);
        mostWatchedMediaId = mediaId;
      }
    }
    Media mostMedia = analysisRepository.getMedia(mostWatchedMediaId);
    serviceAnalysisLogger.info("retrieved {} in {} category", mostMedia, category);
    return mostMedia;
  }

  public List<Media> getTopTen(String category){
    PriorityQueue<MediaWrapper> topTenQueue = new PriorityQueue<>(10);
    Map<String, Long> validMedia = countMedia(category);

    //Build a top ten list using a priority queue
    for (String id : validMedia.keySet())
      if (topTenQueue.size() < 10) {
        MediaWrapper media = new MediaWrapper(id, validMedia.get(id).intValue());
        topTenQueue.offer(media);
      } else if (topTenQueue.peek().count < validMedia.get(id)) {
        topTenQueue.poll();
        MediaWrapper media = new MediaWrapper(id, validMedia.get(id).intValue());
        topTenQueue.offer(media);
      }
    // Return top ten list
    List<Media> topTenList = new ArrayList<>(10);
    while (!topTenQueue.isEmpty()) {
      String mediaId = topTenQueue.poll().mediaId;
      Media media = analysisRepository.getMedia(mediaId);
      topTenList.add(media);
    }
    serviceAnalysisLogger.info("retrieved top ten list in {} category", category);

    return topTenList;
  }

  public Map<String, Long> userPreference(String userId) {
    List<Timeline> timelineList = analysisRepository.getTimelineListByByFilter(userId);
    Map<String, Long> userPreferenceMap = new HashMap<>();
    for(Timeline line: timelineList){
          String mediaId = line.getMediaId();
          Media media = analysisRepository.getMedia(mediaId);
          String genre = media.getGenre();
          long count = userPreferenceMap.containsKey(genre) ? userPreferenceMap.get(genre) : 0;
          userPreferenceMap.put(genre, count + 1);
    }
    serviceAnalysisLogger.info("retrieved preference list for user {}", userId);

    return userPreferenceMap;
  }

  /**
   * Wrapper classes used for building top ten mediaId
   * */
  private class MediaWrapper implements Comparable<MediaWrapper> {
    String mediaId;
    int count;
    MediaWrapper(String mediaId, int count) {
      this.mediaId = mediaId;
      this.count = count;
    }

    @Override
    public int compareTo(MediaWrapper other) {
      return count - other.count;
    }
  }



}
