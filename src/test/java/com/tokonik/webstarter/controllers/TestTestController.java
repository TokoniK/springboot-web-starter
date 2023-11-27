package com.tokonik.webstarter.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokonik.webstarter.services.TestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestTestController {

    @Autowired
    TestService testService;
    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    public void setup(){
        List<com.tokonik.webstarter.entities.Test> all = testService.getAll().getBody();
        if (all!=null)
            all.forEach(x-> testService.deleteById(x.getTestId() ));
    }

    @Test
    @DisplayName("Test get some")
    public void testTestControllerGet() throws Exception {

        mockMvc.perform( MockMvcRequestBuilders
                .post("/test")
                .content(asJsonString(com.tokonik.webstarter.entities.Test.builder()
                        .testDescription("")
                        .testName("")
                        .testDate(new Date())
                        .build()
                ))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());


        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

    }

    @Test
    @DisplayName("Test get none")
    public void testTestControllerGetNone() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());

    }


    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
