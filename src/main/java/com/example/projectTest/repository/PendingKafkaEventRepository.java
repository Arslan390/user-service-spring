package com.example.projectTest.repository;

import com.example.projectTest.entity.PendingKafkaEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PendingKafkaEventRepository extends JpaRepository<PendingKafkaEvent, Long> {
    List<PendingKafkaEvent> findByProcessedFalseAndRetryCountLessThan(int maxRetries);

    @Modifying
    @Transactional
    @Query("DELETE FROM PendingKafkaEvent e WHERE e.createdAt < :cutoffDate")
    void deleteByCreatedAtBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
}
