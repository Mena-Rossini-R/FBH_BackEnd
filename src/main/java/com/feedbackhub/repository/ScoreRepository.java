package com.feedbackhub.repository;
import com.feedbackhub.entity.Score;
import com.feedbackhub.entity.User;
import com.feedbackhub.enums.FeedbackStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ScoreRepository extends JpaRepository<Score, Long> {

    @Query("SELECT s FROM Score s WHERE s.trainee = :trainee ORDER BY s.submittedDate DESC")
    List<Score> findByTraineeOrderBySubmittedDateDesc(@Param("trainee") User trainee);

    @Query("SELECT s FROM Score s WHERE s.trainer = :trainer ORDER BY s.submittedDate DESC")
    List<Score> findByTrainerOrderBySubmittedDateDesc(@Param("trainer") User trainer);

    @Query("SELECT AVG(s.score) FROM Score s WHERE s.trainee = :trainee")
    Optional<Double> findAvgScoreByTrainee(@Param("trainee") User trainee);

    @Query("SELECT AVG(s.score) FROM Score s WHERE s.trainee.podName = :pod")
    Optional<Double> findAvgScoreByPod(@Param("pod") String pod);

    @Query("SELECT s FROM Score s WHERE s.trainee.podName = :pod ORDER BY s.submittedDate DESC")
    List<Score> findByPod(@Param("pod") String pod);

    @Query("SELECT COUNT(s) FROM Score s WHERE s.trainee = :trainee AND s.feedbackStatus = :status")
    long countByTraineeAndFeedbackStatus(@Param("trainee") User trainee, @Param("status") FeedbackStatus status);

    @Query("SELECT s FROM Score s WHERE s.trainee = :trainee ORDER BY s.submittedDate DESC")
    List<Score> findByTraineeOrderBySubmittedDateDesc(@Param("trainee") User trainee, Pageable pageable);
}
