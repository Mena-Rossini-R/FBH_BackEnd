package com.feedbackhub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String activityType;

    @Column(nullable = false)
    private String description;

    private String performedBy;
    private String targetEntity;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    public ActivityLog() {}

    public ActivityLog(String activityType, String description,
                       String performedBy, String targetEntity) {
        this.activityType = activityType;
        this.description  = description;
        this.performedBy  = performedBy;
        this.targetEntity = targetEntity;
    }

    @PrePersist
    void onCreate() { this.createdAt = LocalDateTime.now(); }

    public Long          getId()            { return id; }
    public String        getActivityType()  { return activityType; }
    public String        getDescription()   { return description; }
    public String        getPerformedBy()   { return performedBy; }
    public String        getTargetEntity()  { return targetEntity; }
    public LocalDateTime getCreatedAt()     { return createdAt; }

    public void setId(Long id)                        { this.id           = id; }
    public void setActivityType(String activityType)  { this.activityType = activityType; }
    public void setDescription(String description)    { this.description  = description; }
    public void setPerformedBy(String performedBy)    { this.performedBy  = performedBy; }
    public void setTargetEntity(String targetEntity)  { this.targetEntity = targetEntity; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt    = createdAt; }
}
