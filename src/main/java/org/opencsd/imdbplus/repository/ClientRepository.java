package org.opencsd.imdbplus.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import org.opencsd.imdbplus.entity.Client;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ClientRepository {
  Logger clientLogger = LoggerFactory.getLogger(ClientRepository.class);

  @Autowired
  private DynamoDBMapper dynamoDBMapper;

  // Add new client to the database if the clientname does not exist
  public Client save(Client client) {
    // scan the table to see if the clientname exists
    HashMap<String, AttributeValue> eav = new HashMap<>();
    eav.put(":v1", new AttributeValue().withS(client.getClientname()));
    DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
        .withFilterExpression("clientname = :v1")
        .withExpressionAttributeValues(eav);
    // if the clientname does not exist, add the client to the database
    List<Client> replies = dynamoDBMapper.scan(Client.class, scanExpression);
    if (replies.isEmpty()) {
      dynamoDBMapper.save(client);
      return client;
    } else {
      return null;
    }
  }

  public Client getClient(String clientId) {
    return dynamoDBMapper.load(Client.class, clientId);
  }

  public String delete(String clientId, String accessToken) {
    Client client = getClient(clientId);
    if (client.getAccessToken().equals(accessToken)) {
      dynamoDBMapper.delete(client);
      return "Client deleted successfully";
    } else {
      return "Invalid access token";
    }
  }

  public String update(String clientId, Client client) {
    dynamoDBMapper.save(client, new DynamoDBSaveExpression()
        .withExpectedEntry("clientId",
            new ExpectedAttributeValue(
                new AttributeValue().withS(clientId)
            )));
    return clientId;
  }
}