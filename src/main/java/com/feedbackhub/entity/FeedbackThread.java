package com.feedbackhub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "feedback_threads")
public class FeedbackThread {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "score_id", nullable = false)
    private Score score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    // TRAINER or TRAINEE
    private String senderRole;

    private boolean readByTrainee = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    public FeedbackThread() {}

    @PrePersist
    void onCreate() { this.createdAt = LocalDateTime.now(); }

    public Long          getId()              { return id; }
    public Score         getScore()           { return score; }
    public User          getSender()          { return sender; }
    public String        getMessage()         { return message; }
    public String        getSenderRole()      { return senderRole; }
    public boolean       isReadByTrainee()    { return readByTrainee; }
    public LocalDateTime getCreatedAt()       { return createdAt; }

    public void setId(Long id)                        { this.id           = id; }
    public void setScore(Score score)                 { this.score        = score; }
    public void setSender(User sender)                { this.sender       = sender; }
    public void setMessage(String message)            { this.message      = message; }
    public void setSenderRole(String senderRole)      { this.senderRole   = senderRole; }
    public void setReadByTrainee(boolean read)        { this.readByTrainee = read; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt    = createdAt; }
}
