package com.sellout.util;

import com.sellout.config.ApplicationConfig;

/**
 * Handles graceful application shutdown
 */
public class ApplicationShutdownHook extends Thread {

    @Override
    public void run() {
        System.out.println("Shutting down SellOut EasyTrack...");

        try {
            // Cleanup application resources
            ApplicationConfig.getInstance().cleanup();

            // Additional cleanup if needed
            cleanupTemporaryFiles();

            System.out.println("✓ SellOut EasyTrack shutdown completed gracefully");
        } catch (Exception e) {
            System.err.println("Error during shutdown: " + e.getMessage());
        }
    }

    private void cleanupTemporaryFiles() {
        try {
            // Clean up any temporary files created during execution
            String[] tempFiles = {
                    "temp_chart_script.R",
                    "temp_sales_data.csv",
                    "sales_chart.png",
                    "temp_graph_script.R",
                    "temp_vendas_data.csv",
                    "vendas_grafico.png"
            };

            for (String fileName : tempFiles) {
                java.io.File file = new java.io.File(fileName);
                if (file.exists() && file.delete()) {
                    System.out.println("✓ Cleaned up temporary file: " + fileName);
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not clean all temporary files: " + e.getMessage());
        }
    }
}