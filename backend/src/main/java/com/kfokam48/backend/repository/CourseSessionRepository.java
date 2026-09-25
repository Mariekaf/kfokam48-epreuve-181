package com.kfokam48.backend.repository;

import com.kfokam48.backend.entity.CourseSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseSessionRepository extends JpaRepository<CourseSession, Long> {

    boolean existsByCode(String code);

    Optional<CourseSession> findByCode(String code);
}