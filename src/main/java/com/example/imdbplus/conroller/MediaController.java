package com.example.imdbplus.conroller;

import com.example.imdbplus.repository.MediaRepository;
import com.example.imdbplus.entity.Media;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MediaController {
    @Autowired
    private MediaRepository mediaRepository;

    @GetMapping("api/v1/media/{id}")
    public Media getMedia(@PathVariable(value = "id") String mediaId){
        return mediaRepository.getEntity(mediaId);
    }
    @PostMapping("api/v1/media")
    public ResponseEntity saveUser(@RequestBody Media item) {
        return mediaRepository.saveMedia(item);
    }

    @PutMapping("api/v1/media/{id}")
    public String updateMedia(@PathVariable(value = "id") String mediaId, Media item){
        return mediaRepository.update(mediaId, item);
    }

    @DeleteMapping("api/v1/media/{id}")
    public String deleteMedia(@PathVariable("id") String mediaId) {
        return mediaRepository.delete(mediaId);
    }

}