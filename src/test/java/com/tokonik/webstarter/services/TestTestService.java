package com.tokonik.webstarter.services;

import org.junit.jupiter.api.Test;
//import com.tokonik.webstarter.entities.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

//@TestComponent
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
//        testService.getAll().getBody().size()
    }

//    @Test
//    public void testTestServiceFetch(){

//        mockMvc.perform()

//        testService.create(obj);
//        Assertions.assertEquals(1, testService.getAll().getBody().size());
////        testService.getAll().getBody().size()
//    }

}
