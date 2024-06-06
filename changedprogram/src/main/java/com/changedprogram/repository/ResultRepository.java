package com.changedprogram.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.changedprogram.entity.Result;

public interface ResultRepository extends JpaRepository<Result, Long>{

    Optional<Result> findByUserIdAndPptId(Long userId, Long pptId);
/*
    @Query("SELECT r FROM Result r WHERE r.user.id = :userId AND r.valid > :afterDate")
    List<Result> findByUserIdAndValidAfter(@Param("userId") Long userId, @Param("afterDate") Date afterDate);

    @Query("SELECT r FROM Result r WHERE r.valid <= :thresholdDate AND r.notificationSent = false AND r.user.active = true")
    List<Result> findResultsForNotification(@Param("thresholdDate") Date thresholdDate);
*/
    
    @Query("SELECT r FROM Result r " +
            "JOIN FETCH r.user u " +
            "LEFT JOIN FETCH u.receiver " +
            "LEFT JOIN FETCH u.company " +
            "LEFT JOIN FETCH r.ppt " +
            "WHERE r.valid <= :thirtyDaysAhead " +
            "AND r.notificationSent = false " +
            "AND u.active = true")
     List<Result> findResultsForNotification(@Param("thirtyDaysAhead") Date thirtyDaysAhead);

    
    boolean existsByPptId(Long pptId);
}
