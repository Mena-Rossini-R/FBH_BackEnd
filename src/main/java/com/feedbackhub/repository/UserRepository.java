package com.feedbackhub.repository;
import com.feedbackhub.entity.User;
import com.feedbackhub.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.role = :role")
    List<User> findByRole(@Param("role") UserRole role);

    @Query("SELECT u FROM User u WHERE u.podName = :podName")
    List<User> findByPodName(@Param("podName") String podName);

    @Query("SELECT u FROM User u WHERE u.cohortName = :cohortName")
    List<User> findByCohortName(@Param("cohortName") String cohortName);

    @Query("SELECT u FROM User u WHERE u.role = 'TRAINEE' ORDER BY u.cohortName, u.podName, u.fullName")
    List<User> findAllTraineesOrdered();

    @Query("SELECT DISTINCT u.cohortName FROM User u WHERE u.cohortName IS NOT NULL")
    List<String> findDistinctCohorts();

    @Query("SELECT u FROM User u WHERE u.role = 'TRAINEE' AND (u.podName IS NULL OR u.podName = '') ORDER BY u.fullName")
    List<User> findUnassignedTrainees();
}
