package com.feedbackhub.controller;

import com.feedbackhub.dto.AgentDto;
import com.feedbackhub.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trainer/agent")
public class AgentController {

    @Autowired private AgentService agentService;

    /**
     * GET /trainer/agent/consolidated-report
     * Returns AI-generated consolidated feedback report for all trainees.
     * Only TRAINER role can access (secured by /trainer/** in SecurityConfig).
     */
    @GetMapping("/consolidated-report")
    public ResponseEntity<AgentDto.ConsolidatedReport> getConsolidatedReport(
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(agentService.generateConsolidatedReport(ud.getUsername()));
    }
}
