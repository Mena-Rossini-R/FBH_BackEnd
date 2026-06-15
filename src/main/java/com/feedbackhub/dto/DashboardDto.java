package com.feedbackhub.dto;

import java.util.List;
import java.util.Map;

public class DashboardDto {

    // Trainer dashboard stats
    private Long totalStudents;
    private Double avgScore;
    private Long belowThreshold;
    private Long activePods;

    // Trainee dashboard stats
    private Double overallProgress;
    private Long assignmentsDone;
    private Long totalAssignments;
    private Long pendingFeedback;
    private Long podRank;

    private List<ActivityLogDto> recentActivity;
    private List<ScoreDto.Response> recentScores;

    // Pod averages for bar chart: podName -> avgScore
    private Map<String, Double> podAverages;

    // Week-by-week trend for trainee: weekLabel -> score
    private Map<String, Double> weeklyTrend;

    // At-risk students for trainer
    private List<UserDto.Response> atRiskStudents;

    public DashboardDto() {
    }

    public Long getTotalStudents() {
        return totalStudents;
    }

    public Double getAvgScore() {
        return avgScore;
    }

    public Long getBelowThreshold() {
        return belowThreshold;
    }

    public Long getActivePods() {
        return activePods;
    }

    public Double getOverallProgress() {
        return overallProgress;
    }

    public Long getAssignmentsDone() {
        return assignmentsDone;
    }

    public Long getTotalAssignments() {
        return totalAssignments;
    }

    public Long getPendingFeedback() {
        return pendingFeedback;
    }

    public Long getPodRank() {
        return podRank;
    }

    public List<ActivityLogDto> getRecentActivity() {
        return recentActivity;
    }

    public List<ScoreDto.Response> getRecentScores() {
        return recentScores;
    }

    public Map<String, Double> getPodAverages() {
        return podAverages;
    }

    public Map<String, Double> getWeeklyTrend() {
        return weeklyTrend;
    }

    public List<UserDto.Response> getAtRiskStudents() {
        return atRiskStudents;
    }

    public void setTotalStudents(Long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public void setAvgScore(Double avgScore) {
        this.avgScore = avgScore;
    }

    public void setBelowThreshold(Long belowThreshold) {
        this.belowThreshold = belowThreshold;
    }

    public void setActivePods(Long activePods) {
        this.activePods = activePods;
    }

    public void setOverallProgress(Double overallProgress) {
        this.overallProgress = overallProgress;
    }

    public void setAssignmentsDone(Long assignmentsDone) {
        this.assignmentsDone = assignmentsDone;
    }

    public void setTotalAssignments(Long totalAssignments) {
        this.totalAssignments = totalAssignments;
    }

    public void setPendingFeedback(Long pendingFeedback) {
        this.pendingFeedback = pendingFeedback;
    }

    public void setPodRank(Long podRank) {
        this.podRank = podRank;
    }

    public void setRecentActivity(List<ActivityLogDto> recentActivity) {
        this.recentActivity = recentActivity;
    }

    public void setRecentScores(List<ScoreDto.Response> recentScores) {
        this.recentScores = recentScores;
    }

    public void setPodAverages(Map<String, Double> podAverages) {
        this.podAverages = podAverages;
    }

    public void setWeeklyTrend(Map<String, Double> weeklyTrend) {
        this.weeklyTrend = weeklyTrend;
    }

    public void setAtRiskStudents(List<UserDto.Response> atRiskStudents) {
        this.atRiskStudents = atRiskStudents;
    }
}
