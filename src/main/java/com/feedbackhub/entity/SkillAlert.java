package com.feedbackhub.entity;

import com.feedbackhub.enums.AlertLevel;
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

import java.time.LocalDateTime;

@Entity
@Table(name = "skill_alerts")
public class SkillAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_id", nullable = false)
    private User trainee;

    @Column(nullable = false)
    private String skillName;

    private Double scoreValue;

    @Enumerated(EnumType.STRING)
    private AlertLevel alertLevel = AlertLevel.WARNING;

    private String message;

    private boolean acknowledged = false;
    private boolean resolved     = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime acknowledgedAt;

    public SkillAlert() {}

    @PrePersist
    void onCreate() { this.createdAt = LocalDateTime.now(); }

    public Long          getId()              { return id; }
    public User          getTrainee()         { return trainee; }
    public String        getSkillName()       { return skillName; }
    public Double        getScoreValue()      { return scoreValue; }
    public AlertLevel    getAlertLevel()      { return alertLevel; }
    public String        getMessage()         { return message; }
    public boolean       isAcknowledged()     { return acknowledged; }
    public boolean       isResolved()         { return resolved; }
    public LocalDateTime getCreatedAt()       { return createdAt; }
    public LocalDateTime getAcknowledgedAt()  { return acknowledgedAt; }

    public void setId(Long id)                              { this.id             = id; }
    public void setTrainee(User trainee)                    { this.trainee        = trainee; }
    public void setSkillName(String skillName)              { this.skillName      = skillName; }
    public void setScoreValue(Double scoreValue)            { this.scoreValue     = scoreValue; }
    public void setAlertLevel(AlertLevel alertLevel)        { this.alertLevel     = alertLevel; }
    public void setMessage(String message)                  { this.message        = message; }
    public void setAcknowledged(boolean acknowledged)       { this.acknowledged   = acknowledged; }
    public void setResolved(boolean resolved)               { this.resolved       = resolved; }
    public void setCreatedAt(LocalDateTime createdAt)       { this.createdAt      = createdAt; }
    public void setAcknowledgedAt(LocalDateTime acknowledgedAt){ this.acknowledgedAt = acknowledgedAt; }
}
