package org.opencsd.imdbplus.controller;

import org.opencsd.imdbplus.entity.Client;
import org.opencsd.imdbplus.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientController {

  @Autowired
  private ClientRepository clientRepository;

  // Add new client to the database
  @PostMapping("/client")
  public ResponseEntity<Client> save(@RequestBody Client client) {
    Client response = clientRepository.save(client);
    if (response == null) {
      return ResponseEntity.badRequest().body(null);
    } else {
      return ResponseEntity.ok(response);
    }
  }

  // Get client by clientId
  @GetMapping("/client/{id}")
  public ResponseEntity<Client> getClient(@PathVariable("id") String clientId,
      @RequestHeader("Authorization") String accessToken) {
    Client response = clientRepository.getClient(clientId, accessToken);
    if (response == null) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok(response);
    }
  }

  // Delete client by clientId
  @DeleteMapping("/client/{id}")
  public ResponseEntity<String> deleteClient(@PathVariable("id") String clientId,
      @RequestHeader("Authorization") String accessToken) {
    String response = clientRepository.delete(clientId, accessToken);
    return ResponseEntity.ok(response);
  }

  // Update client by clientId
  @PutMapping("/client/{id}")
  public ResponseEntity<Client> updateClient(@PathVariable("id") String clientId,
      @RequestHeader("Authorization") String accessToken, @RequestBody Client client) {
    client.setClientId(clientId);
    client.setAccessToken(accessToken);
    Client response = clientRepository.update(clientId, client, accessToken);
    return ResponseEntity.ok(response);
  }
}