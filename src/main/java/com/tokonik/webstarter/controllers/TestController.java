package com.tokonik.webstarter.controllers;

import com.tokonik.webstarter.entities.Test;
import com.tokonik.webstarter.services.TestService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/test")
public class TestController extends AbstractController<Test, TestService, Test> {

    public TestController(TestService testService) {
        this.setService(testService);
    }

}
