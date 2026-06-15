package com.feedbackhub.entity;

import com.feedbackhub.enums.FeedbackStatus;
import com.feedbackhub.enums.TrendDirection;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "scores")
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_id", nullable = false)
    private User trainee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    private User trainer;

    @Column(nullable = false)
    private String assignmentName;

    private String category;      // Technical, Testing, Communication, etc.

    @Column(nullable = false)
    private Double score;

    private String grade;         // A, B+, C, etc.

    private LocalDate submittedDate;

    @Enumerated(EnumType.STRING)
    private FeedbackStatus feedbackStatus = FeedbackStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private TrendDirection trend = TrendDirection.STABLE;

    private String weekLabel;     // e.g. "W1", "W2"

    @Column(updatable = false)
    private LocalDateTime createdAt;

    public Score() {}

    @PrePersist
    void onCreate() { this.createdAt = LocalDateTime.now(); }

    public Long           getId()             { return id; }
    public User           getTrainee()        { return trainee; }
    public User           getTrainer()        { return trainer; }
    public String         getAssignmentName() { return assignmentName; }
    public String         getCategory()       { return category; }
    public Double         getScore()          { return score; }
    public String         getGrade()          { return grade; }
    public LocalDate      getSubmittedDate()  { return submittedDate; }
    public FeedbackStatus getFeedbackStatus() { return feedbackStatus; }
    public TrendDirection getTrend()          { return trend; }
    public String         getWeekLabel()      { return weekLabel; }
    public LocalDateTime  getCreatedAt()      { return createdAt; }

    public void setId(Long id)                           { this.id             = id; }
    public void setTrainee(User trainee)                 { this.trainee        = trainee; }
    public void setTrainer(User trainer)                 { this.trainer        = trainer; }
    public void setAssignmentName(String assignmentName) { this.assignmentName = assignmentName; }
    public void setCategory(String category)             { this.category       = category; }
    public void setScore(Double score)                   { this.score          = score; }
    public void setGrade(String grade)                   { this.grade          = grade; }
    public void setSubmittedDate(LocalDate submittedDate){ this.submittedDate  = submittedDate; }
    public void setFeedbackStatus(FeedbackStatus status) { this.feedbackStatus = status; }
    public void setTrend(TrendDirection trend)           { this.trend          = trend; }
    public void setWeekLabel(String weekLabel)           { this.weekLabel      = weekLabel; }
    public void setCreatedAt(LocalDateTime createdAt)    { this.createdAt      = createdAt; }
}
