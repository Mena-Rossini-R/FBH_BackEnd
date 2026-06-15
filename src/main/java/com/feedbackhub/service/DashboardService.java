package com.feedbackhub.service;

import com.feedbackhub.dto.DashboardDto;
import com.feedbackhub.dto.UserDto;
import com.feedbackhub.entity.Score;
import com.feedbackhub.entity.User;
import com.feedbackhub.enums.FeedbackStatus;
import com.feedbackhub.enums.UserRole;
import com.feedbackhub.exception.ResourceNotFoundException;
import com.feedbackhub.repository.ScoreRepository;
import com.feedbackhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ScoreRepository scoreRepo;

    @Autowired
    private ActivityLogService logService;

    public DashboardDto getTrainerDashboard(String trainerEmail) {
        List<User> trainees = userRepo.findByRole(UserRole.TRAINEE);

        // Compute class average
        double totalAvg = 0;
        int count = 0;
        for (User t : trainees) {
            Optional<Double> avg = scoreRepo.findAvgScoreByTrainee(t);
            if (avg.isPresent()) {
                totalAvg += avg.get();
                count++;
            }
        }
        double classAvg = count > 0 ? totalAvg / count : 0;

        // At-risk students (avg < 65)
        List<UserDto.Response> atRisk = new ArrayList<>();
        for (User t : trainees) {
            Optional<Double> avg = scoreRepo.findAvgScoreByTrainee(t);
            if (avg.isPresent() && avg.get() < 65) {
                UserDto.Response ud = new UserDto.Response();
                ud.setId(t.getId());
                ud.setFullName(t.getFullName());
                ud.setEmail(t.getEmail());
                ud.setPodName(t.getPodName());
                ud.setLatestScore(avg.get());
                atRisk.add(ud);
            }
        }

        // Pod averages
        Map<String, Double> podAverages = new LinkedHashMap<>();
        List<String> pods = List.of("Pod A", "Pod B", "Pod C", "Pod D", "Pod E", "Pod F");
        for (String pod : pods) {
            Optional<Double> avg = scoreRepo.findAvgScoreByPod(pod);
            podAverages.put(pod, avg.orElse(0.0));
        }

        DashboardDto dto = new DashboardDto();
        dto.setTotalStudents((long) trainees.size());
        dto.setAvgScore(Math.round(classAvg * 10.0) / 10.0);
        dto.setBelowThreshold((long) atRisk.size());
        dto.setActivePods((long) podAverages.values().stream().filter(v -> v > 0).count());
        dto.setPodAverages(podAverages);
        dto.setAtRiskStudents(atRisk);
        dto.setRecentActivity(logService.getRecentActivity(10));
        return dto;
    }

    public DashboardDto getTraineeDashboard(String traineeEmail) {
        User trainee = userRepo.findByEmail(traineeEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found"));

        List<Score> scores = scoreRepo.findByTraineeOrderBySubmittedDateDesc(trainee);

        double overallAvg = scores.stream()
                .mapToDouble(Score::getScore).average().orElse(0);

        long pendingFeedback = scoreRepo.countByTraineeAndFeedbackStatus(trainee, FeedbackStatus.PENDING);

        // Week-by-week trend
        Map<String, Double> weeklyTrend = new LinkedHashMap<>();
        for (Score s : scores) {
            if (s.getWeekLabel() != null && !weeklyTrend.containsKey(s.getWeekLabel())) {
                weeklyTrend.put(s.getWeekLabel(), s.getScore());
            }
        }

        DashboardDto dto = new DashboardDto();
        dto.setOverallProgress(Math.round(overallAvg * 10.0) / 10.0);
        dto.setAssignmentsDone((long) scores.size());
        dto.setTotalAssignments((long) scores.size() + 4); // upcoming slots
        dto.setPendingFeedback(pendingFeedback);
        dto.setWeeklyTrend(weeklyTrend);
        dto.setRecentActivity(logService.getRecentActivity(6));
        return dto;
    }
}
