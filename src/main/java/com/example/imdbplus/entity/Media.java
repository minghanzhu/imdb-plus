package com.example.imdbplus.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

@DynamoDBTable(tableName = "media")
public class Media {

    @DynamoDBHashKey
    private String mediaId; // tt0000001 (IMDB ID)
    @DynamoDBAttribute(attributeName = "title")
    private String title; // The Shawshank Redemption
    @DynamoDBAttribute(attributeName = "release_date")
    private String release_date; // 1994-09-23
    @DynamoDBAttribute(attributeName = "genre")
    private String genre; // Drama

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}