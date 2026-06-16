package com.feedbackhub.service;

import com.feedbackhub.dto.FeedbackDto;
import com.feedbackhub.entity.FeedbackThread;
import com.feedbackhub.entity.Score;
import com.feedbackhub.entity.User;
import com.feedbackhub.enums.FeedbackStatus;
import com.feedbackhub.exception.ResourceNotFoundException;
import com.feedbackhub.repository.FeedbackThreadRepository;
import com.feedbackhub.repository.ScoreRepository;
import com.feedbackhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class FeedbackService {

    @Autowired private FeedbackThreadRepository threadRepo;
    @Autowired private ScoreRepository scoreRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private ActivityLogService logService;

    public FeedbackDto.Response addMessage(FeedbackDto.Request req, String senderEmail) {
        Score score = scoreRepo.findById(req.getScoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Score not found"));
        User sender = userRepo.findByEmail(senderEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isTrainer = sender.getRole().name().equals("TRAINER");

        FeedbackThread thread = new FeedbackThread();
        thread.setScore(score);
        thread.setSender(sender);
        thread.setMessage(req.getMessage());
        thread.setSenderRole(sender.getRole().name());
        thread.setReadByTrainee(!isTrainer); // trainee has already read their own message
        thread.setReadByTrainer(isTrainer);  // trainer has already read their own message
        thread = threadRepo.save(thread);

        if (!isTrainer) {
            score.setFeedbackStatus(FeedbackStatus.VIEWED);
            scoreRepo.save(score);
        }

        logService.log("FEEDBACK_GIVEN",
                sender.getFullName() + " added feedback on " + score.getAssignmentName(),
                senderEmail, score.getTrainee().getFullName());

        return toResponse(thread);
    }

    @Transactional(readOnly = true)
    public List<FeedbackDto.Response> getThread(Long scoreId) {
        List<FeedbackThread> threads = threadRepo.findByScoreIdOrderByCreatedAtAsc(scoreId);
        List<FeedbackDto.Response> result = new ArrayList<>();
        for (FeedbackThread t : threads) { result.add(toResponse(t)); }
        return result;
    }

    public void markReadByTrainee(Long scoreId, String traineeEmail) {
        Score score = scoreRepo.findById(scoreId)
                .orElseThrow(() -> new ResourceNotFoundException("Score not found"));
        score.setFeedbackStatus(FeedbackStatus.VIEWED);
        scoreRepo.save(score);
        threadRepo.markReadByTraineeForScore(scoreId);
        logService.log("FEEDBACK_VIEWED",
                traineeEmail + " viewed feedback for " + score.getAssignmentName(),
                traineeEmail, score.getTrainee().getFullName());
    }

    public void markReadByTrainer(Long scoreId) {
        threadRepo.markReadByTrainerForScore(scoreId);
    }

    @Transactional(readOnly = true)
    public Map<Long, Long> getUnreadCountsForTrainer(String trainerEmail) {
        List<Object[]> rows = threadRepo.countUnreadForTrainer(trainerEmail);
        Map<Long, Long> result = new HashMap<>();
        for (Object[] row : rows) { result.put((Long) row[0], (Long) row[1]); }
        return result;
    }

    @Transactional(readOnly = true)
    public Map<Long, Long> getUnreadCountsForTrainee(String traineeEmail) {
        List<Object[]> rows = threadRepo.countUnreadForTrainee(traineeEmail);
        Map<Long, Long> result = new HashMap<>();
        for (Object[] row : rows) { result.put((Long) row[0], (Long) row[1]); }
        return result;
    }

    private FeedbackDto.Response toResponse(FeedbackThread t) {
        FeedbackDto.Response dto = new FeedbackDto.Response();
        dto.setId(t.getId());
        dto.setScoreId(t.getScore().getId());
        dto.setAssignmentName(t.getScore().getAssignmentName());
        dto.setSenderName(t.getSender().getFullName());
        dto.setSenderRole(t.getSenderRole());
        dto.setMessage(t.getMessage());
        dto.setReadByTrainee(t.isReadByTrainee());
        dto.setReadByTrainer(t.isReadByTrainer());
        dto.setCreatedAt(t.getCreatedAt());
        return dto;
    }
}