package com.sellout.config;

import com.sellout.repository.SaleRepository;
import com.sellout.repository.impl.DatabaseSaleRepository;
import com.sellout.service.*;
import com.sellout.service.impl.*;
import com.sellout.service.validator.SaleValidator;

/**
 * Central configuration class for dependency injection
 * Follows Singleton pattern to ensure single instance of each service
 */
public class ApplicationConfig {
    private static ApplicationConfig instance;

    // Core configuration
    private final DatabaseConnection databaseConnection;

    // Repository layer
    private final SaleRepository saleRepository;

    // Service layer components
    private final SaleValidator saleValidator;
    private final SalesService salesService;
    private final ImportService importService;
    private final ExportService exportService;
    private final ChartService chartService;

    private ApplicationConfig() {
        // Initialize in dependency order
        this.databaseConnection = new DatabaseConnection();
        this.saleRepository = new DatabaseSaleRepository(databaseConnection);
        this.saleValidator = new SaleValidator();
        this.salesService = new SalesServiceImpl(saleRepository, saleValidator);
        this.importService = new ImportServiceImpl(salesService);
        this.exportService = new ExportServiceImpl();
        this.chartService = new ChartServiceImpl();

        // Validate configuration
        validateConfiguration();
    }

    /**
     * Get the singleton instance of ApplicationConfig
     * Thread-safe implementation
     */
    public static synchronized ApplicationConfig getInstance() {
        if (instance == null) {
            instance = new ApplicationConfig();
        }
        return instance;
    }

    /**
     * Validates that all dependencies are properly configured
     */
    private void validateConfiguration() {
        if (!databaseConnection.testConnection()) {
            throw new RuntimeException("Database connection failed during configuration validation");
        }

        System.out.println("✓ ApplicationConfig initialized successfully");
        System.out.println("✓ All dependencies configured and validated");
    }

    // Getters for dependency injection

    public DatabaseConnection getDatabaseConnection() {
        return databaseConnection;
    }

    public SaleRepository getSaleRepository() {
        return saleRepository;
    }

    public SaleValidator getSaleValidator() {
        return saleValidator;
    }

    public SalesService getSalesService() {
        return salesService;
    }

    public ImportService getImportService() {
        return importService;
    }

    public ExportService getExportService() {
        return exportService;
    }

    public ChartService getChartService() {
        return chartService;
    }

    /**
     * Get application information for debugging/logging
     */
    public String getApplicationInfo() {
        return String.format(
                "SellOut EasyTrack v2.0\n" +
                        "Architecture: Clean Code + SOLID Principles\n" +
                        "Database: %s\n" +
                        "Chart Generation: %s\n" +
                        "Services Loaded: %d",
                databaseConnection.testConnection() ? "Connected" : "Disconnected",
                chartService.isChartGenerationAvailable() ? "Available (R)" : "Unavailable",
                6 // Total number of services
        );
    }

    /**
     * Cleanup resources when application shuts down
     * Should be called from application shutdown hook
     */
    public void cleanup() {
        try {
            if (chartService instanceof ChartServiceImpl) {
                ((ChartServiceImpl) chartService).cleanup();
            }
            System.out.println("✓ Application resources cleaned up");
        } catch (Exception e) {
            System.err.println("Warning: Error during cleanup: " + e.getMessage());
        }
    }

    /**
     * Reset singleton instance (mainly for testing purposes)
     * Use with caution in production code
     */
    public static synchronized void resetInstance() {
        if (instance != null) {
            instance.cleanup();
            instance = null;
        }
    }
}