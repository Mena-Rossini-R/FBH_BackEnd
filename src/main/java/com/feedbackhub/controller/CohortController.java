package com.feedbackhub.controller;

import com.feedbackhub.dto.CohortDto;
import com.feedbackhub.dto.UserDto;
import com.feedbackhub.service.CohortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/trainer/cohort")
public class CohortController {

    @Autowired private CohortService cohortService;

    @PostMapping("/upload")
    public ResponseEntity<CohortDto.UploadResult> uploadCohort(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(cohortService.uploadCohort(file, ud.getUsername()));
    }

    @GetMapping("/trainees")
    public ResponseEntity<List<UserDto.Response>> getTrainees() {
        return ResponseEntity.ok(cohortService.getTrainees());
    }
}
