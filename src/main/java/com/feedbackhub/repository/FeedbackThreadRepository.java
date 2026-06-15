package com.feedbackhub.repository;
import com.feedbackhub.entity.FeedbackThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface FeedbackThreadRepository extends JpaRepository<FeedbackThread, Long> {

    @Query("SELECT f FROM FeedbackThread f WHERE f.score.id = :scoreId ORDER BY f.createdAt ASC")
    List<FeedbackThread> findByScoreIdOrderByCreatedAtAsc(@Param("scoreId") Long scoreId);

    @Query("SELECT f FROM FeedbackThread f WHERE f.sender.email = :email ORDER BY f.createdAt DESC")
    List<FeedbackThread> findBySenderEmailOrderByCreatedAtDesc(@Param("email") String email);
}
