package com.feedbackhub.repository;
import com.feedbackhub.entity.FeedbackThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface FeedbackThreadRepository extends JpaRepository<FeedbackThread, Long> {

    @Query("SELECT f FROM FeedbackThread f WHERE f.score.id = :scoreId ORDER BY f.createdAt ASC")
    List<FeedbackThread> findByScoreIdOrderByCreatedAtAsc(@Param("scoreId") Long scoreId);

    @Query("SELECT f FROM FeedbackThread f WHERE f.sender.email = :email ORDER BY f.createdAt DESC")
    List<FeedbackThread> findBySenderEmailOrderByCreatedAtDesc(@Param("email") String email);

    @Modifying
    @Query("UPDATE FeedbackThread f SET f.readByTrainee = true WHERE f.score.id = :scoreId AND (f.senderRole = 'TRAINER' OR f.senderRole IS NULL)")
    void markReadByTraineeForScore(@Param("scoreId") Long scoreId);

    @Modifying
    @Query("UPDATE FeedbackThread f SET f.readByTrainer = true WHERE f.score.id = :scoreId AND (f.senderRole = 'TRAINEE' OR f.senderRole IS NULL)")
    void markReadByTrainerForScore(@Param("scoreId") Long scoreId);

    @Query("SELECT f.score.id, COUNT(f) FROM FeedbackThread f WHERE f.score.trainer.email = :trainerEmail AND (f.senderRole = 'TRAINEE' OR f.senderRole IS NULL) AND (f.readByTrainer = false OR f.readByTrainer IS NULL) GROUP BY f.score.id")
    List<Object[]> countUnreadForTrainer(@Param("trainerEmail") String trainerEmail);

    @Query("SELECT f.score.id, COUNT(f) FROM FeedbackThread f WHERE f.score.trainee.email = :traineeEmail AND (f.senderRole = 'TRAINER' OR f.senderRole IS NULL) AND (f.readByTrainee = false OR f.readByTrainee IS NULL) GROUP BY f.score.id")
    List<Object[]> countUnreadForTrainee(@Param("traineeEmail") String traineeEmail);
}