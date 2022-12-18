package org.opencsd.imdbplus.controller;

import org.opencsd.imdbplus.entity.AccountSetting;
import org.opencsd.imdbplus.entity.Client;
import org.opencsd.imdbplus.repository.ClientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(ClientController.class)
@ExtendWith(SpringExtension.class)
class ClientControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private ClientRepository mockClientRepository;
  private Client clientTest1;
  private Client clientReturn1;
  private Client clientTest4;
  private Client clientTest5;
  private AccountSetting accountSetting;

  @BeforeEach
  void setUp() {
    accountSetting = new AccountSetting(true, true);

    clientTest1 = new Client("newClient1", "newClient1@gmail.com", accountSetting);

    clientReturn1 = new Client("newClient1", "newClient1@gmail.com", accountSetting);
    clientReturn1.setClientId("f7e4fb88-b59c-4c73-ad36-21fb5107646c");
    clientReturn1.setAccessToken("8ed4cea1-eee6-41bc-97f1-12a6095b51aa");

    clientTest4 = new Client("existClient", "existClient@gmail.com", accountSetting);

    clientTest5 = new Client("validTokenClient", "validTokenClient@gmail.com", accountSetting);
    clientTest5.setClientId("aaaf7e4fb88-b59c-4c73-ad36-21fb5107646c");
    clientTest5.setAccessToken("ccced4cea1-eee6-41bc-97f1-12a6095b51aa");
  }

  @AfterEach
  void tearDown() {
    clientTest1 = null;
    clientReturn1 = null;
    clientTest4 = null;
  }

  @Order(1)
  @Test
  void getClient() throws Exception {
    when(mockClientRepository.getClient("f7e4fb88-b59c-4c73-ad36-21fb5107646c", "8ed4cea1-eee6-41bc-97f1-12a6095b51aa")).thenReturn(
        clientReturn1);
    when(mockClientRepository.getClient("a7e4fb88-b59c-4c73-ad36-21fb5107646c", "ccced4cea1-eee6-41bc-97f1-12a6095b51aa")).thenReturn(null);

    RequestBuilder requestBuilder1 = get("/client/{id}", "f7e4fb88-b59c-4c73-ad36-21fb5107646c").header("Authorization","8ed4cea1-eee6-41bc-97f1-12a6095b51aa");
    RequestBuilder requestBuilder2 = get("/client/{id}", "a7e4fb88-b59c-4c73-ad36-21fb5107646c").header("Authorization","ccced4cea1-eee6-41bc-97f1-12a6095b51aa");

    mockMvc.perform(requestBuilder1)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.clientname").value("newClient1"))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.email").value("newClient1@gmail.com"))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.accountSetting.isPrivate").value(true))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.accountSetting.isAdult").value(true));

    mockMvc.perform(requestBuilder2)
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Order(2)
  @Test
  void saveClient() throws Exception {
    when(mockClientRepository.save(clientTest1)).thenReturn(clientReturn1);
    when(mockClientRepository.save(clientTest4)).thenReturn(null);

    RequestBuilder requestBuilder3 = post("/client")
        .content(asJsonString(clientTest1))
        .contentType(MediaType.APPLICATION_JSON);
    RequestBuilder requestBuilder4 = post("/client")
        .content(asJsonString(clientTest4))
        .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(requestBuilder3)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.clientname").value("newClient1"))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.email").value("newClient1@gmail.com"))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.accountSetting.isPrivate").value(true))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.accountSetting.isAdult").value(true))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.clientId").value("f7e4fb88-b59c-4c73-ad36-21fb5107646c"))
        .andExpect(MockMvcResultMatchers.
            jsonPath("$.accessToken").value("8ed4cea1-eee6-41bc-97f1-12a6095b51aa"));

    mockMvc.perform(requestBuilder4)
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Order(3)
  @Test
  void deleteClient() throws Exception {
    when(mockClientRepository.delete("vaf7e4fb88-b59c-4c73-ad36-21fb5107646c",
        "9ed4cea1-eee6-41bc-97f1-12a6095b51aa")).thenReturn("Client deleted successfully");
    when(mockClientRepository.delete("inf7e4fb88-b59c-4c73-ad36-21fb5107646c",
        "in9ed4cea1-eee6-41bc-97f1-12a6095b51aa")).thenReturn("Invalid access token");

    RequestBuilder requestBuilder1 = delete("/client/{id}", "vaf7e4fb88-b59c-4c73-ad36-21fb5107646c")
        .header("Authorization", "9ed4cea1-eee6-41bc-97f1-12a6095b51aa");
    RequestBuilder requestBuilder2 = delete("/client/{id}", "inf7e4fb88-b59c-4c73-ad36-21fb5107646c")
        .header("Authorization", "in9ed4cea1-eee6-41bc-97f1-12a6095b51aa");

    mockMvc.perform(requestBuilder1)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string("Client deleted successfully"));

    mockMvc.perform(requestBuilder2)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string("Invalid access token"));
  }

  @Order(4)
  @Test
  void updateClient() throws Exception {
    when(mockClientRepository.update("aaaf7e4fb88-b59c-4c73-ad36-21fb5107646c", clientTest5))
        .thenReturn("aaaf7e4fb88-b59c-4c73-ad36-21fb5107646c");

    RequestBuilder requestBuilder1 = put("/client/{id}", "aaaf7e4fb88-b59c-4c73-ad36-21fb5107646c")
        .content(asJsonString(clientTest5))
        .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(requestBuilder1)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string("aaaf7e4fb88-b59c-4c73-ad36-21fb5107646c"));
  }

  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}