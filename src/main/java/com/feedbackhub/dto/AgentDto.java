package com.feedbackhub.dto;

import java.util.List;

public class AgentDto {

    /** Consolidated feedback analysis for the trainer about ALL trainees */
    public static class ConsolidatedReport {
        private String trainerName;
        private int totalTrainees;
        private int totalAssessments;
        private double classAverage;
        private String classHealthStatus;    // "EXCELLENT" | "GOOD" | "NEEDS_ATTENTION" | "CRITICAL"
        private String executiveSummary;
        private List<TraineeSummary>  traineeSummaries;
        private List<String>          topPerformers;
        private List<String>          atRiskTrainees;
        private List<CategoryInsight> categoryInsights;
        private List<String>          trainerActions;
        private int                   confidenceScore;

        public ConsolidatedReport() {}

        public String               getTrainerName()      { return trainerName; }
        public int                  getTotalTrainees()    { return totalTrainees; }
        public int                  getTotalAssessments() { return totalAssessments; }
        public double               getClassAverage()     { return classAverage; }
        public String               getClassHealthStatus(){ return classHealthStatus; }
        public String               getExecutiveSummary() { return executiveSummary; }
        public List<TraineeSummary> getTraineeSummaries() { return traineeSummaries; }
        public List<String>         getTopPerformers()    { return topPerformers; }
        public List<String>         getAtRiskTrainees()   { return atRiskTrainees; }
        public List<CategoryInsight>getCategoryInsights() { return categoryInsights; }
        public List<String>         getTrainerActions()   { return trainerActions; }
        public int                  getConfidenceScore()  { return confidenceScore; }

        public void setTrainerName(String v)                     { trainerName       = v; }
        public void setTotalTrainees(int v)                      { totalTrainees     = v; }
        public void setTotalAssessments(int v)                   { totalAssessments  = v; }
        public void setClassAverage(double v)                    { classAverage      = v; }
        public void setClassHealthStatus(String v)               { classHealthStatus = v; }
        public void setExecutiveSummary(String v)                { executiveSummary  = v; }
        public void setTraineeSummaries(List<TraineeSummary> v)  { traineeSummaries  = v; }
        public void setTopPerformers(List<String> v)             { topPerformers     = v; }
        public void setAtRiskTrainees(List<String> v)            { atRiskTrainees    = v; }
        public void setCategoryInsights(List<CategoryInsight> v) { categoryInsights  = v; }
        public void setTrainerActions(List<String> v)            { trainerActions    = v; }
        public void setConfidenceScore(int v)                    { confidenceScore   = v; }
    }

    /** Per-trainee summary inside the consolidated report */
    public static class TraineeSummary {
        private String name;
        private String email;
        private String podName;
        private double average;
        private String status;          // "STRONG" | "GOOD" | "WATCH" | "AT_RISK"
        private String trend;           // "IMPROVING" | "DECLINING" | "STABLE"
        private String weakestCategory;
        private int    assessmentCount;
        private String recommendation;

        public TraineeSummary() {}

        public String getName()            { return name; }
        public String getEmail()           { return email; }
        public String getPodName()         { return podName; }
        public double getAverage()         { return average; }
        public String getStatus()          { return status; }
        public String getTrend()           { return trend; }
        public String getWeakestCategory() { return weakestCategory; }
        public int    getAssessmentCount() { return assessmentCount; }
        public String getRecommendation()  { return recommendation; }

        public void setName(String v)            { name            = v; }
        public void setEmail(String v)           { email           = v; }
        public void setPodName(String v)         { podName         = v; }
        public void setAverage(double v)         { average         = v; }
        public void setStatus(String v)          { status          = v; }
        public void setTrend(String v)           { trend           = v; }
        public void setWeakestCategory(String v) { weakestCategory = v; }
        public void setAssessmentCount(int v)    { assessmentCount = v; }
        public void setRecommendation(String v)  { recommendation  = v; }
    }

    /** Per-category class-level insight */
    public static class CategoryInsight {
        private String category;
        private double classAverage;
        private int    studentsBelowThreshold;
        private String healthIcon;      // "✅" | "⚠️" | "🚨"
        private String suggestion;

        public CategoryInsight() {}

        public String getCategory()               { return category; }
        public double getClassAverage()           { return classAverage; }
        public int    getStudentsBelowThreshold() { return studentsBelowThreshold; }
        public String getHealthIcon()             { return healthIcon; }
        public String getSuggestion()             { return suggestion; }

        public void setCategory(String v)               { category               = v; }
        public void setClassAverage(double v)           { classAverage           = v; }
        public void setStudentsBelowThreshold(int v)    { studentsBelowThreshold = v; }
        public void setHealthIcon(String v)             { healthIcon             = v; }
        public void setSuggestion(String v)             { suggestion             = v; }
    }
}
