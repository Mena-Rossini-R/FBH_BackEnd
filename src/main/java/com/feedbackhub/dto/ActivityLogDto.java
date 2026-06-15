package com.feedbackhub.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ActivityLogDto {
    private Long          id;
    private String        activityType;
    private String        description;
    private String        performedBy;
    private String        targetEntity;
    private LocalDateTime createdAt;
    public ActivityLogDto() {}
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
