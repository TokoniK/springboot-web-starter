package com.tokonik.webstarter.services;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
//@AutoConfigureMockMvc
//@WebMvcTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestTestService {


    @Autowired
    TestService testService;
//    @Autowired
//    MockMvc mockMvc;

    @AfterAll
    public void tearDown(){
        List<com.tokonik.webstarter.entities.Test> all = testService.getAll().getBody();
        if (all!=null)
            all.forEach(x-> testService.deleteById(x.getTestId() ));

    }


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
