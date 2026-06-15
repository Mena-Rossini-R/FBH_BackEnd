package com.feedbackhub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class AiFeedbackController {
    @GetMapping("/feedback/{scoreId}")
    public ResponseEntity<String> getAiFeedback(@PathVariable Long scoreId) {
        // TODO: Integrate AI feedback logic
        // Example: Return mock feedback
        return ResponseEntity.ok("Great improvement! Keep up the good work.");
    }

    @GetMapping("/skills-gap/{traineeId}")
    public ResponseEntity<String> getSkillsGap(@PathVariable Long traineeId) {
        // TODO: Integrate AI skills gap logic
        // Example: Return mock skills gap
        return ResponseEntity.ok("Needs improvement in Algebra and Geometry.");
    }
}
