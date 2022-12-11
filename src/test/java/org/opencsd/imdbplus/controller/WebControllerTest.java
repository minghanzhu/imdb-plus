package org.opencsd.imdbplus.controller;

import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(WebController.class)
class WebControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void homePage() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    void clientOptionPage() throws Exception {
        mockMvc.perform(get("/client-option"))
                .andExpect(status().isOk())
                .andExpect(view().name("client_option"));
    }

    @Test
    void timelineOptionPage() throws Exception {
        mockMvc.perform(get("/timeline-option"))
                .andExpect(status().isOk())
                .andExpect(view().name("timeline_option"));
    }

    @Test
    void clientGetPage() throws Exception {
        mockMvc.perform(get("/client-get"))
                .andExpect(status().isOk())
                .andExpect(view().name("client_get"));
    }

    @Test
    void clientPostPage() throws Exception {
        mockMvc.perform(get("/client-post"))
                .andExpect(status().isOk())
                .andExpect(view().name("client_post"));
    }


    @Test
    void clientDeletePage() throws Exception {
        mockMvc.perform(get("/client-delete"))
                .andExpect(status().isOk())
                .andExpect(view().name("client_delete"));
    }

    @Test
    void timelinePostPage() throws Exception {
        mockMvc.perform(get("/timeline-post"))
                .andExpect(status().isOk())
                .andExpect(view().name("timeline_post"));
    }

    @Test
    void timelineGETByClientIdPage() throws Exception {
        mockMvc.perform(get("/timeline-get-by-client-id"))
                .andExpect(status().isOk())
                .andExpect(view().name("timeline_get_client"));
    }

    @Test
    void timelineGETByMediaIdPage() throws Exception {
        mockMvc.perform(get("/timeline-get-by-media-id"))
                .andExpect(status().isOk())
                .andExpect(view().name("timeline_get_media"));
    }

    @Test
    void timelineGETByClientMediaIdPage() throws Exception {
        mockMvc.perform(get("/timeline-get-by-client-media-id"))
                .andExpect(status().isOk())
                .andExpect(view().name("timeline_get_search"));
    }

    @Test
    void timelineDeletePage() throws Exception {
        mockMvc.perform(get("/timeline-delete"))
                .andExpect(status().isOk())
                .andExpect(view().name("timeline_delete"));
    }

    @Test
    void ayalysisOptionPage() throws Exception {
        mockMvc.perform(get("/analysis-option"))
                .andExpect(status().isOk())
                .andExpect(view().name("analysis_option"));
    }

    @Test
    void profilePage() throws Exception {
        mockMvc.perform(get("/get-profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"));
    }
}