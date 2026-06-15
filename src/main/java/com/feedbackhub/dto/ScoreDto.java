package com.feedbackhub.dto;

import com.feedbackhub.enums.FeedbackStatus;
import com.feedbackhub.enums.TrendDirection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ScoreDto {

    public static class Request {
        private Long traineeId; private String assignmentName, category, grade, weekLabel;
        private Double score; private LocalDate submittedDate;
        public Request() {}
        public Long      getTraineeId()       { return traineeId; }
        public String    getAssignmentName()  { return assignmentName; }
        public String    getCategory()        { return category; }
        public String    getGrade()           { return grade; }
        public String    getWeekLabel()       { return weekLabel; }
        public Double    getScore()           { return score; }
        public LocalDate getSubmittedDate()   { return submittedDate; }
        public void setTraineeId(Long v)          { this.traineeId      = v; }
        public void setAssignmentName(String v)   { this.assignmentName = v; }
        public void setCategory(String v)         { this.category       = v; }
        public void setGrade(String v)            { this.grade          = v; }
        public void setWeekLabel(String v)        { this.weekLabel      = v; }
        public void setScore(Double v)            { this.score          = v; }
        public void setSubmittedDate(LocalDate v) { this.submittedDate  = v; }
    }

    public static class Response {
        private Long id; private String traineeName, traineeEmail, podName, trainerName;
        private String assignmentName, category, grade, weekLabel;
        private Double score; private LocalDate submittedDate;
        private FeedbackStatus feedbackStatus; private TrendDirection trend;
        private LocalDateTime createdAt; private String aiFeedback;
        public Response() {}
        public Long           getId()             { return id; }
        public String         getTraineeName()    { return traineeName; }
        public String         getTraineeEmail()   { return traineeEmail; }
        public String         getPodName()        { return podName; }
        public String         getTrainerName()    { return trainerName; }
        public String         getAssignmentName() { return assignmentName; }
        public String         getCategory()       { return category; }
        public String         getGrade()          { return grade; }
        public String         getWeekLabel()      { return weekLabel; }
        public Double         getScore()          { return score; }
        public LocalDate      getSubmittedDate()  { return submittedDate; }
        public FeedbackStatus getFeedbackStatus() { return feedbackStatus; }
        public TrendDirection getTrend()          { return trend; }
        public LocalDateTime  getCreatedAt()      { return createdAt; }
        public String         getAiFeedback()     { return aiFeedback; }
        public void setId(Long v)                      { this.id             = v; }
        public void setTraineeName(String v)           { this.traineeName    = v; }
        public void setTraineeEmail(String v)          { this.traineeEmail   = v; }
        public void setPodName(String v)               { this.podName        = v; }
        public void setTrainerName(String v)           { this.trainerName    = v; }
        public void setAssignmentName(String v)        { this.assignmentName = v; }
        public void setCategory(String v)              { this.category       = v; }
        public void setGrade(String v)                 { this.grade          = v; }
        public void setWeekLabel(String v)             { this.weekLabel      = v; }
        public void setScore(Double v)                 { this.score          = v; }
        public void setSubmittedDate(LocalDate v)      { this.submittedDate  = v; }
        public void setFeedbackStatus(FeedbackStatus v){ this.feedbackStatus = v; }
        public void setTrend(TrendDirection v)         { this.trend          = v; }
        public void setCreatedAt(LocalDateTime v)      { this.createdAt      = v; }
        public void setAiFeedback(String v)            { this.aiFeedback     = v; }
    }

    // Result of bulk upload — includes errors list
    public static class BulkUploadResult {
        private List<Response> uploaded;
        private List<String>   errors;
        private int successCount;
        private int errorCount;
        public BulkUploadResult() {}
        public List<Response> getUploaded()    { return uploaded; }
        public List<String>   getErrors()      { return errors; }
        public int getSuccessCount()           { return successCount; }
        public int getErrorCount()             { return errorCount; }
        public void setUploaded(List<Response> v)  { this.uploaded      = v; }
        public void setErrors(List<String> v)      { this.errors        = v; }
        public void setSuccessCount(int v)         { this.successCount  = v; }
        public void setErrorCount(int v)           { this.errorCount    = v; }
    }
}
