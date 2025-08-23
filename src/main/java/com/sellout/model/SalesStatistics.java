package com.sellout.model;

import java.math.BigDecimal;
import java.util.Map;

public class SalesStatistics {
    private final int totalSalesCount;
    private final BigDecimal totalRevenue;
    private final BigDecimal averageTicket;
    private final Map<String, Integer> topProductsByQuantity;

    public SalesStatistics(int totalSalesCount, BigDecimal totalRevenue,
                           BigDecimal averageTicket, Map<String, Integer> topProductsByQuantity) {
        this.totalSalesCount = totalSalesCount;
        this.totalRevenue = totalRevenue;
        this.averageTicket = averageTicket;
        this.topProductsByQuantity = Map.copyOf(topProductsByQuantity);
    }

    // Getters
    public int getTotalSalesCount() { return totalSalesCount; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public BigDecimal getAverageTicket() { return averageTicket; }
    public Map<String, Integer> getTopProductsByQuantity() { return topProductsByQuantity; }
}