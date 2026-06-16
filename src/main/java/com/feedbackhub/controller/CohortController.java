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

    /** Full cohort → pod → student structure */
    @GetMapping("/structure")
    public ResponseEntity<CohortDto.Structure> getStructure() {
        return ResponseEntity.ok(cohortService.getStructure());
    }

    /** Trainees with no pod assigned */
    @GetMapping("/unassigned")
    public ResponseEntity<List<CohortDto.StudentInfo>> getUnassigned() {
        return ResponseEntity.ok(cohortService.getUnassigned());
    }

    /** Create a brand-new trainee and place them in a pod */
    @PostMapping("/add-trainee")
    public ResponseEntity<CohortDto.StudentInfo> addTrainee(
            @RequestBody CohortDto.AddTraineeRequest req,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(cohortService.addTrainee(req, ud.getUsername()));
    }

    /** Move an existing trainee to a different pod / cohort */
    @PutMapping("/assign")
    public ResponseEntity<CohortDto.StudentInfo> assign(
            @RequestBody CohortDto.AssignRequest req,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(cohortService.assignStudent(req, ud.getUsername()));
    }

    /** Remove a trainee from their pod (keeps account, clears pod/cohort) */
    @DeleteMapping("/remove/{userId}")
    public ResponseEntity<Void> removeFromPod(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails ud) {
        cohortService.removeFromPod(userId, ud.getUsername());
        return ResponseEntity.ok().build();
    }

    /** Excel bulk upload */
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
