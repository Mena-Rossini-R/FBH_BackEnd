package com.feedbackhub.service;

import com.feedbackhub.dto.CohortDto;
import com.feedbackhub.dto.UserDto;
import com.feedbackhub.entity.User;
import com.feedbackhub.enums.UserRole;
import com.feedbackhub.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CohortService {

    @Autowired private UserRepository   userRepo;
    @Autowired private PasswordEncoder  encoder;
    @Autowired private ActivityLogService logService;

    // ── Structure ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public CohortDto.Structure getStructure() {
        List<User> all = userRepo.findAllTraineesOrdered();

        // Group by cohort → pod
        Map<String, Map<String, List<User>>> cohortPodMap = new LinkedHashMap<>();
        List<User> unassigned = new ArrayList<>();

        for (User u : all) {
            String cohort = u.getCohortName();
            String pod    = u.getPodName();
            if (cohort == null || cohort.isBlank() || pod == null || pod.isBlank()) {
                unassigned.add(u);
                continue;
            }
            cohortPodMap.computeIfAbsent(cohort, k -> new LinkedHashMap<>())
                        .computeIfAbsent(pod, k -> new ArrayList<>())
                        .add(u);
        }

        List<CohortDto.CohortInfo> cohorts = new ArrayList<>();
        for (Map.Entry<String, Map<String, List<User>>> ce : cohortPodMap.entrySet()) {
            CohortDto.CohortInfo ci = new CohortDto.CohortInfo();
            ci.setCohortName(ce.getKey());
            List<CohortDto.PodInfo> pods = new ArrayList<>();
            int total = 0;
            for (Map.Entry<String, List<User>> pe : ce.getValue().entrySet()) {
                CohortDto.PodInfo pi = new CohortDto.PodInfo();
                pi.setPodName(pe.getKey());
                pi.setStudents(pe.getValue().stream().map(this::toStudentInfo).collect(Collectors.toList()));
                pods.add(pi);
                total += pe.getValue().size();
            }
            ci.setPods(pods);
            ci.setStudentCount(total);
            cohorts.add(ci);
        }

        CohortDto.Structure structure = new CohortDto.Structure();
        structure.setCohorts(cohorts);
        structure.setUnassigned(unassigned.stream().map(this::toStudentInfo).collect(Collectors.toList()));
        return structure;
    }

    // ── Add new trainee ──────────────────────────────────────────────────────

    public CohortDto.StudentInfo addTrainee(CohortDto.AddTraineeRequest req, String trainerEmail) {
        if (req.getEmail() == null || req.getEmail().isBlank())
            throw new IllegalArgumentException("Email is required");
        if (req.getFullName() == null || req.getFullName().isBlank())
            throw new IllegalArgumentException("Full name is required");
        if (userRepo.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Email already exists: " + req.getEmail());

        User u = new User();
        u.setFullName(req.getFullName().trim());
        u.setEmail(req.getEmail().trim().toLowerCase());
        u.setPassword(encoder.encode("password@123"));
        u.setRole(UserRole.TRAINEE);
        u.setCohortName(req.getCohortName());
        u.setPodName(req.getPodName());
        u.setPhone(req.getPhone());
        u.setDepartment(req.getDepartment());
        u.setActive(true);
        u = userRepo.save(u);

        logService.log("TRAINEE_ADDED", "New trainee added: " + u.getFullName()
                + " → " + req.getPodName(), trainerEmail, u.getFullName());
        return toStudentInfo(u);
    }

    // ── Assign (or move) existing student to a pod ───────────────────────────

    public CohortDto.StudentInfo assignStudent(CohortDto.AssignRequest req, String trainerEmail) {
        User u = userRepo.findById(req.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + req.getUserId()));
        String oldPod = u.getPodName();
        u.setPodName(req.getPodName());
        u.setCohortName(req.getCohortName());
        userRepo.save(u);

        logService.log("TRAINEE_ASSIGNED", "Moved " + u.getFullName()
                + " from " + oldPod + " → " + req.getPodName(), trainerEmail, u.getFullName());
        return toStudentInfo(u);
    }

    // ── Remove student from pod (keep account, clear pod/cohort) ─────────────

    public void removeFromPod(Long userId, String trainerEmail) {
        User u = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        String pod = u.getPodName();
        u.setPodName(null);
        u.setCohortName(null);
        userRepo.save(u);
        logService.log("TRAINEE_REMOVED", "Removed " + u.getFullName() + " from pod " + pod,
                trainerEmail, u.getFullName());
    }

    // ── Unassigned trainees ──────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<CohortDto.StudentInfo> getUnassigned() {
        return userRepo.findUnassignedTrainees().stream()
                .map(this::toStudentInfo).collect(Collectors.toList());
    }

    // ── Excel upload ─────────────────────────────────────────────────────────

    public CohortDto.UploadResult uploadCohort(MultipartFile file, String trainerEmail) {
        CohortDto.UploadResult result = new CohortDto.UploadResult();
        List<UserDto.Response> created = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        List<UserDto.Response> existing = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook wb = new XSSFWorkbook(is)) {

            Sheet sheet = wb.getSheetAt(0);
            Map<String, Integer> cols = new HashMap<>();

            int headerRow = 0;
            for (int r = 0; r <= Math.min(2, sheet.getLastRowNum()); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                for (int c = 0; c < row.getLastCellNum(); c++) {
                    String h = cellStr(row, c).toLowerCase();
                    if (h.contains("name"))       cols.put("name",   c);
                    if (h.contains("email"))      cols.put("email",  c);
                    if (h.contains("cohort"))     cols.put("cohort", c);
                    if (h.contains("pod"))        cols.put("pod",    c);
                    if (h.contains("phone") || h.contains("contact")) cols.put("phone", c);
                    if (h.contains("dept") || h.contains("department")) cols.put("dept", c);
                }
                if (cols.containsKey("email")) { headerRow = r; break; }
            }

            if (!cols.containsKey("name"))   cols.put("name",   0);
            if (!cols.containsKey("email"))  cols.put("email",  1);
            if (!cols.containsKey("cohort")) cols.put("cohort", 2);
            if (!cols.containsKey("pod"))    cols.put("pod",    3);

            for (int i = headerRow + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isBlank(row)) continue;
                try {
                    String email = cellStr(row, cols.get("email"));
                    String name  = cellStr(row, cols.get("name"));
                    if (email.isEmpty()) { errors.add("Row " + (i+1) + ": email missing"); continue; }
                    if (name.isEmpty())  { errors.add("Row " + (i+1) + ": name missing");  continue; }

                    if (userRepo.findByEmail(email).isPresent()) {
                        User u = userRepo.findByEmail(email).get();
                        String cohort = cellStr(row, cols.getOrDefault("cohort", -1));
                        String pod    = cellStr(row, cols.getOrDefault("pod",    -1));
                        if (!cohort.isEmpty()) u.setCohortName(cohort);
                        if (!pod.isEmpty())    u.setPodName(pod);
                        userRepo.save(u);
                        existing.add(toUserDto(u));
                        continue;
                    }

                    User u = new User();
                    u.setFullName(name);
                    u.setEmail(email);
                    u.setPassword(encoder.encode("password@123"));
                    u.setRole(UserRole.TRAINEE);
                    u.setCohortName(cellStr(row, cols.getOrDefault("cohort", -1)));
                    u.setPodName(cellStr(row, cols.getOrDefault("pod", -1)));
                    u.setPhone(cellStr(row, cols.getOrDefault("phone", -1)));
                    u.setDepartment(cellStr(row, cols.getOrDefault("dept", -1)));
                    u.setActive(true);
                    u = userRepo.save(u);
                    created.add(toUserDto(u));

                } catch (Exception e) {
                    errors.add("Row " + (i+1) + ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to process file: " + e.getMessage());
        }

        logService.log("COHORT_UPLOAD", "Cohort upload: " + created.size() + " created, "
                + existing.size() + " updated", trainerEmail, "Cohort");

        result.setCreated(created);
        result.setUpdated(existing);
        result.setErrors(errors);
        result.setCreatedCount(created.size());
        result.setUpdatedCount(existing.size());
        result.setErrorCount(errors.size());
        return result;
    }

    @Transactional(readOnly = true)
    public List<UserDto.Response> getTrainees() {
        return userRepo.findByRole(UserRole.TRAINEE).stream()
                .map(this::toUserDto).collect(Collectors.toList());
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private CohortDto.StudentInfo toStudentInfo(User u) {
        CohortDto.StudentInfo s = new CohortDto.StudentInfo();
        s.setId(u.getId()); s.setFullName(u.getFullName());
        s.setEmail(u.getEmail()); s.setPhone(u.getPhone());
        s.setDepartment(u.getDepartment()); return s;
    }

    private String cellStr(Row row, int col) {
        if (col < 0 || row.getCell(col) == null) return "";
        Cell cell = row.getCell(col);
        if (cell.getCellType() == CellType.NUMERIC)
            return String.valueOf((long) cell.getNumericCellValue());
        return cell.toString().trim();
    }

    private boolean isBlank(Row row) {
        for (int c = 0; c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK
                    && !cell.toString().trim().isEmpty()) return false;
        }
        return true;
    }

    private UserDto.Response toUserDto(User u) {
        UserDto.Response dto = new UserDto.Response();
        dto.setId(u.getId()); dto.setFullName(u.getFullName());
        dto.setEmail(u.getEmail()); dto.setRole(u.getRole().name());
        dto.setPodName(u.getPodName()); dto.setCohortName(u.getCohortName());
        dto.setActive(u.isActive()); return dto;
    }
}

