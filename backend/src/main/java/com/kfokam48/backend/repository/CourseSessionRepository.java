package com.kfokam48.backend.repository;

import com.kfokam48.backend.entity.CourseSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface CourseSessionRepository extends JpaRepository<CourseSession, Long> {

    boolean existsByCode(String code);

    Optional<CourseSession> findByCode(String code);

    List<CourseSession> findByPromotionIdOrderByOuvertureAtDesc(Long promotionId);
}