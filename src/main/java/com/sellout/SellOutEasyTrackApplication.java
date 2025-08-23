package com.sellout;

import com.sellout.config.ApplicationConfig;
import com.sellout.ui.util.UIUtils;
import com.sellout.ui.view.MainDashboard;

import javax.swing.*;
import java.io.File;

/**
 * Main application class for SellOut EasyTrack
 * A comprehensive sales management system with clean architecture
 */
public class SellOutEasyTrackApplication {

    private static final String APPLICATION_NAME = "SellOut EasyTrack";
    private static final String VERSION = "2.0.0";
    private static final String DESCRIPTION = "Clean Architecture Sales Management System";

    public static void main(String[] args) {
        printApplicationInfo();

        // Register shutdown hook for graceful cleanup
        Runtime.getRuntime().addShutdownHook(new Thread(() -> performCleanup()));

        if (!validateSystemRequirements()) {
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> {
            try {
                initializeApplication();
                launchMainDashboard();
                System.out.println("✓ Application launched successfully!");
            } catch (Exception e) {
                handleApplicationStartupError(e);
            }
        });
    }

    private static void printApplicationInfo() {
        System.out.println("════════════════════════════════════════");
        System.out.println("    " + APPLICATION_NAME + " v" + VERSION);
        System.out.println("    " + DESCRIPTION);
        System.out.println("════════════════════════════════════════");
        System.out.println();
    }

    private static boolean validateSystemRequirements() {
        System.out.println("Validating system requirements...");

        boolean allRequirementsMet = true;

        if (!validateJavaVersion()) {
            allRequirementsMet = false;
        }

        if (!validateDatabaseConnection()) {
            // Database issues are not fatal - app can still run with limitations
            System.out.println("⚠ Database validation failed - continuing with limitations");
        }

        checkOptionalComponents();

        return allRequirementsMet;
    }

    private static boolean validateJavaVersion() {
        try {
            String javaVersion = System.getProperty("java.version");
            System.out.println("Java version: " + javaVersion);

            String[] versionParts = javaVersion.split("\\.");
            int majorVersion = Integer.parseInt(versionParts[0]);

            if (majorVersion < 11) {
                System.err.println("ERROR: Java 11+ is required. Current version: " + javaVersion);
                return false;
            }

            System.out.println("✓ Java version is compatible");
            return true;
        } catch (Exception e) {
            System.err.println("ERROR: Failed to validate Java version: " + e.getMessage());
            return false;
        }
    }

    private static boolean validateDatabaseConnection() {
        try {
            ApplicationConfig config = ApplicationConfig.getInstance();
            boolean connectionValid = config.getDatabaseConnection().testConnection();

            if (connectionValid) {
                System.out.println("✓ Database connection established");
                return true;
            } else {
                System.err.println("⚠ Failed to connect to database");
                showDatabaseConnectionWarning();
                return false;
            }
        } catch (Exception e) {
            System.err.println("⚠ Database connection validation failed: " + e.getMessage());
            showDatabaseConnectionWarning();
            return false;
        }
    }

    private static void checkOptionalComponents() {
        try {
            ApplicationConfig config = ApplicationConfig.getInstance();

            if (config.getChartService().isChartGenerationAvailable()) {
                System.out.println("✓ Advanced chart generation available (R detected)");
            } else {
                System.out.println("⚠ Advanced charts unavailable - Basic charts will be used");
            }
        } catch (Exception e) {
            System.out.println("⚠ Could not check chart service: " + e.getMessage());
        }
    }

    private static void showDatabaseConnectionWarning() {
        SwingUtilities.invokeLater(() -> {
            String message = "Database connection failed.\n\n" +
                    "Please ensure:\n" +
                    "• MySQL server is running\n" +
                    "• Database 'SellOutEasyTrack_SQL' exists\n" +
                    "• Credentials are correct\n\n" +
                    "The application will start but with limited functionality.";

            JOptionPane.showMessageDialog(
                    null,
                    message,
                    "Database Connection Warning",
                    JOptionPane.WARNING_MESSAGE
            );
        });
    }

    private static void initializeApplication() {
        System.out.println("Initializing application...");

        try {
            UIUtils.setupLookAndFeel();
            System.out.println("✓ UI Look and Feel configured");
        } catch (Exception e) {
            System.err.println("⚠ Could not setup Look and Feel: " + e.getMessage());
        }

        try {
            configureSystemProperties();
            System.out.println("✓ System properties configured");
        } catch (Exception e) {
            System.err.println("⚠ Could not configure system properties: " + e.getMessage());
        }

        try {
            ApplicationConfig config = ApplicationConfig.getInstance();
            System.out.println("✓ Application configuration initialized");
            printConfigurationSummary(config);
        } catch (Exception e) {
            System.err.println("⚠ Application configuration issues: " + e.getMessage());
        }
    }

    private static void printConfigurationSummary(ApplicationConfig config) {
        try {
            System.out.println("\nConfiguration Summary:");
            System.out.println(config.getApplicationInfo());
            System.out.println();
        } catch (Exception e) {
            System.out.println("Could not retrieve application info: " + e.getMessage());
        }
    }

    private static void configureSystemProperties() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("sun.java2d.d3d", "false");
        System.setProperty("sun.java2d.noddraw", "true");
    }

    private static void launchMainDashboard() {
        System.out.println("Launching main dashboard...");
        new MainDashboard();
    }

    private static void handleApplicationStartupError(Exception e) {
        System.err.println("CRITICAL ERROR: Application startup failed");
        e.printStackTrace();

        String errorMessage = "Application failed to start.\n\n" +
                "Error: " + e.getMessage() + "\n\n" +
                "Please check:\n" +
                "• Database connection settings\n" +
                "• Java version compatibility\n" +
                "• System requirements\n\n" +
                "Check the console for detailed information.";

        JOptionPane.showMessageDialog(
                null,
                errorMessage,
                "Application Startup Error",
                JOptionPane.ERROR_MESSAGE
        );

        System.exit(1);
    }

    private static void performCleanup() {
        System.out.println("Shutting down SellOut EasyTrack...");

        try {
            ApplicationConfig.getInstance().cleanup();
            System.out.println("✓ Application cleanup completed");
        } catch (Exception e) {
            System.err.println("⚠ Error during application cleanup: " + e.getMessage());
        }

        try {
            cleanupTemporaryFiles();
            System.out.println("✓ Temporary files cleaned up");
        } catch (Exception e) {
            System.err.println("⚠ Could not clean all temporary files: " + e.getMessage());
        }

        System.out.println("✓ Shutdown completed gracefully");
    }

    private static void cleanupTemporaryFiles() {
        String[] tempFiles = {
                "temp_chart_script.R",
                "temp_sales_data.csv",
                "sales_chart.png",
                "temp_graph_script.R",
                "temp_vendas_data.csv",
                "vendas_grafico.png"
        };

        for (String fileName : tempFiles) {
            try {
                File file = new File(fileName);
                if (file.exists() && file.delete()) {
                    System.out.println("  ✓ Cleaned: " + fileName);
                }
            } catch (Exception e) {
                System.err.println("  ⚠ Could not delete: " + fileName);
            }
        }
    }
}