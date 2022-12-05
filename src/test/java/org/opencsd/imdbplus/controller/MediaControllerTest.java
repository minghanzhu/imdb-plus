package org.opencsd.imdbplus.controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.opencsd.imdbplus.entity.Media;
import org.opencsd.imdbplus.repository.MediaRepository;
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


@WebMvcTest(MediaController.class)
@ExtendWith(SpringExtension.class)
public class MediaControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MediaRepository mockMediaRepository;
    private Media mediaTest1;
    private Media mediaReturn1;

    private Media mediaTest3;
    private Media mediaTest4;
    private Media mediaTest5;

    @BeforeEach
    void setUp() {

        mediaTest1 = new Media("tt0000001", "The Shawshank redemption", "1994-09-23", "Drama");

        mediaReturn1 = new Media("tt0000001", "The Shawshank redemption", "1994-09-23", "Drama");

        mediaTest3 = new Media("tt0000002", "The Shawshank redemption", "1994-09-23", "Drama");

        mediaTest4 = new Media("tt0000001", "The Shawshank redemption", "1994-09-23", "Drama");

        mediaTest5 = new Media("tt0000003", "The Shawshank redemption1", "1994-09-23", "Drama");

    }

    @AfterEach
    void tearDown() {
        mediaTest1 = null;
        mediaReturn1 = null;
        mediaTest4 = null;
    }

    @Order(1)
    @Test
    void getMedia() throws Exception {
        when(mockMediaRepository.getEntity("tt0000001")).thenReturn(mediaReturn1);
        when(mockMediaRepository.getEntity("st0000001")).thenReturn(null);

        RequestBuilder requestBuilder1 = get("/api/v1/media/{id}", "tt0000001");
        RequestBuilder requestBuilder2 = get("/api/v1/media/{id}", "st0000001");

        mockMvc.perform(requestBuilder1)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.
                        jsonPath("$.title").value("The Shawshank redemption"))
                .andExpect(MockMvcResultMatchers.
                        jsonPath("$.genre").value("Drama"));

        mockMvc.perform(requestBuilder2)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string("Media not found"));
    }

    @Test
    void saveMedia() throws Exception {
        when(mockMediaRepository.saveMedia(mediaTest1)).thenReturn(mediaReturn1);

        RequestBuilder requestBuilder3 = post("/api/v1/media")
                .content(asJsonString(mediaTest1))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder3)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.
                        jsonPath("$.title").value("The Shawshank redemption"))
                .andExpect(MockMvcResultMatchers.
                        jsonPath("$.genre").value("Drama"));
    }

    @Test
    void deleteMedia() throws Exception {
        when(mockMediaRepository.delete("tt0000003")).thenReturn("Media deleted successfully");


        RequestBuilder requestBuilder1 = delete("/api/v1/media/{id}", "tt0000003");

        mockMvc.perform(requestBuilder1)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Media deleted successfully"));

    }

    @Test
    void updateMedia() throws Exception {
        when(mockMediaRepository.update("tt0000002", mediaTest5))
                .thenReturn("tt0000002");

        RequestBuilder requestBuilder1 = put("/api/v1/media/{id}", "tt0000002")
                .content(asJsonString(mediaTest5))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder1)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("tt0000002"));
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}


