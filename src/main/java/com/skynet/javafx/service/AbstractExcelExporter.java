package com.skynet.javafx.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class AbstractExcelExporter implements ExcelExportable {

    @Override
    public void exportToExcel(String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(getSheetName());
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            List<String> headers = getHeaders();
            for (int i = 0; i < headers.size(); i++) {
                headerRow.createCell(i).setCellValue(headers.get(i));
            }
            
            // Create data rows
            List<Map<String, Object>> data = getExportData();
            int rowNum = 1;
            for (Map<String, Object> rowData : data) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = row.createCell(i);
                    Object value = rowData.get(headers.get(i));
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
        }
    }
}
