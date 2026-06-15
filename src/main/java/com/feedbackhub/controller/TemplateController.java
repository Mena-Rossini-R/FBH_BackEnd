package com.feedbackhub.controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/template")
public class TemplateController {

    @GetMapping("/score-upload")
    public ResponseEntity<byte[]> downloadScoreTemplate() {
        try {
            byte[] file = buildExcel(
                new String[]{"name", "email", "category", "assignment", "score", "grade", "weekLabel"},
                new String[]{"Ravi Kumar", "ravi@fh.com", "Technical", "Sprint Review W3", "78", "B+", "W3"}
            );
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=score-upload-template.xlsx")
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .body(file);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/cohort-upload")
    public ResponseEntity<byte[]> downloadCohortTemplate() {
        try {
            byte[] file = buildExcel(
                new String[]{"name", "email", "cohort", "pod", "phone", "department"},
                new String[]{"Ravi Kumar", "ravi@company.com", "Cohort 12", "Pod A", "9876543210", "Engineering"}
            );
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=cohort-upload-template.xlsx")
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .body(file);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private byte[] buildExcel(String[] headers, String[] sampleRow) throws Exception {
        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Template");

            // Header style — bold + background
            CellStyle headerStyle = wb.createCellStyle();
            Font font = wb.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }

            // Sample data row
            Row dataRow = sheet.createRow(1);
            for (int i = 0; i < sampleRow.length; i++) {
                dataRow.createCell(i).setCellValue(sampleRow[i]);
            }

            wb.write(out);
            return out.toByteArray();
        }
    }
}