package com.log.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.log.model.DailyLog;

public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
//    List<DailyLog> findByUserIdOrderByDateDesc(Long userId);
//    Optional<DailyLog> findByUserIdAndDate(Long userId, LocalDate date);
    Page<DailyLog> findByUserIdOrderByDateDesc(Long userId, Pageable pageable);

    Optional<DailyLog> findByUserIdAndDate(Long userId, LocalDate date);
}
