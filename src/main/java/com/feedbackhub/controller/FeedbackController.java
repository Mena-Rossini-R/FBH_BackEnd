package com.feedbackhub.controller;

import com.feedbackhub.dto.FeedbackDto;
import com.feedbackhub.entity.User;
import com.feedbackhub.repository.UserRepository;
import com.feedbackhub.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired private FeedbackService feedbackService;
    @Autowired private UserRepository userRepo;

    @PostMapping
    public ResponseEntity<FeedbackDto.Response> addMessage(@RequestBody FeedbackDto.Request req,
                                                            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(feedbackService.addMessage(req, ud.getUsername()));
    }

    @GetMapping("/score/{scoreId}")
    public ResponseEntity<List<FeedbackDto.Response>> getThread(@PathVariable Long scoreId,
                                                                  @AuthenticationPrincipal UserDetails ud) {
        User user = userRepo.findByEmail(ud.getUsername()).orElse(null);
        if (user != null && "TRAINER".equals(user.getRole().name())) {
            feedbackService.markReadByTrainer(scoreId);
        } else {
            feedbackService.markReadByTrainee(scoreId, ud.getUsername());
        }
        return ResponseEntity.ok(feedbackService.getThread(scoreId));
    }

    @GetMapping("/unread-counts")
    public ResponseEntity<Map<Long, Long>> getUnreadCountsTrainer(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(feedbackService.getUnreadCountsForTrainer(ud.getUsername()));
    }

    @GetMapping("/unread-trainee-counts")
    public ResponseEntity<Map<Long, Long>> getUnreadCountsTrainee(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(feedbackService.getUnreadCountsForTrainee(ud.getUsername()));
    }
}