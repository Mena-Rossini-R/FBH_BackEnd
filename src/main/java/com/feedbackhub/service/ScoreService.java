package com.feedbackhub.service;

import com.feedbackhub.dto.ScoreDto;
import com.feedbackhub.entity.Score;
import com.feedbackhub.entity.User;
import com.feedbackhub.enums.AlertLevel;
import com.feedbackhub.enums.FeedbackStatus;
import com.feedbackhub.enums.TrendDirection;
import com.feedbackhub.exception.ResourceNotFoundException;
import com.feedbackhub.repository.ScoreRepository;
import com.feedbackhub.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class ScoreService {

    @Autowired private ScoreRepository   scoreRepo;
    @Autowired private UserRepository    userRepo;
    @Autowired private ActivityLogService logService;
    @Autowired private SkillAlertService alertService;

    public ScoreDto.Response createScore(ScoreDto.Request req, String trainerEmail) {
        User trainee = userRepo.findById(req.getTraineeId())
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found"));
        User trainer = userRepo.findByEmail(trainerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));

        Score score = new Score();
        score.setTrainee(trainee);
        score.setTrainer(trainer);
        score.setAssignmentName(req.getAssignmentName());
        score.setCategory(req.getCategory());
        score.setScore(req.getScore());
        score.setGrade(req.getGrade() != null ? req.getGrade() : computeGrade(req.getScore()));
        score.setSubmittedDate(req.getSubmittedDate() != null ? req.getSubmittedDate() : LocalDate.now());
        score.setWeekLabel(req.getWeekLabel());
        score.setFeedbackStatus(FeedbackStatus.PENDING);
        score.setTrend(computeTrend(trainee, req.getScore()));
        score = scoreRepo.save(score);

        logService.log("SCORE_UPLOADED", "Score uploaded for " + trainee.getFullName(), trainerEmail, trainee.getFullName());
        if (req.getScore() < 65) {
            alertService.createAlert(trainee, req.getAssignmentName(), req.getScore(), trainerEmail);
        }
        return toResponse(score);
    }

    @Transactional(readOnly = true)
    public List<ScoreDto.Response> getScoresByTrainee(Long traineeId) {
        User trainee = userRepo.findById(traineeId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found"));
        List<Score> scores = scoreRepo.findByTraineeOrderBySubmittedDateDesc(trainee);
        List<ScoreDto.Response> result = new ArrayList<>();
        for (Score s : scores) result.add(toResponse(s));
        return result;
    }

    @Transactional(readOnly = true)
    public List<ScoreDto.Response> getScoresByTrainer(String trainerEmail) {
        User trainer = userRepo.findByEmail(trainerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));
        List<Score> scores = scoreRepo.findByTrainerOrderBySubmittedDateDesc(trainer);
        List<ScoreDto.Response> result = new ArrayList<>();
        for (Score s : scores) result.add(toResponse(s));
        return result;
    }

    @Transactional(readOnly = true)
    public List<ScoreDto.Response> getAllScores() {
        List<Score> scores = scoreRepo.findAll();
        List<ScoreDto.Response> result = new ArrayList<>();
        for (Score s : scores) result.add(toResponse(s));
        return result;
    }

    /**
     * FIXED Bulk upload — flexible column detection
     * Supported formats:
     *   A) email, assignment, category, score, [grade], [weekLabel]
     *   B) fullName, email, assignment, category, score, [grade], [weekLabel]
     *   Header row auto-detected from column names
     */
    public ScoreDto.BulkUploadResult bulkUpload(MultipartFile file, String trainerEmail) {
        ScoreDto.BulkUploadResult result = new ScoreDto.BulkUploadResult();
        List<ScoreDto.Response> uploaded  = new ArrayList<>();
        List<String> errors               = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            int headerRow = 0;

            // Find header row — scan first 3 rows
            Map<String, Integer> colIndex = new HashMap<>();
            for (int r = 0; r <= Math.min(2, sheet.getLastRowNum()); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                for (int c = 0; c < row.getLastCellNum(); c++) {
                    String hdr = getCellStr(row, c).toLowerCase().trim();
                    if (hdr.contains("email"))       colIndex.put("email",      c);
                    if (hdr.contains("name"))        colIndex.put("name",       c);
                    if (hdr.contains("assignment"))  colIndex.put("assignment", c);
                    if (hdr.contains("category") || hdr.contains("subject")) colIndex.put("category", c);
                    if (hdr.contains("score") || hdr.contains("mark"))       colIndex.put("score",    c);
                    if (hdr.contains("grade"))       colIndex.put("grade",      c);
                    if (hdr.contains("week"))        colIndex.put("week",       c);
                    if (hdr.contains("date"))        colIndex.put("date",       c);
                }
                if (colIndex.containsKey("email") || colIndex.containsKey("score")) {
                    headerRow = r; break;
                }
            }

            // Fallback positional mapping if no headers found
            // Default: col 0=name, col 1=email, col 2=category, col 3=assignment, col 4=score, col 5=grade
            if (!colIndex.containsKey("email"))      colIndex.put("email",      1);
            if (!colIndex.containsKey("assignment")) colIndex.put("assignment", 3);
            if (!colIndex.containsKey("category"))   colIndex.put("category",   2);
            if (!colIndex.containsKey("score"))      colIndex.put("score",      4);
            if (!colIndex.containsKey("grade"))      colIndex.put("grade",      5);
            if (!colIndex.containsKey("week"))       colIndex.put("week",       6);

            System.out.println("[BulkUpload] Column mapping: " + colIndex);

            for (int i = headerRow + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                try {
                    String email = getCellStr(row, colIndex.get("email"));
                    if (email.isEmpty()) { errors.add("Row " + (i+1) + ": email missing"); continue; }

                    String scoreStr = getCellStr(row, colIndex.get("score"));
                    if (scoreStr.isEmpty()) { errors.add("Row " + (i+1) + ": score missing for " + email); continue; }

                    double scoreVal;
                    try {
                        scoreVal = Double.parseDouble(scoreStr.replace("%", "").trim());
                    } catch (NumberFormatException e) {
                        errors.add("Row " + (i+1) + ": invalid score '" + scoreStr + "' for " + email);
                        continue;
                    }

                    User trainee = userRepo.findByEmail(email).orElse(null);
                    if (trainee == null) {
                        errors.add("Row " + (i+1) + ": student not found: " + email);
                        continue;
                    }

                    String assignment = getCellStr(row, colIndex.get("assignment"));
                    if (assignment.isEmpty()) assignment = "Assignment " + i;

                    ScoreDto.Request req = new ScoreDto.Request();
                    req.setTraineeId(trainee.getId());
                    req.setAssignmentName(assignment);
                    req.setCategory(getCellStr(row, colIndex.get("category")));
                    req.setScore(scoreVal);
                    req.setGrade(getCellStr(row, colIndex.get("grade")));
                    req.setWeekLabel(getCellStr(row, colIndex.get("week")));
                    req.setSubmittedDate(LocalDate.now());
                    uploaded.add(createScore(req, trainerEmail));

                } catch (Exception e) {
                    errors.add("Row " + (i+1) + ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to process file: " + e.getMessage());
        }

        logService.log("BULK_UPLOAD", "Bulk upload: " + uploaded.size() + " scores", trainerEmail, "Batch");
        result.setUploaded(uploaded);
        result.setErrors(errors);
        result.setSuccessCount(uploaded.size());
        result.setErrorCount(errors.size());
        return result;
    }

    // ── AI Feedback Generation (rule-based, no API key) ──────────────────────
    public String generateAIFeedback(Long scoreId) {
        Score score = scoreRepo.findById(scoreId)
                .orElseThrow(() -> new ResourceNotFoundException("Score not found"));
        return buildFeedbackText(score);
    }

    public String generateSkillsGapAnalysis(Long traineeId) {
        User trainee = userRepo.findById(traineeId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found"));
        List<Score> scores = scoreRepo.findByTraineeOrderBySubmittedDateDesc(trainee);
        return buildSkillsGapText(trainee, scores);
    }

    private String buildFeedbackText(Score score) {
        double s = score.getScore();
        String name       = score.getTrainee().getFullName();
        String assignment = score.getAssignmentName();
        String category   = score.getCategory() != null ? score.getCategory() : "General";
        String trend      = score.getTrend() != null ? score.getTrend().name() : "STABLE";

        StringBuilder sb = new StringBuilder();
        sb.append("**AI Feedback for ").append(name).append(" — ").append(assignment).append("**\n\n");

        if (s >= 85) {
            sb.append("🌟 **Excellent Performance!** Score: ").append(s).append("%\n\n");
            sb.append("You have demonstrated strong understanding of ").append(category).append(". ");
            sb.append("Your work shows clear conceptual grasp and practical application. ");
            sb.append("Keep maintaining this standard as topics become more complex.\n\n");
            sb.append("**Strengths identified:** Consistent accuracy, well-structured approach, good time management.\n\n");
            sb.append("**Suggestion:** Challenge yourself with advanced problems in ").append(category).append(" to stay ahead.");
        } else if (s >= 75) {
            sb.append("✅ **Good Work!** Score: ").append(s).append("%\n\n");
            sb.append("You have a solid understanding of ").append(category).append(". ");
            sb.append("Minor gaps exist that can be addressed with focused revision.\n\n");
            sb.append("**Areas to improve:** Review edge cases and best practices in ").append(category).append(".\n\n");
            sb.append("**Next step:** Practice 2–3 additional exercises in weak areas before the next assessment.");
        } else if (s >= 65) {
            sb.append("⚠️ **Needs Improvement.** Score: ").append(s).append("%\n\n");
            sb.append("You are on track but need additional focus on ").append(category).append(". ");
            sb.append("The score indicates partial understanding — some core concepts need revision.\n\n");
            sb.append("**Recommended action:** Re-study the core fundamentals of ").append(category).append(".\n");
            sb.append("Schedule a doubt-clearing session with your trainer this week.\n\n");
            sb.append("**Resources:** Review module notes, attempt practice quizzes, and revisit failed test cases.");
        } else {
            sb.append("🚨 **Critical — Immediate Attention Required.** Score: ").append(s).append("%\n\n");
            sb.append("This score in ").append(category).append(" is significantly below the expected threshold. ");
            sb.append("A targeted intervention plan is recommended.\n\n");
            sb.append("**Action plan:**\n");
            sb.append("1. One-on-one session with trainer this week\n");
            sb.append("2. Re-attempt the fundamentals module for ").append(category).append("\n");
            sb.append("3. Daily practice exercises (30 min minimum)\n");
            sb.append("4. Peer learning session with a higher-scoring student\n\n");
            sb.append("**Trainer note:** This student needs extra support. Please check in daily.");
        }

        if ("DOWN".equals(trend)) {
            sb.append("\n\n📉 **Trend Alert:** Scores have been declining. Please identify the root cause and address it proactively.");
        } else if ("UP".equals(trend)) {
            sb.append("\n\n📈 **Positive Trend:** Great improvement noticed! Keep up the momentum.");
        }

        return sb.toString();
    }

    private String buildSkillsGapText(User trainee, List<Score> scores) {
        if (scores.isEmpty()) {
            return "No scores recorded yet for " + trainee.getFullName() + ". Upload scores to generate analysis.";
        }

        // Group by category
        Map<String, List<Double>> byCategory = new LinkedHashMap<>();
        for (Score s : scores) {
            String cat = s.getCategory() != null ? s.getCategory() : "General";
            byCategory.computeIfAbsent(cat, k -> new ArrayList<>()).add(s.getScore());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("**Skills Gap Analysis — ").append(trainee.getFullName()).append("**\n\n");

        double overall = scores.stream().mapToDouble(Score::getScore).average().orElse(0);
        sb.append("📊 **Overall Average:** ").append(String.format("%.1f", overall)).append("%\n\n");

        // Sort by avg score ascending — weakest first
        List<Map.Entry<String, List<Double>>> sorted = new ArrayList<>(byCategory.entrySet());
        sorted.sort((a, b) -> {
            double avgA = a.getValue().stream().mapToDouble(d->d).average().orElse(0);
            double avgB = b.getValue().stream().mapToDouble(d->d).average().orElse(0);
            return Double.compare(avgA, avgB);
        });

        sb.append("**Category Breakdown (weakest first):**\n");
        for (Map.Entry<String, List<Double>> entry : sorted) {
            double avg = entry.getValue().stream().mapToDouble(d->d).average().orElse(0);
            String icon = avg >= 75 ? "✅" : avg >= 65 ? "⚠️" : "🚨";
            sb.append(icon).append(" **").append(entry.getKey()).append(":** ")
              .append(String.format("%.1f", avg)).append("% (")
              .append(entry.getValue().size()).append(" assessment").append(entry.getValue().size() > 1 ? "s" : "").append(")\n");
        }

        // Identify weakest areas
        List<String> critical = new ArrayList<>(), warning = new ArrayList<>(), strong = new ArrayList<>();
        for (Map.Entry<String, List<Double>> entry : byCategory.entrySet()) {
            double avg = entry.getValue().stream().mapToDouble(d->d).average().orElse(0);
            if (avg < 65) critical.add(entry.getKey());
            else if (avg < 75) warning.add(entry.getKey());
            else strong.add(entry.getKey());
        }

        sb.append("\n**🚨 Critical gaps (below 65%):** ");
        sb.append(critical.isEmpty() ? "None — good job!\n" : String.join(", ", critical) + "\n");

        sb.append("**⚠️ Needs attention (65–74%):** ");
        sb.append(warning.isEmpty() ? "None\n" : String.join(", ", warning) + "\n");

        sb.append("**✅ Strong areas (75%+):** ");
        sb.append(strong.isEmpty() ? "None yet\n" : String.join(", ", strong) + "\n");

        // Trend analysis
        if (scores.size() >= 3) {
            List<Score> recent = scores.subList(0, Math.min(3, scores.size()));
            double recentAvg = recent.stream().mapToDouble(Score::getScore).average().orElse(0);
            if (recentAvg < overall - 5) {
                sb.append("\n📉 **Trend:** Recent scores are **declining**. Immediate review recommended.");
            } else if (recentAvg > overall + 5) {
                sb.append("\n📈 **Trend:** Recent scores show **improvement**. Keep up the momentum!");
            } else {
                sb.append("\n📊 **Trend:** Performance is **stable**. Focus on weak areas to improve.");
            }
        }

        if (!critical.isEmpty()) {
            sb.append("\n\n**Recommended Action Plan:**\n");
            for (String cat : critical) {
                sb.append("• Re-study fundamentals of **").append(cat).append("** — schedule trainer session\n");
            }
        }

        return sb.toString();
    }

    private String computeGrade(double score) {
        if (score >= 90) return "A+";
        if (score >= 85) return "A";
        if (score >= 80) return "B+";
        if (score >= 75) return "B";
        if (score >= 70) return "B-";
        if (score >= 65) return "C+";
        if (score >= 60) return "C";
        return "D";
    }

    private String getCellStr(Row row, int col) {
        if (col < 0 || row.getCell(col) == null) return "";
        Cell cell = row.getCell(col);
        if (cell.getCellType() == CellType.NUMERIC) {
            double v = cell.getNumericCellValue();
            if (v == Math.floor(v)) return String.valueOf((long) v);
            return String.valueOf(v);
        }
        return cell.toString().trim();
    }

    private boolean isRowEmpty(Row row) {
        for (int c = 0; c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK
                    && !cell.toString().trim().isEmpty()) return false;
        }
        return true;
    }

    private TrendDirection computeTrend(User trainee, double newScore) {
        List<Score> history = scoreRepo.findByTraineeOrderBySubmittedDateDesc(trainee,
                org.springframework.data.domain.PageRequest.of(0, 1));
        if (history.isEmpty()) return TrendDirection.STABLE;
        double prev = history.get(0).getScore();
        if (newScore > prev + 2)  return TrendDirection.UP;
        if (newScore < prev - 2)  return TrendDirection.DOWN;
        return TrendDirection.STABLE;
    }

    public ScoreDto.Response toResponse(Score s) {
        ScoreDto.Response dto = new ScoreDto.Response();
        dto.setId(s.getId());
        dto.setTraineeName(s.getTrainee().getFullName());
        dto.setTraineeEmail(s.getTrainee().getEmail());
        dto.setPodName(s.getTrainee().getPodName());
        dto.setTrainerName(s.getTrainer().getFullName());
        dto.setAssignmentName(s.getAssignmentName());
        dto.setCategory(s.getCategory());
        dto.setScore(s.getScore());
        dto.setGrade(s.getGrade());
        dto.setSubmittedDate(s.getSubmittedDate());
        dto.setFeedbackStatus(s.getFeedbackStatus());
        dto.setTrend(s.getTrend());
        dto.setWeekLabel(s.getWeekLabel());
        dto.setCreatedAt(s.getCreatedAt());
        return dto;
    }
}
