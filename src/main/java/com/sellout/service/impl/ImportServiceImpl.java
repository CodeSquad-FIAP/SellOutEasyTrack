package com.sellout.service.impl;

import com.sellout.model.ImportResult;
import com.sellout.model.Sale;
import com.sellout.service.ImportService;
import com.sellout.service.SalesService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImportServiceImpl implements ImportService {
    private final SalesService salesService;

    private static final Pattern MONEY_PATTERN = Pattern.compile("R\\$?\\s*([0-9]+(?:[.,][0-9]{2})?)");
    private static final Pattern QUANTITY_PATTERN = Pattern.compile("(\\d+)\\s*(?:unidades?|un|x|vezes|units)?");
    private static final List<DateTimeFormatter> DATE_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ISO_LOCAL_DATE
    );

    public ImportServiceImpl(SalesService salesService) {
        this.salesService = salesService;
    }

    @Override
    public ImportResult importFromCsv(File csvFile) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;

        if (!validateFile(csvFile, ".csv", errors)) {
            return new ImportResult(false, 0, errorCount, errors, warnings);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            int lineNumber = 0;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty()) continue;

                if (isFirstLine) {
                    if (!validateCsvHeader(line)) {
                        errors.add("Line " + lineNumber + ": Invalid header format. Expected: product_name,quantity,unit_price,sale_date");
                        return new ImportResult(false, 0, 1, errors, warnings);
                    }
                    isFirstLine = false;
                    continue;
                }

                try {
                    Sale sale = parseCsvLine(line, lineNumber);
                    if (sale != null) {
                        salesService.createSale(sale);
                        successCount++;
                    } else {
                        errorCount++;
                    }
                } catch (Exception e) {
                    errors.add("Line " + lineNumber + ": " + e.getMessage());
                    errorCount++;
                }
            }
        } catch (IOException e) {
            errors.add("Error reading file: " + e.getMessage());
            return new ImportResult(false, 0, 1, errors, warnings);
        }

        return new ImportResult(true, successCount, errorCount, errors, warnings);
    }

    @Override
    public ImportResult importFromText(File textFile) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;

        if (!validateFile(textFile, ".txt", errors)) {
            return new ImportResult(false, 0, errorCount, errors, warnings);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                    continue;
                }

                try {
                    Sale sale = parseTextLine(line, lineNumber);
                    if (sale != null) {
                        salesService.createSale(sale);
                        successCount++;
                    } else {
                        warnings.add("Line " + lineNumber + ": Could not parse line format");
                    }
                } catch (Exception e) {
                    errors.add("Line " + lineNumber + ": " + e.getMessage());
                    errorCount++;
                }
            }
        } catch (IOException e) {
            errors.add("Error reading file: " + e.getMessage());
            return new ImportResult(false, 0, 1, errors, warnings);
        }

        return new ImportResult(true, successCount, errorCount, errors, warnings);
    }

    @Override
    public ImportResult importFromWhatsApp(String whatsappText) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;

        if (whatsappText == null || whatsappText.trim().isEmpty()) {
            errors.add("WhatsApp text is empty");
            return new ImportResult(false, 0, 1, errors, warnings);
        }

        String[] lines = whatsappText.split("\n");
        int lineNumber = 0;

        for (String line : lines) {
            lineNumber++;
            line = line.trim();

            if (line.isEmpty() || line.startsWith("DICA:") || line.startsWith("TIP:") || line.startsWith("-")) {
                continue;
            }

            try {
                Sale sale = parseWhatsAppLine(line, lineNumber);
                if (sale != null) {
                    salesService.createSale(sale);
                    successCount++;
                } else {
                    warnings.add("Line " + lineNumber + ": Could not extract sales data from message");
                }
            } catch (Exception e) {
                errors.add("Line " + lineNumber + ": " + e.getMessage());
                errorCount++;
            }
        }

        return new ImportResult(true, successCount, errorCount, errors, warnings);
    }

    private boolean validateFile(File file, String expectedExtension, List<String> errors) {
        if (file == null || !file.exists()) {
            errors.add("File not found");
            return false;
        }

        if (!file.getName().toLowerCase().endsWith(expectedExtension)) {
            errors.add("Invalid file type. Expected: " + expectedExtension);
            return false;
        }

        if (file.length() == 0) {
            errors.add("File is empty");
            return false;
        }

        if (file.length() > 10 * 1024 * 1024) { // 10MB limit
            errors.add("File too large (maximum 10MB)");
            return false;
        }

        return true;
    }

    private boolean validateCsvHeader(String headerLine) {
        String[] headers = parseCsvLine(headerLine);
        String headerString = String.join(",", headers).toLowerCase();
        return headerString.contains("product") &&
                headerString.contains("quantity") &&
                (headerString.contains("price") || headerString.contains("value")) &&
                headerString.contains("date");
    }

    private Sale parseCsvLine(String line, int lineNumber) {
        String[] values = parseCsvLine(line);

        if (values.length < 4) {
            throw new IllegalArgumentException("Incomplete line. Expected 4 fields: product,quantity,price,date");
        }

        try {
            String productName = cleanText(values[0]);
            Integer quantity = parseInteger(values[1]);
            BigDecimal unitPrice = parseDecimal(values[2]);
            LocalDate saleDate = parseDate(values[3]);

            return new Sale(productName, quantity, unitPrice, saleDate);
        } catch (Exception e) {
            throw new IllegalArgumentException("Parsing error: " + e.getMessage());
        }
    }

    private Sale parseTextLine(String line, int lineNumber) {
        // Format: "Product Name - 5 - 25.50 - 2024-01-15"
        if (line.contains(" - ")) {
            String[] parts = line.split(" - ");
            if (parts.length >= 3) {
                try {
                    String productName = cleanText(parts[0]);
                    Integer quantity = parseInteger(parts[1]);
                    BigDecimal unitPrice = parseDecimal(parts[2]);
                    LocalDate saleDate = parts.length > 3 ? parseDate(parts[3]) : LocalDate.now();

                    if (quantity > 0 && unitPrice.compareTo(BigDecimal.ZERO) > 0) {
                        return new Sale(productName, quantity, unitPrice, saleDate);
                    }
                } catch (Exception e) {
                    // Continue to next parsing attempt
                }
            }
        }

        // Format: "Product: 5x R$ 25.50"
        if (line.contains(":") && line.contains("x")) {
            String[] mainParts = line.split(":");
            if (mainParts.length >= 2) {
                try {
                    String productName = cleanText(mainParts[0]);
                    String resto = mainParts[1];

                    String quantityStr = resto.split("x")[0].replaceAll("[^0-9]", "");
                    String priceStr = resto.replaceAll("[^0-9.,]", "").replace(",", ".");

                    if (!quantityStr.isEmpty() && !priceStr.isEmpty()) {
                        Integer quantity = Integer.parseInt(quantityStr);
                        BigDecimal unitPrice = new BigDecimal(priceStr);
                        LocalDate saleDate = LocalDate.now();

                        if (quantity > 0 && unitPrice.compareTo(BigDecimal.ZERO) > 0) {
                            return new Sale(productName, quantity, unitPrice, saleDate);
                        }
                    }
                } catch (Exception e) {
                    // Continue to next parsing attempt
                }
            }
        }

        return null; // Could not parse
    }

    private Sale parseWhatsAppLine(String line, int lineNumber) {
        // Remove timestamp and sender info
        line = line.replaceFirst("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2} - ", "");
        line = line.replaceFirst("^[^:]+: ", "");

        String productName = "";
        Integer quantity = 1;
        BigDecimal unitPrice = BigDecimal.ZERO;

        // Extract quantity
        Matcher quantMatcher = QUANTITY_PATTERN.matcher(line);
        if (quantMatcher.find()) {
            quantity = Integer.parseInt(quantMatcher.group(1));
        }

        // Extract price
        Matcher moneyMatcher = MONEY_PATTERN.matcher(line);
        if (moneyMatcher.find()) {
            String priceStr = moneyMatcher.group(1).replace(",", ".");
            unitPrice = new BigDecimal(priceStr);
        } else {
            // Try to find decimal numbers as potential prices
            Pattern decimalPattern = Pattern.compile("\\d+[.,]\\d{2}");
            Matcher decimalMatcher = decimalPattern.matcher(line);
            while (decimalMatcher.find()) {
                try {
                    double price = Double.parseDouble(decimalMatcher.group().replace(",", "."));
                    if (price > 1.0) { // Assume prices over 1.00
                        unitPrice = BigDecimal.valueOf(price);
                        break;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        // Extract product name (remove numbers and price indicators)
        productName = line.replaceAll("\\d+[.,]?\\d*", "")
                .replaceAll("R\\$?", "")
                .replaceAll("(?i)por|cada|unidades?|vendeu|comprou|vendidos?|sold|bought", "")
                .replaceAll("\\s+", " ")
                .trim();

        // Clean up product name
        if (productName.startsWith(":")) productName = productName.substring(1).trim();
        if (productName.endsWith("-")) productName = productName.substring(0, productName.length() - 1).trim();

        if (productName.isEmpty()) {
            productName = "WhatsApp Product " + lineNumber;
        }

        if (unitPrice.compareTo(BigDecimal.ZERO) > 0 && !productName.trim().isEmpty()) {
            return new Sale(productName.trim(), quantity, unitPrice, LocalDate.now());
        }

        return null;
    }

    private String[] parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentValue = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(currentValue.toString().trim());
                currentValue = new StringBuilder();
            } else {
                currentValue.append(c);
            }
        }
        values.add(currentValue.toString().trim());

        return values.toArray(new String[0]);
    }

    private String cleanText(String text) {
        if (text == null) return "";
        return text.trim().replaceAll("^\"|\"$", "");
    }

    private Integer parseInteger(String value) {
        String cleaned = value.replaceAll("[^0-9]", "");
        if (cleaned.isEmpty()) {
            throw new NumberFormatException("Empty value");
        }
        return Integer.parseInt(cleaned);
    }

    private BigDecimal parseDecimal(String value) {
        String cleaned = value.replaceAll("[^0-9.,]", "").replace(",", ".");
        if (cleaned.isEmpty()) {
            throw new NumberFormatException("Empty value");
        }
        return new BigDecimal(cleaned);
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return LocalDate.now();
        }

        String cleaned = cleanText(dateStr);

        // Handle special cases
        if (cleaned.equalsIgnoreCase("hoje") || cleaned.equalsIgnoreCase("today")) {
            return LocalDate.now();
        }
        if (cleaned.equalsIgnoreCase("ontem") || cleaned.equalsIgnoreCase("yesterday")) {
            return LocalDate.now().minusDays(1);
        }

        // Try different date formats
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(cleaned, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }

        // If all formats fail, return current date
        return LocalDate.now();
    }
}