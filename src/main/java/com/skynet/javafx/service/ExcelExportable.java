package com.skynet.javafx.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ExcelExportable {
    String getSheetName();
    List<String> getHeaders();
    List<Map<String, Object>> getExportData();
    void exportToExcel(String filePath) throws IOException;
}
