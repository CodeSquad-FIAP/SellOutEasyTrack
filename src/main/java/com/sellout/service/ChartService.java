package com.sellout.service;

import com.sellout.model.Sale;
import java.util.List;

public interface ChartService {
    String generateSalesChart(List<Sale> sales);
    boolean isChartGenerationAvailable();
}