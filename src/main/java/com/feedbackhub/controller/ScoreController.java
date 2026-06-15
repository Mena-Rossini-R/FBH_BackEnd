package com.feedbackhub.controller;

import com.feedbackhub.dto.ScoreDto;
import com.feedbackhub.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/scores")
public class ScoreController {

    @Autowired private ScoreService scoreService;

    @PostMapping
    public ResponseEntity<ScoreDto.Response> create(
            @RequestBody ScoreDto.Request req,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(scoreService.createScore(req, ud.getUsername()));
    }

    @GetMapping("/trainee/{traineeId}")
    public ResponseEntity<List<ScoreDto.Response>> getByTrainee(@PathVariable Long traineeId) {
        return ResponseEntity.ok(scoreService.getScoresByTrainee(traineeId));
    }

    @GetMapping("/trainer")
    public ResponseEntity<List<ScoreDto.Response>> getByTrainer(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(scoreService.getScoresByTrainer(ud.getUsername()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ScoreDto.Response>> getAll() {
        return ResponseEntity.ok(scoreService.getAllScores());
    }

    // ── Bulk upload — returns full result with errors list ─────────────────
    @PostMapping("/bulk-upload")
    public ResponseEntity<ScoreDto.BulkUploadResult> bulkUpload(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(scoreService.bulkUpload(file, ud.getUsername()));
    }

    // ── AI Feedback per score ──────────────────────────────────────────────
    @GetMapping("/{scoreId}/ai-feedback")
    public ResponseEntity<Map<String, String>> getAiFeedback(@PathVariable Long scoreId) {
        String feedback = scoreService.generateAIFeedback(scoreId);
        return ResponseEntity.ok(Map.of("feedback", feedback));
    }

    // ── Skills gap analysis for a trainee ─────────────────────────────────
    @GetMapping("/trainee/{traineeId}/skills-gap")
    public ResponseEntity<Map<String, String>> getSkillsGap(@PathVariable Long traineeId) {
        String analysis = scoreService.generateSkillsGapAnalysis(traineeId);
        return ResponseEntity.ok(Map.of("analysis", analysis));
    }
}
