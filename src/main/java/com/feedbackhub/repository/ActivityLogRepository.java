package com.feedbackhub.repository;
import com.feedbackhub.entity.ActivityLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    @Query("SELECT a FROM ActivityLog a ORDER BY a.createdAt DESC")
    List<ActivityLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT a FROM ActivityLog a WHERE a.performedBy = :performedBy ORDER BY a.createdAt DESC")
    List<ActivityLog> findByPerformedByOrderByCreatedAtDesc(@Param("performedBy") String performedBy);

    @Query("SELECT a FROM ActivityLog a WHERE a.targetEntity = :targetEntity ORDER BY a.createdAt DESC")
    List<ActivityLog> findByTargetEntityOrderByCreatedAtDesc(@Param("targetEntity") String targetEntity, Pageable pageable);
}
