package com.feedbackhub.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cohort_upload_records")
public class CohortUploadRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cohortName;
    private String podName;
    private int totalImported;
    private String uploadedBy;
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @PrePersist void onCreate() { this.createdAt = LocalDateTime.now(); }
    public Long getId()              { return id; }
    public String getCohortName()    { return cohortName; }
    public String getPodName()       { return podName; }
    public int getTotalImported()    { return totalImported; }
    public String getUploadedBy()    { return uploadedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setId(Long v)            { this.id            = v; }
    public void setCohortName(String v)  { this.cohortName    = v; }
    public void setPodName(String v)     { this.podName       = v; }
    public void setTotalImported(int v)  { this.totalImported = v; }
    public void setUploadedBy(String v)  { this.uploadedBy    = v; }
    public void setCreatedAt(LocalDateTime v){ this.createdAt = v; }
}
