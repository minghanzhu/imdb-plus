package com.example.imdbplus.config;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DynamoDBConfig {

  @Bean
  public static DynamoDBMapper dynamoDBMapper() {
    return new DynamoDBMapper(DynamoDBConfig.amazonDynamoDB());
  }

  private static AmazonDynamoDB amazonDynamoDB() {
    return AmazonDynamoDBClientBuilder
        .standard()
        .withEndpointConfiguration(
            new AwsClientBuilder.EndpointConfiguration(
                "dynamodb.us-east-1.amazonaws.com",
                "us-east-1"
            )
        )
        .withCredentials(
            new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(
                    "",
                    ""
                )
            )
        )
        .build();
  }
}
