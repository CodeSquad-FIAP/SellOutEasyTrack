package com.sellout.service.impl;

import com.sellout.model.Sale;
import com.sellout.service.ChartService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChartServiceImpl implements ChartService {

    private static final String R_SCRIPT_PATH = "temp_chart_script.R";
    private static final String CSV_DATA_PATH = "temp_sales_data.csv";
    private static final String OUTPUT_IMAGE_PATH = "sales_chart.png";

    @Override
    public String generateSalesChart(List<Sale> sales) {
        if (!isChartGenerationAvailable()) {
            return null;
        }

        try {
            Map<String, Integer> salesData = aggregateSalesByProduct(sales);

            if (salesData.isEmpty()) {
                return null;
            }

            createTempCsvFile(salesData);
            createRScript();

            if (executeRScript()) {
                File chartFile = new File(OUTPUT_IMAGE_PATH);
                if (chartFile.exists()) {
                    return chartFile.getAbsolutePath();
                }
            }
        } catch (Exception e) {
            System.err.println("Error generating chart: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean isChartGenerationAvailable() {
        return isRAvailable();
    }

    private Map<String, Integer> aggregateSalesByProduct(List<Sale> sales) {
        return sales.stream()
                .collect(Collectors.groupingBy(
                        Sale::getProductName,
                        Collectors.summingInt(Sale::getQuantity)
                ));
    }

    private void createTempCsvFile(Map<String, Integer> salesData) throws IOException {
        try (FileWriter writer = new FileWriter(CSV_DATA_PATH)) {
            writer.write("Product,Quantity\n");
            for (Map.Entry<String, Integer> entry : salesData.entrySet()) {
                String product = entry.getKey().replace("\"", "'");
                if (product.contains(",")) {
                    writer.write("\"" + product + "\"," + entry.getValue() + "\n");
                } else {
                    writer.write(product + "," + entry.getValue() + "\n");
                }
            }
        }
    }

    private void createRScript() throws IOException {
        try (FileWriter writer = new FileWriter(R_SCRIPT_PATH)) {
            writer.write("library(ggplot2)\n");
            writer.write("library(dplyr)\n\n");

            writer.write("# Read data\n");
            writer.write("dados <- read.csv('" + CSV_DATA_PATH + "', stringsAsFactors = FALSE)\n");
            writer.write("dados <- dados[order(-dados$Quantity), ]\n");
            writer.write("if(nrow(dados) > 10) dados <- dados[1:10, ]\n\n");

            writer.write("# Create chart\n");
            writer.write("grafico <- ggplot(dados, aes(x = reorder(Product, Quantity), y = Quantity)) +\n");
            writer.write("  geom_col(fill = '#F23064', alpha = 0.8, color = 'white') +\n");
            writer.write("  geom_text(aes(label = paste(Quantity, 'un')), vjust = -0.3, size = 3.5, fontface = 'bold') +\n");
            writer.write("  labs(title = 'Top Products by Sales Quantity',\n");
            writer.write("       subtitle = 'SellOut EasyTrack - Sales Analysis',\n");
            writer.write("       x = 'Products', y = 'Quantity Sold') +\n");
            writer.write("  theme_minimal() +\n");
            writer.write("  theme(axis.text.x = element_text(angle = 45, hjust = 1),\n");
            writer.write("        plot.title = element_text(size = 16, face = 'bold', hjust = 0.5),\n");
            writer.write("        plot.subtitle = element_text(size = 12, hjust = 0.5))\n\n");

            writer.write("# Save chart\n");
            writer.write("ggsave('" + OUTPUT_IMAGE_PATH + "', plot = grafico, width = 12, height = 8, dpi = 300)\n");
            writer.write("cat('Chart saved successfully!\\n')\n");
        }
    }

    private boolean executeRScript() {
        String[] rCommands = {
                "Rscript",
                "C:\\Program Files\\R\\R-4.4.1\\bin\\Rscript.exe",
                "C:\\Program Files\\R\\R-4.3.1\\bin\\Rscript.exe",
                "/usr/bin/Rscript",
                "/usr/local/bin/Rscript"
        };

        for (String command : rCommands) {
            try {
                ProcessBuilder pb = new ProcessBuilder(command, R_SCRIPT_PATH);
                pb.redirectErrorStream(true);
                Process process = pb.start();

                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    System.out.println("R script executed successfully with command: " + command);
                    return true;
                }
            } catch (Exception e) {
                // Continue to next command
            }
        }

        System.err.println("Failed to execute R script with any available command");
        return false;
    }

    private boolean isRAvailable() {
        String[] rCommands = {
                "Rscript",
                "R",
                "C:\\Program Files\\R\\R-4.4.1\\bin\\Rscript.exe",
                "/usr/bin/Rscript",
                "/usr/local/bin/Rscript"
        };

        for (String command : rCommands) {
            try {
                ProcessBuilder pb = new ProcessBuilder(command, "--version");
                pb.redirectErrorStream(true);
                Process process = pb.start();

                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    return true;
                }
            } catch (Exception e) {
                // Continue checking other commands
            }
        }

        return false;
    }

    // Cleanup method (can be called when application closes)
    public void cleanup() {
        try {
            new File(R_SCRIPT_PATH).delete();
            new File(CSV_DATA_PATH).delete();
            new File(OUTPUT_IMAGE_PATH).delete();
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }
}