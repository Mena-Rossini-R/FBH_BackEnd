package com.feedbackhub.service;

import com.feedbackhub.dto.AlertDto;
import com.feedbackhub.entity.SkillAlert;
import com.feedbackhub.entity.User;
import com.feedbackhub.enums.AlertLevel;
import com.feedbackhub.exception.ResourceNotFoundException;
import com.feedbackhub.repository.SkillAlertRepository;
import com.feedbackhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SkillAlertService {

    @Autowired
    private SkillAlertRepository alertRepo;

    @Autowired
    private UserRepository userRepo;

    public void createAlert(User trainee, String skillName, double scoreValue, String trainerEmail) {
        AlertLevel level;
        String message;
        if (scoreValue < 50) {
            level   = AlertLevel.CRITICAL;
            message = "Score " + (int) scoreValue + "% — below 50%. Immediate attention required.";
        } else if (scoreValue < 65) {
            level   = AlertLevel.WARNING;
            message = "Score " + (int) scoreValue + "% — below 65% threshold. Needs improvement.";
        } else {
            level   = AlertLevel.WATCH;
            message = "Score " + (int) scoreValue + "% — approaching threshold. Monitor closely.";
        }

        SkillAlert alert = new SkillAlert();
        alert.setTrainee(trainee);
        alert.setSkillName(skillName);
        alert.setScoreValue(scoreValue);
        alert.setAlertLevel(level);
        alert.setMessage(message);
        alert.setAcknowledged(false);
        alert.setResolved(false);
        alertRepo.save(alert);
    }

    @Transactional(readOnly = true)
    public List<AlertDto.Response> getAlertsForTrainee(Long traineeId) {
        User trainee = userRepo.findById(traineeId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found"));
        List<SkillAlert> alerts = alertRepo.findByTraineeOrderByCreatedAtDesc(trainee);
        List<AlertDto.Response> result = new ArrayList<>();
        for (SkillAlert a : alerts) { result.add(toResponse(a)); }
        return result;
    }

    public AlertDto.Response acknowledge(Long alertId) {
        SkillAlert alert = alertRepo.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found"));
        alert.setAcknowledged(true);
        alert.setAcknowledgedAt(LocalDateTime.now());
        return toResponse(alertRepo.save(alert));
    }

    public AlertDto.Response resolve(Long alertId) {
        SkillAlert alert = alertRepo.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found"));
        alert.setResolved(true);
        return toResponse(alertRepo.save(alert));
    }

    private AlertDto.Response toResponse(SkillAlert a) {
        AlertDto.Response dto = new AlertDto.Response();
        dto.setId(a.getId());
        dto.setSkillName(a.getSkillName());
        dto.setScoreValue(a.getScoreValue());
        dto.setAlertLevel(a.getAlertLevel());
        dto.setMessage(a.getMessage());
        dto.setAcknowledged(a.isAcknowledged());
        dto.setResolved(a.isResolved());
        dto.setCreatedAt(a.getCreatedAt());
        dto.setAcknowledgedAt(a.getAcknowledgedAt());
        return dto;
    }
}
