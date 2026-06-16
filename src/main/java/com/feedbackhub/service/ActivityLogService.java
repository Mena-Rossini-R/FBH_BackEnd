package com.feedbackhub.service;

import com.feedbackhub.dto.ActivityLogDto;
import com.feedbackhub.entity.ActivityLog;
import com.feedbackhub.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActivityLogService {

    @Autowired
    private ActivityLogRepository repo;

    public void log(String type, String description, String performedBy, String targetEntity) {
        repo.save(new ActivityLog(type, description, performedBy, targetEntity));
    }

    public List<ActivityLogDto> getRecentActivity(int limit) {
        List<ActivityLog> logs = repo.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit));
        return toDto(logs);
    }

    public List<ActivityLogDto> getRecentActivityForTrainee(String traineeName, int limit) {
        List<ActivityLog> logs = repo.findByTargetEntityOrderByCreatedAtDesc(traineeName, PageRequest.of(0, limit));
        return toDto(logs);
    }

    private List<ActivityLogDto> toDto(List<ActivityLog> logs) {
        List<ActivityLogDto> result = new ArrayList<>();
        for (ActivityLog a : logs) {
            ActivityLogDto dto = new ActivityLogDto();
            dto.setId(a.getId());
            dto.setActivityType(a.getActivityType());
            dto.setDescription(a.getDescription());
            dto.setPerformedBy(a.getPerformedBy());
            dto.setTargetEntity(a.getTargetEntity());
            dto.setCreatedAt(a.getCreatedAt());
            result.add(dto);
        }
        return result;
    }
}
