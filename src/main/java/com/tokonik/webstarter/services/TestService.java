package com.tokonik.webstarter.services;

import com.tokonik.webstarter.entities.Test;
import com.tokonik.webstarter.repos.TestRepository;
import org.springframework.stereotype.Service;

@Service
public class TestService extends AbstractService<Test>{

//    TestRepository testRepository;
    public TestService(TestRepository testRepository) {
        super(testRepository);
    }

}
