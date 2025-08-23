package com.sellout.controller;

import com.sellout.model.Sale;
import com.sellout.service.ChartService;

import java.util.List;

public class ChartController {
    private final ChartService chartService;

    public ChartController(ChartService chartService) {
        this.chartService = chartService;
    }

    public String generateChart(List<Sale> sales) {
        if (sales == null || sales.isEmpty()) {
            throw new IllegalArgumentException("Sales data is required to generate chart");
        }

        try {
            return chartService.generateSalesChart(sales);
        } catch (Exception e) {
            throw new ControllerException("Failed to generate chart: " + e.getMessage(), e);
        }
    }

    public boolean isChartGenerationAvailable() {
        return chartService.isChartGenerationAvailable();
    }

    public static class ControllerException extends RuntimeException {
        public ControllerException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}