package com.feedbackhub.controller;

import com.feedbackhub.dto.FeedbackDto;
import com.feedbackhub.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<FeedbackDto.Response> addMessage(@RequestBody FeedbackDto.Request req,
                                                            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(feedbackService.addMessage(req, ud.getUsername()));
    }

    @GetMapping("/score/{scoreId}")
    public ResponseEntity<List<FeedbackDto.Response>> getThread(@PathVariable Long scoreId,
                                                                  @AuthenticationPrincipal UserDetails ud) {
        feedbackService.markViewed(scoreId, ud.getUsername());
        return ResponseEntity.ok(feedbackService.getThread(scoreId));
    }
}
