package org.opencsd.imdbplus.conroller;

import org.opencsd.imdbplus.entity.Media;
import org.opencsd.imdbplus.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MediaController {

  @Autowired
  private MediaRepository mediaRepository;

  @GetMapping("/api/v1/media/{id}")
  public Media getMedia(@PathVariable(value = "id") String mediaId) {
    return mediaRepository.getEntity(mediaId);
  }

  @GetMapping("/api/v1/media")
  public ResponseEntity getAllMedia() {
    return mediaRepository.getAllEntities();
  }

  @PostMapping("/api/v1/media")
  public ResponseEntity saveUser(@RequestBody Media item) {
    return mediaRepository.saveMedia(item);
  }

  @PutMapping("api/v1/media/{id}")
  public String updateMedia(@PathVariable(value = "id") String mediaId, Media item) {
    return mediaRepository.update(mediaId, item);
  }

  @DeleteMapping("api/v1/media/{id}")
  public String deleteMedia(@PathVariable("id") String mediaId) {
    return mediaRepository.delete(mediaId);
  }

}