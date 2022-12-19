package org.opencsd.imdbplus.config;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


@Configuration
public class DynamoDBConfig {

  @Autowired
  private Environment env;

  @Bean
  public DynamoDBMapper dynamoDBMapper() {
    return new DynamoDBMapper(amazonDynamoDB());
  }

  private AmazonDynamoDB amazonDynamoDB() {
    return AmazonDynamoDBClientBuilder
        .standard()
        .withEndpointConfiguration(
            new AwsClientBuilder.EndpointConfiguration(
                "dynamodb.us-west-2.amazonaws.com",
                "us-west-2"
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
