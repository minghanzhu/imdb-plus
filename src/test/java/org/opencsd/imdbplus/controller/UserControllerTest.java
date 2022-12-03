package org.opencsd.imdbplus.controller;

import org.opencsd.imdbplus.entity.AccountSetting;
import org.opencsd.imdbplus.entity.User;
import org.opencsd.imdbplus.repository.UserRepository;
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

@WebMvcTest(UserController.class)
@ExtendWith(SpringExtension.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserRepository mockUserRepository;
    private User userTest1;
    private User userReturn1;
    private User userTest4;
    private User userTest5;
    private AccountSetting accountSetting;

    @BeforeEach
    void setUp() {
        accountSetting = new AccountSetting(true, true);

        userTest1 = new User("newUser1", "newUser1@gmail.com", accountSetting);

        userReturn1 = new User("newUser1", "newUser1@gmail.com", accountSetting);
        userReturn1.setUserId("f7e4fb88-b59c-4c73-ad36-21fb5107646c");
        userReturn1.setAccessToken("8ed4cea1-eee6-41bc-97f1-12a6095b51aa");

        userTest4 = new User("existUser", "existUser@gmail.com", accountSetting);

        userTest5 = new User("validTokenUser", "validTokenUser@gmail.com", accountSetting);
        userTest5.setUserId("aaaf7e4fb88-b59c-4c73-ad36-21fb5107646c");
        userTest5.setAccessToken("ccced4cea1-eee6-41bc-97f1-12a6095b51aa");
    }

    @AfterEach
    void tearDown() {
        userTest1 = null;
        userReturn1 = null;
        userTest4 = null;
    }

    @Order(1)
    @Test
    void getUser() throws Exception {
        when(mockUserRepository.getUser("f7e4fb88-b59c-4c73-ad36-21fb5107646c")).thenReturn(userReturn1);
        when(mockUserRepository.getUser("a7e4fb88-b59c-4c73-ad36-21fb5107646c")).thenReturn(null);

        RequestBuilder requestBuilder1 = get("/user/{id}", "f7e4fb88-b59c-4c73-ad36-21fb5107646c");
        RequestBuilder requestBuilder2 = get("/user/{id}", "a7e4fb88-b59c-4c73-ad36-21fb5107646c");

        mockMvc.perform(requestBuilder1)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.
                        jsonPath("$.username").value("newUser1"))
                .andExpect(MockMvcResultMatchers.
                        jsonPath("$.email").value("newUser1@gmail.com"))
                .andExpect(MockMvcResultMatchers.
                        jsonPath("$.accountSetting.isPrivate").value(true))
                .andExpect(MockMvcResultMatchers.
                        jsonPath("$.accountSetting.isAdult").value(true));

        mockMvc.perform(requestBuilder2)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Order(2)
    @Test
    void saveUser() throws Exception {
        when(mockUserRepository.save(userTest1)).thenReturn(userReturn1);
        when(mockUserRepository.save(userTest4)).thenReturn(null);

        RequestBuilder requestBuilder3 = post("/user")
                .content(asJsonString(userTest1))
                .contentType(MediaType.APPLICATION_JSON);
        RequestBuilder requestBuilder4 = post("/user")
                .content(asJsonString(userTest4))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder3)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.
                        jsonPath("$.username").value("newUser1"))
                .andExpect(MockMvcResultMatchers.
                        jsonPath("$.email").value("newUser1@gmail.com"))
                .andExpect(MockMvcResultMatchers.
                        jsonPath("$.accountSetting.isPrivate").value(true))
                .andExpect(MockMvcResultMatchers.
                        jsonPath("$.accountSetting.isAdult").value(true))
                .andExpect(MockMvcResultMatchers.
                        jsonPath("$.userId").value("f7e4fb88-b59c-4c73-ad36-21fb5107646c"))
                .andExpect(MockMvcResultMatchers.
                        jsonPath("$.accessToken").value("8ed4cea1-eee6-41bc-97f1-12a6095b51aa"));

        mockMvc.perform(requestBuilder4)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User already exists"));
    }

    @Order(3)
    @Test
    void deleteUser() throws Exception {
        when(mockUserRepository.delete("vaf7e4fb88-b59c-4c73-ad36-21fb5107646c",
                "9ed4cea1-eee6-41bc-97f1-12a6095b51aa")).thenReturn("User deleted successfully");
        when(mockUserRepository.delete("inf7e4fb88-b59c-4c73-ad36-21fb5107646c",
                "in9ed4cea1-eee6-41bc-97f1-12a6095b51aa")).thenReturn("Invalid access token");

        RequestBuilder requestBuilder1 = delete("/user/{id}", "vaf7e4fb88-b59c-4c73-ad36-21fb5107646c")
                .header("Authorization", "9ed4cea1-eee6-41bc-97f1-12a6095b51aa");
        RequestBuilder requestBuilder2 = delete("/user/{id}", "inf7e4fb88-b59c-4c73-ad36-21fb5107646c")
                .header("Authorization", "in9ed4cea1-eee6-41bc-97f1-12a6095b51aa");

        mockMvc.perform(requestBuilder1)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));

        mockMvc.perform(requestBuilder2)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Invalid access token"));
    }

    @Order(4)
    @Test
    void updateUser() throws Exception {
        when(mockUserRepository.update("aaaf7e4fb88-b59c-4c73-ad36-21fb5107646c", userTest5))
                .thenReturn("aaaf7e4fb88-b59c-4c73-ad36-21fb5107646c");

        RequestBuilder requestBuilder1 = put("/user/{id}", "aaaf7e4fb88-b59c-4c73-ad36-21fb5107646c")
                .content(asJsonString(userTest5))
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