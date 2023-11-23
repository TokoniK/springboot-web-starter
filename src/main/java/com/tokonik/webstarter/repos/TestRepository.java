package com.tokonik.webstarter.repos;

import com.tokonik.webstarter.entities.Test;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<Test, Integer> {
}


