package com.example.imdbplus.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@DynamoDBTable(tableName = "timeline")
public class Timeline {
    @DynamoDBAttribute
    private String user_id; // 0b5dedb5-4b04-4c36-adda-e748e60bc20a (UUID)
    @DynamoDBAttribute
    private String epoch_time; // 1600000000
    @DynamoDBAttribute
    private String media_id; // tt0000001 (IMDB ID)
    @DynamoDBAttribute
    private String action; // DONE, IN_PROGRESS, PLAN_
    @DynamoDBAttribute
    private String rating; // 1-5 (optional, only for DONE)
    @DynamoDBAttribute
    private String comment; // (optional)
}