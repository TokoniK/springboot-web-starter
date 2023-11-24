package com.tokonik.webstarter.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
//@AutoConfigureMockMvc
//@WebMvcTest
public class TestTestService {


    @Autowired
    TestService testService;
//    @Autowired
//    MockMvc mockMvc;


    @Test
    public void testTestServiceFetch(){

        com.tokonik.webstarter.entities.Test obj =
                com.tokonik.webstarter.entities.Test.builder().testDescription("string")
                .testName("string xxx")
                .testDate(new Date())
                .build();

        testService.create(obj);
        Assertions.assertEquals(1, testService.getAll().getBody().size());
    }

}
