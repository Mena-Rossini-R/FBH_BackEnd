package com.feedbackhub.controller;

import com.feedbackhub.dto.DashboardDto;
import com.feedbackhub.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/trainer/dashboard")
    public ResponseEntity<DashboardDto> trainerDashboard(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(dashboardService.getTrainerDashboard(ud.getUsername()));
    }

    @GetMapping("/trainee/dashboard")
    public ResponseEntity<DashboardDto> traineeDashboard(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(dashboardService.getTraineeDashboard(ud.getUsername()));
    }
}
