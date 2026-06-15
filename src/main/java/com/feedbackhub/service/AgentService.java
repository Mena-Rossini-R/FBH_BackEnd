package com.feedbackhub.service;

import com.feedbackhub.dto.AgentDto;
import com.feedbackhub.entity.Score;
import com.feedbackhub.entity.User;
import com.feedbackhub.enums.UserRole;
import com.feedbackhub.exception.ResourceNotFoundException;
import com.feedbackhub.repository.ScoreRepository;
import com.feedbackhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AgentService — AI Decision Assistant for FeedbackHub
 *
 * Generates a consolidated feedback report for the trainer
 * by analysing ALL trainees' scores in one shot.
 *
 * 100% rule-based. No external API. No API key.
 */
@Service
@Transactional(readOnly = true)
public class AgentService {

    @Autowired private ScoreRepository scoreRepo;
    @Autowired private UserRepository  userRepo;

    public AgentDto.ConsolidatedReport generateConsolidatedReport(String trainerEmail) {
        User trainer = userRepo.findByEmail(trainerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));

        // All trainees in the system
        List<User> trainees = userRepo.findByRole(UserRole.TRAINEE);
        if (trainees.isEmpty()) {
            AgentDto.ConsolidatedReport empty = new AgentDto.ConsolidatedReport();
            empty.setTrainerName(trainer.getFullName());
            empty.setExecutiveSummary("No trainees found. Upload scores to generate analysis.");
            empty.setTraineeSummaries(new ArrayList<>());
            empty.setCategoryInsights(new ArrayList<>());
            empty.setTrainerActions(new ArrayList<>());
            return empty;
        }

        // ── Collect all scores grouped by trainee ─────────────────────────
        Map<User, List<Score>> scoresByTrainee = new LinkedHashMap<>();
        for (User t : trainees) {
            List<Score> scores = scoreRepo.findByTraineeOrderBySubmittedDateDesc(t);
            if (!scores.isEmpty()) scoresByTrainee.put(t, scores);
        }

        int totalAssessments = scoresByTrainee.values().stream().mapToInt(List::size).sum();

        // ── Class average ─────────────────────────────────────────────────
        double classAvg = scoresByTrainee.values().stream()
                .flatMap(Collection::stream)
                .mapToDouble(Score::getScore)
                .average().orElse(0);

        // ── Per-trainee summaries ─────────────────────────────────────────
        List<AgentDto.TraineeSummary> summaries = new ArrayList<>();
        List<String> topPerformers   = new ArrayList<>();
        List<String> atRiskTrainees  = new ArrayList<>();

        for (Map.Entry<User, List<Score>> entry : scoresByTrainee.entrySet()) {
            User       trainee = entry.getKey();
            List<Score> scores = entry.getValue();

            double avg = scores.stream().mapToDouble(Score::getScore).average().orElse(0);
            avg = Math.round(avg * 10.0) / 10.0;

            // Trend: compare first half vs second half
            String trend = "STABLE";
            if (scores.size() >= 4) {
                int mid = scores.size() / 2;
                // scores are DESC so recent = first half
                double recentAvg = scores.subList(0, mid).stream().mapToDouble(Score::getScore).average().orElse(0);
                double olderAvg  = scores.subList(mid, scores.size()).stream().mapToDouble(Score::getScore).average().orElse(0);
                if (recentAvg > olderAvg + 3)       trend = "IMPROVING";
                else if (recentAvg < olderAvg - 3)  trend = "DECLINING";
            }

            // Weakest category
            Map<String, List<Double>> byCat = new LinkedHashMap<>();
            for (Score s : scores) {
                String cat = s.getCategory() != null ? s.getCategory() : "General";
                byCat.computeIfAbsent(cat, k -> new ArrayList<>()).add(s.getScore());
            }
            String weakest = byCat.entrySet().stream()
                    .min(Comparator.comparingDouble(e -> e.getValue().stream().mapToDouble(d->d).average().orElse(100)))
                    .map(Map.Entry::getKey).orElse("N/A");

            // Status
            String status;
            if      (avg >= 80) status = "STRONG";
            else if (avg >= 70) status = "GOOD";
            else if (avg >= 60) status = "WATCH";
            else                status = "AT_RISK";

            // Recommendation
            String rec = buildTraineeRecommendation(trainee.getFullName(), avg, trend, weakest, status);

            if (avg >= 80) topPerformers.add(trainee.getFullName() + " (" + avg + "%)");
            if (avg <  65) atRiskTrainees.add(trainee.getFullName() + " (" + avg + "%)");

            AgentDto.TraineeSummary ts = new AgentDto.TraineeSummary();
            ts.setName(trainee.getFullName());
            ts.setEmail(trainee.getEmail());
            ts.setPodName(trainee.getPodName());
            ts.setAverage(avg);
            ts.setStatus(status);
            ts.setTrend(trend);
            ts.setWeakestCategory(weakest);
            ts.setAssessmentCount(scores.size());
            ts.setRecommendation(rec);
            summaries.add(ts);
        }

        // Sort by average ascending — trainer sees most urgent cases first
        summaries.sort(Comparator.comparingDouble(AgentDto.TraineeSummary::getAverage));

        // ── Category insights ─────────────────────────────────────────────
        Map<String, List<Double>> globalByCat = new LinkedHashMap<>();
        for (List<Score> scores : scoresByTrainee.values()) {
            for (Score s : scores) {
                String cat = s.getCategory() != null ? s.getCategory() : "General";
                globalByCat.computeIfAbsent(cat, k -> new ArrayList<>()).add(s.getScore());
            }
        }

        List<AgentDto.CategoryInsight> catInsights = new ArrayList<>();
        for (Map.Entry<String, List<Double>> e : globalByCat.entrySet()) {
            double catAvg = e.getValue().stream().mapToDouble(d->d).average().orElse(0);
            catAvg = Math.round(catAvg * 10.0) / 10.0;
            int below = (int) e.getValue().stream().filter(v -> v < 65).count();

            String icon       = catAvg >= 75 ? "✅" : catAvg >= 65 ? "⚠️" : "🚨";
            String suggestion = buildCategorySuggestion(e.getKey(), catAvg, below);

            AgentDto.CategoryInsight ci = new AgentDto.CategoryInsight();
            ci.setCategory(e.getKey());
            ci.setClassAverage(catAvg);
            ci.setStudentsBelowThreshold(below);
            ci.setHealthIcon(icon);
            ci.setSuggestion(suggestion);
            catInsights.add(ci);
        }
        catInsights.sort(Comparator.comparingDouble(AgentDto.CategoryInsight::getClassAverage));

        // ── Class health ──────────────────────────────────────────────────
        String health;
        if      (classAvg >= 80) health = "EXCELLENT";
        else if (classAvg >= 70) health = "GOOD";
        else if (classAvg >= 60) health = "NEEDS_ATTENTION";
        else                     health = "CRITICAL";

        // ── Trainer action items ──────────────────────────────────────────
        List<String> actions = buildTrainerActions(atRiskTrainees, catInsights, classAvg, summaries);

        // ── Executive summary ─────────────────────────────────────────────
        String summary = buildExecutiveSummary(trainer.getFullName(), trainees.size(),
                scoresByTrainee.size(), classAvg, health, atRiskTrainees.size(), topPerformers.size());

        // ── Confidence score ─────────────────────────────────────────────
        int confidence = totalAssessments > 20 ? 90 : totalAssessments > 10 ? 75 : totalAssessments > 4 ? 60 : 40;

        AgentDto.ConsolidatedReport report = new AgentDto.ConsolidatedReport();
        report.setTrainerName(trainer.getFullName());
        report.setTotalTrainees(scoresByTrainee.size());
        report.setTotalAssessments(totalAssessments);
        report.setClassAverage(Math.round(classAvg * 10.0) / 10.0);
        report.setClassHealthStatus(health);
        report.setExecutiveSummary(summary);
        report.setTraineeSummaries(summaries);
        report.setTopPerformers(topPerformers);
        report.setAtRiskTrainees(atRiskTrainees);
        report.setCategoryInsights(catInsights);
        report.setTrainerActions(actions);
        report.setConfidenceScore(confidence);
        return report;
    }

    private String buildExecutiveSummary(String trainerName, int total, int active,
                                          double avg, String health, int atRisk, int top) {
        StringBuilder sb = new StringBuilder();
        sb.append("Trainer ").append(trainerName).append(" has ").append(active)
          .append(" active trainee(s) out of ").append(total).append(" enrolled. ");
        sb.append("The class average stands at ").append(String.format("%.1f", avg)).append("%, ");
        sb.append("placing the overall class health at ").append(health.replace("_", " ")).append(". ");
        if (atRisk > 0) sb.append(atRisk).append(" trainee(s) are at risk (average below 65%) and require immediate intervention. ");
        if (top   > 0) sb.append(top).append(" trainee(s) are performing strongly (average above 80%) and may benefit from advanced challenges. ");
        if (atRisk == 0 && top == 0) sb.append("Overall performance is within the expected range. ");
        sb.append("Focus areas and recommended actions are listed below.");
        return sb.toString();
    }

    private String buildTraineeRecommendation(String name, double avg, String trend,
                                               String weakest, String status) {
        if ("AT_RISK".equals(status)) {
            return "Schedule one-on-one session this week. Focus on " + weakest + ". Daily check-ins recommended.";
        }
        if ("WATCH".equals(status) && "DECLINING".equals(trend)) {
            return "Monitor closely. " + weakest + " scores declining — assign targeted practice exercises.";
        }
        if ("STRONG".equals(status) && "IMPROVING".equals(trend)) {
            return "Excellent trajectory. Assign stretch goals in " + weakest + " to maintain engagement.";
        }
        if ("GOOD".equals(status)) {
            return "On track. Address gaps in " + weakest + " before next assessment cycle.";
        }
        return "Continue regular feedback. Watch " + weakest + " category for improvement.";
    }

    private String buildCategorySuggestion(String category, double avg, int below) {
        if (avg < 65) return "Class is struggling with " + category + " (" + below + " students below 65%). Schedule a refresher session.";
        if (avg < 75) return category + " needs focused practice. Consider group exercises or peer learning.";
        return category + " performance is satisfactory. Maintain current approach.";
    }

    private List<String> buildTrainerActions(List<String> atRisk,
                                              List<AgentDto.CategoryInsight> cats,
                                              double classAvg,
                                              List<AgentDto.TraineeSummary> summaries) {
        List<String> actions = new ArrayList<>();

        if (!atRisk.isEmpty()) {
            actions.add("🚨 Immediate: Schedule intervention sessions for " + String.join(", ", atRisk));
        }

        // Find weakest category across class
        cats.stream().filter(c -> c.getClassAverage() < 65).limit(2).forEach(c ->
            actions.add("📚 Run a group refresher on " + c.getCategory()
                + " — " + c.getStudentsBelowThreshold() + " student(s) below threshold")
        );

        // Declining trainees
        long declining = summaries.stream().filter(s -> "DECLINING".equals(s.getTrend())).count();
        if (declining > 0) {
            actions.add("📉 " + declining + " trainee(s) show declining trends — review recent feedback quality");
        }

        // Positive action
        if (classAvg >= 70) {
            actions.add("✅ Class average is healthy — maintain current feedback cadence");
        } else {
            actions.add("⚠️ Class average below 70% — increase feedback frequency and provide additional resources");
        }

        if (actions.isEmpty()) {
            actions.add("Continue regular assessments and keep feedback detailed per category.");
        }

        return actions;
    }
}
