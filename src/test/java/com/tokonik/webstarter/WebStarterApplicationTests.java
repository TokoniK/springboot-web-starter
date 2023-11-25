package com.tokonik.webstarter;

import com.tokonik.webstarter.controllers.TestController;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Suite
@SelectPackages({"com.tokonik.webstarter.service","com.tokonik.webstarter.controllers"})
class WebStarterApplicationTests {

    @Autowired
    private TestController controller;

    @Test
    void contextLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

}
