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
    void userOptionPage() throws Exception {
        mockMvc.perform(get("/user-option"))
                .andExpect(status().isOk())
                .andExpect(view().name("user_option"));
    }

    @Test
    void timelineOptionPage() throws Exception {
        mockMvc.perform(get("/timeline-option"))
                .andExpect(status().isOk())
                .andExpect(view().name("timeline_option"));
    }

    @Test
    void userGetPage() throws Exception {
        mockMvc.perform(get("/user-get"))
                .andExpect(status().isOk())
                .andExpect(view().name("user_get"));
    }

    @Test
    void userPostPage() throws Exception {
        mockMvc.perform(get("/user-post"))
                .andExpect(status().isOk())
                .andExpect(view().name("user_post"));
    }


    @Test
    void userDeletePage() throws Exception {
        mockMvc.perform(get("/user-delete"))
                .andExpect(status().isOk())
                .andExpect(view().name("user_delete"));
    }

    @Test
    void timelinePostPage() throws Exception {
        mockMvc.perform(get("/timeline-post"))
                .andExpect(status().isOk())
                .andExpect(view().name("timeline_post"));
    }

    @Test
    void timelineGETByUserIdPage() throws Exception {
        mockMvc.perform(get("/timeline-get-by-user-id"))
                .andExpect(status().isOk())
                .andExpect(view().name("timeline_get_user"));
    }

    @Test
    void timelineGETByMediaIdPage() throws Exception {
        mockMvc.perform(get("/timeline-get-by-media-id"))
                .andExpect(status().isOk())
                .andExpect(view().name("timeline_get_media"));
    }

    @Test
    void timelineGETByUserMediaIdPage() throws Exception {
        mockMvc.perform(get("/timeline-get-by-user-media-id"))
                .andExpect(status().isOk())
                .andExpect(view().name("timeline_get_search"));
    }

    @Test
    void timelineDeletePage() throws Exception {
        mockMvc.perform(get("/timeline-delete"))
                .andExpect(status().isOk())
                .andExpect(view().name("timeline_delete"));
    }
}