package com.example.imdbplus.conroller;

import com.example.imdbplus.repository.MediaRepository;
import com.example.imdbplus.repository.UserRepository;
import com.example.imdbplus.entity.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


public class MediaController {
    @Autowired
    private MediaRepository mediaRepository;

    //    @DynamoDBAttribute
//    private String mediaId; // tt0000001 (IMDB ID)
//    @DynamoDBAttribute
//    private String title; // The Shawshank Redemption
//    @DynamoDBAttribute
//    private String release_date; // 1994-09-23

//    private String genre;
    @GetMapping("/media/{id}")
    public Media getMedia(@PathVariable(value = "id") String mediaId){
        return null;
    }

    @PutMapping("/media")
    public ResponseEntity postMedia(@RequestBody Media entry){
        return null;
    }

}