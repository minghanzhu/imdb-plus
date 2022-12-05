package org.opencsd.imdbplus.controller;

import org.opencsd.imdbplus.entity.Media;
import org.opencsd.imdbplus.repository.MediaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
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
  public ResponseEntity getMedia(@PathVariable(value = "id") String mediaId) {
    Media response = mediaRepository.getEntity(mediaId);
    if (response == null) {
      return ResponseEntity.status(404).body("Media not found");
    } else {
      return ResponseEntity.ok(response);
    }
  }


  @PostMapping("/api/v1/media")
  public ResponseEntity saveMedia(@RequestBody Media item) {
    Media response = mediaRepository.saveMedia(item);
    if (response == null) {
      return ResponseEntity.status(400).body("Media already exists");
    } else {
      return ResponseEntity.ok(response);
    }
  }

  @PutMapping("api/v1/media/{id}")
  public String updateMedia(@PathVariable(value = "id") String mediaId, @RequestBody Media item) {
    return mediaRepository.update(mediaId, item);
  }

  @DeleteMapping("api/v1/media/{id}")
  public String deleteMedia(@PathVariable("id") String mediaId) {
    return mediaRepository.delete(mediaId);
  }

}