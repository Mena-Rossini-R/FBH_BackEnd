package com.feedbackhub.service;

import com.feedbackhub.dto.CohortDto;
import com.feedbackhub.dto.UserDto;
import com.feedbackhub.entity.CohortUploadRecord;
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

@Service
@Transactional
public class CohortService {

    @Autowired private UserRepository   userRepo;
    @Autowired private PasswordEncoder  encoder;
    @Autowired private ActivityLogService logService;

    /**
     * Upload Excel → create trainee accounts → assign to cohort/pod
     *
     * Expected columns (any order, detected by header):
     * name/fullName, email, cohort, pod, [phone/contact], [department]
     */
    public CohortDto.UploadResult uploadCohort(MultipartFile file, String trainerEmail) {
        CohortDto.UploadResult result = new CohortDto.UploadResult();
        List<UserDto.Response> created = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        List<UserDto.Response> existing = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook wb = new XSSFWorkbook(is)) {

            Sheet sheet = wb.getSheetAt(0);
            Map<String, Integer> cols = new HashMap<>();

            // Detect header row
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

            // Fallback positional
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

                    // Check if already exists
                    if (userRepo.findByEmail(email).isPresent()) {
                        User u = userRepo.findByEmail(email).get();
                        // Update cohort/pod if given
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
                    u.setPassword(encoder.encode("password")); // default password
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

    // Get all trainees grouped by cohort/pod
    @Transactional(readOnly = true)
    public List<UserDto.Response> getTrainees() {
        List<User> users = userRepo.findByRole(UserRole.TRAINEE);
        List<UserDto.Response> result = new ArrayList<>();
        for (User u : users) result.add(toUserDto(u));
        return result;
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
        dto.setId(u.getId());
        dto.setFullName(u.getFullName());
        dto.setEmail(u.getEmail());
        dto.setRole(u.getRole().name());
        dto.setPodName(u.getPodName());
        dto.setCohortName(u.getCohortName());
        dto.setActive(u.isActive());
        return dto;
    }
}
