package com.feedbackhub.controller;

import com.feedbackhub.dto.AlertDto;
import com.feedbackhub.service.SkillAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/trainee/alerts")
public class AlertController {

    @Autowired
    private SkillAlertService alertService;

    @GetMapping("/{traineeId}")
    public ResponseEntity<List<AlertDto.Response>> getAlerts(@PathVariable Long traineeId) {
        return ResponseEntity.ok(alertService.getAlertsForTrainee(traineeId));
    }

    @PatchMapping("/{alertId}/acknowledge")
    public ResponseEntity<AlertDto.Response> acknowledge(@PathVariable Long alertId) {
        return ResponseEntity.ok(alertService.acknowledge(alertId));
    }

    @PatchMapping("/{alertId}/resolve")
    public ResponseEntity<AlertDto.Response> resolve(@PathVariable Long alertId) {
        return ResponseEntity.ok(alertService.resolve(alertId));
    }
}
