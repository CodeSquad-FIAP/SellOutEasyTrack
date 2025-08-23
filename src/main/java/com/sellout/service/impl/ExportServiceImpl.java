package com.sellout.service.impl;

import com.sellout.model.Sale;
import com.sellout.service.ExportService;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExportServiceImpl implements ExportService {

    @Override
    public void exportToCsv(List<Sale> sales, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write header
            writer.write("ID,Product Name,Quantity,Unit Price,Total Value,Sale Date\n");

            // Write data
            for (Sale sale : sales) {
                writer.write(String.format("%d,\"%s\",%d,%.2f,%.2f,%s\n",
                        sale.getId(),
                        sale.getProductName(),
                        sale.getQuantity(),
                        sale.getUnitPrice(),
                        sale.getTotalValue(),
                        sale.getSaleDate().toString()
                ));
            }
        }
    }

    @Override
    public String generateReport(List<Sale> sales) {
        StringBuilder report = new StringBuilder();

        // Header
        report.append("═".repeat(70)).append("\n");
        report.append("                    SELLOUT EASYTRACK SALES REPORT\n");
        report.append("═".repeat(70)).append("\n");
        report.append("Generated: ").append(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n\n");

        if (sales.isEmpty()) {
            report.append("No sales data available.\n");
            return report.toString();
        }

        // Summary statistics
        int totalSales = sales.size();
        BigDecimal totalRevenue = sales.stream()
                .map(Sale::getTotalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal averageTicket = totalRevenue.divide(BigDecimal.valueOf(totalSales), 2, java.math.RoundingMode.HALF_UP);

        report.append("EXECUTIVE SUMMARY:\n");
        report.append("─".repeat(50)).append("\n");
        report.append("• Total sales: ").append(totalSales).append("\n");
        report.append("• Total revenue: R$ ").append(String.format("%.2f", totalRevenue)).append("\n");
        report.append("• Average ticket: R$ ").append(String.format("%.2f", averageTicket)).append("\n\n");

        // Top products by quantity
        Map<String, Integer> productQuantities = sales.stream()
                .collect(Collectors.groupingBy(
                        Sale::getProductName,
                        Collectors.summingInt(Sale::getQuantity)
                ));

        report.append("TOP PRODUCTS BY QUANTITY:\n");
        report.append("─".repeat(50)).append("\n");
        productQuantities.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .forEach(entry -> {
                    BigDecimal totalValue = sales.stream()
                            .filter(s -> s.getProductName().equals(entry.getKey()))
                            .map(Sale::getTotalValue)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    report.append(String.format("• %-30s: %4d units - R$ %8.2f\n",
                            entry.getKey(), entry.getValue(), totalValue));
                });

        // Top products by revenue
        Map<String, BigDecimal> productRevenues = sales.stream()
                .collect(Collectors.groupingBy(
                        Sale::getProductName,
                        Collectors.reducing(BigDecimal.ZERO, Sale::getTotalValue, BigDecimal::add)
                ));

        report.append("\nTOP PRODUCTS BY REVENUE:\n");
        report.append("─".repeat(50)).append("\n");
        productRevenues.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(10)
                .forEach(entry -> {
                    int totalQuantity = sales.stream()
                            .filter(s -> s.getProductName().equals(entry.getKey()))
                            .mapToInt(Sale::getQuantity)
                            .sum();

                    report.append(String.format("• %-30s: R$ %8.2f (%d units)\n",
                            entry.getKey(), entry.getValue(), totalQuantity));
                });

        // Recent sales (last 10)
        report.append("\nRECENT SALES:\n");
        report.append("─".repeat(50)).append("\n");
        sales.stream()
                .sorted((s1, s2) -> s2.getSaleDate().compareTo(s1.getSaleDate()))
                .limit(10)
                .forEach(sale -> {
                    report.append(String.format("• %s: %s - %d x R$ %.2f = R$ %.2f\n",
                            sale.getSaleDate().toString(),
                            sale.getProductName(),
                            sale.getQuantity(),
                            sale.getUnitPrice(),
                            sale.getTotalValue()));
                });

        report.append("\n").append("═".repeat(70)).append("\n");
        report.append("Report generated by SellOut EasyTrack v2.0 - Clean Architecture\n");

        return report.toString();
    }
}