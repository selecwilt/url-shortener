package com.maksym_solodukhin.Shortener.controller;

import com.maksym_solodukhin.Shortener.entity.Link;
import com.maksym_solodukhin.Shortener.exception.LinkNotFoundException;
import com.maksym_solodukhin.Shortener.service.LinkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LinkController.class)
public class LinkControllerTest {

    @Autowired 
    MockMvc mockMvc;

    @MockitoBean 
    LinkService service;

    @Test
    void create_returnShortLink() throws Exception {
        Link link = new Link();
        link.setShortCode("abc1234");
        link.setOriginalUrl("https://github.com");
        when(service.create("https://github.com")).thenReturn(link);

        mockMvc.perform(post("/api/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://github.com\"}"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.shortCode").value("abc1234"));
    }

    @Test 
    void create_rejectsNonHttpUrl() throws Exception {
        mockMvc.perform(post("/api/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"ftp://example.com\"}"))
                        .andExpect(status().isBadRequest());
    }
    
    @Test 
    void redirect_returns302WithLocation() throws Exception {
        Link link = new Link();
        link.setOriginalUrl("https://github.com");
        when(service.getByCode("abc1234")).thenReturn(link);

        mockMvc.perform(get("/abc1234"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://github.com"));
    }

    @Test 
    void redirect_unknownCode_returns404() throws Exception {
        when(service.getByCode("nope")).thenThrow(new LinkNotFoundException("nope"));

        mockMvc.perform(get("/nope"))
                .andExpect(status().isNotFound());
    }
}
