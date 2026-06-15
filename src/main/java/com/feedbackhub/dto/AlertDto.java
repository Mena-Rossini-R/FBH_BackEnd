package com.feedbackhub.dto;

import com.feedbackhub.enums.AlertLevel;
import java.time.LocalDateTime;

public class AlertDto {

    public static class Response {
        private Long          id;
        private String        skillName;
        private Double        scoreValue;
        private AlertLevel    alertLevel;
        private String        message;
        private boolean       acknowledged;
        private boolean       resolved;
        private LocalDateTime createdAt;
        private LocalDateTime acknowledgedAt;
        public Response() {}
        public Long          getId()             { return id; }
        public String        getSkillName()      { return skillName; }
        public Double        getScoreValue()     { return scoreValue; }
        public AlertLevel    getAlertLevel()     { return alertLevel; }
        public String        getMessage()        { return message; }
        public boolean       isAcknowledged()    { return acknowledged; }
        public boolean       isResolved()        { return resolved; }
        public LocalDateTime getCreatedAt()      { return createdAt; }
        public LocalDateTime getAcknowledgedAt() { return acknowledgedAt; }
        public void setId(Long id)                                   { this.id             = id; }
        public void setSkillName(String skillName)                   { this.skillName      = skillName; }
        public void setScoreValue(Double scoreValue)                 { this.scoreValue     = scoreValue; }
        public void setAlertLevel(AlertLevel alertLevel)             { this.alertLevel     = alertLevel; }
        public void setMessage(String message)                       { this.message        = message; }
        public void setAcknowledged(boolean acknowledged)            { this.acknowledged   = acknowledged; }
        public void setResolved(boolean resolved)                    { this.resolved       = resolved; }
        public void setCreatedAt(LocalDateTime createdAt)            { this.createdAt      = createdAt; }
        public void setAcknowledgedAt(LocalDateTime acknowledgedAt)  { this.acknowledgedAt = acknowledgedAt; }
    }
}
