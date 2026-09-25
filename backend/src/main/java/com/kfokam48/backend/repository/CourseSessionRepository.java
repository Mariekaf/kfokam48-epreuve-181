package com.kfokam48.backend.repository;

import com.kfokam48.backend.entity.CourseSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseSessionRepository extends JpaRepository<CourseSession, Long> {

    boolean existsByCode(String code);
}