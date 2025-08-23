package com.sellout.service;

import com.sellout.model.Sale;
import java.io.IOException;
import java.util.List;

public interface ExportService {
    void exportToCsv(List<Sale> sales, String filePath) throws IOException;
    String generateReport(List<Sale> sales);
}