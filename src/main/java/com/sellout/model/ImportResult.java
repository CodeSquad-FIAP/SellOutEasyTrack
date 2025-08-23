package com.sellout.model;

import java.util.List;

public class ImportResult {
    private final boolean success;
    private final int successCount;
    private final int errorCount;
    private final List<String> errors;
    private final List<String> warnings;

    public ImportResult(boolean success, int successCount, int errorCount,
                        List<String> errors, List<String> warnings) {
        this.success = success;
        this.successCount = successCount;
        this.errorCount = errorCount;
        this.errors = List.copyOf(errors);
        this.warnings = List.copyOf(warnings);
    }

    public boolean isSuccess() { return success; }
    public int getSuccessCount() { return successCount; }
    public int getErrorCount() { return errorCount; }
    public List<String> getErrors() { return errors; }
    public List<String> getWarnings() { return warnings; }
    public boolean hasErrors() { return !errors.isEmpty(); }
    public boolean hasWarnings() { return !warnings.isEmpty(); }

    public String getSummary() {
        return String.format("Import completed: %d successful, %d errors, %d warnings",
                successCount, errorCount, warnings.size());
    }
}