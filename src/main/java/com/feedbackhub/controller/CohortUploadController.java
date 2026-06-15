package com.feedbackhub.controller;

import com.feedbackhub.dto.CohortDto;
import com.feedbackhub.service.CohortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/cohort")
public class CohortUploadController {

    @Autowired
    private CohortService cohortService;

    @PostMapping("/upload")
    public ResponseEntity<CohortDto.UploadResult> uploadCohort(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails ud) {
        CohortDto.UploadResult result = cohortService.uploadCohort(file, ud.getUsername());
        return ResponseEntity.ok(result);
    }
}