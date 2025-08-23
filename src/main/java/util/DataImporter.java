package util;

import model.Venda;
import controller.VendaController;

import java.io.*;
import java.nio.file.Files;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class DataImporter {

    private final VendaController vendaController;
    private final List<ImportError> errors;
    private final List<ImportWarning> warnings;

    private static final Pattern DECIMAL_PATTERN = Pattern.compile("\\d+([.,]\\d{1,2})?");
    private static final Pattern INTEGER_PATTERN = Pattern.compile("\\d+");
    private static final Pattern MONEY_PATTERN = Pattern.compile("R\\$?\\s*([0-9]+(?:[.,][0-9]{2})?)");
    private static final Pattern QUANTITY_PATTERN = Pattern.compile("(\\d+)\\s*(?:unidades?|un|x|vezes)?");

    private static final List<DateTimeFormatter> DATE_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ISO_LOCAL_DATE
    );

    public DataImporter() {
        this.vendaController = new VendaController();
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }

    public ImportResult importFromCSV(File csvFile) {
        clearErrorsAndWarnings();
        System.out.println(" Iniciando importação CSV: " + csvFile.getAbsolutePath());

        if (!validateFile(csvFile, ".csv")) {
            return new ImportResult(false, 0, 0, errors, warnings);
        }

        List<Venda> vendasImportadas = new ArrayList<>();
        int lineNumber = 0;
        int successCount = 0;
        int errorCount = 0;

        try (BufferedReader reader = Files.newBufferedReader(csvFile.toPath())) {
            String line;
            boolean isFirstLine = true;
            String[] headers = null;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                if (isFirstLine) {
                    headers = parseCSVLine(line);
                    System.out.println(" Cabeçalhos encontrados: " + Arrays.toString(headers));

                    if (!validateHeaders(headers)) {
                        addError(lineNumber, "Cabeçalhos inválidos. Esperado: produto,quantidade,valor_unitario,data");
                        System.err.println(" Cabeçalhos inválidos");
                        return new ImportResult(false, 0, 0, errors, warnings);
                    }
                    isFirstLine = false;
                    continue;
                }

                try {
                    String[] values = parseCSVLine(line);
                    System.out.println(" Processando linha " + lineNumber + ": " + Arrays.toString(values));

                    Venda venda = parseVendaFromCSV(values, lineNumber);

                    if (venda != null) {
                        vendaController.salvarVenda(venda);
                        vendasImportadas.add(venda);
                        successCount++;
                        System.out.println(" Venda salva: " + venda.getProduto());
                    } else {
                        errorCount++;
                    }
                } catch (Exception e) {
                    addError(lineNumber, "Erro ao processar linha: " + e.getMessage());
                    errorCount++;
                    System.err.println(" Erro na linha " + lineNumber + ": " + e.getMessage());
                }
            }

        } catch (IOException e) {
            addError(0, "Erro ao ler arquivo: " + e.getMessage());
            System.err.println(" Erro de E/S: " + e.getMessage());
            return new ImportResult(false, 0, 0, errors, warnings);
        }

        System.out.println(" Importação CSV concluída: " + successCount + " sucessos, " + errorCount + " erros");
        return new ImportResult(true, successCount, errorCount, errors, warnings);
    }

    public ImportResult importFromTextFile(File textFile) {
        clearErrorsAndWarnings();
        System.out.println(" Iniciando importação TXT: " + textFile.getAbsolutePath());

        if (!validateFile(textFile, ".txt")) {
            return new ImportResult(false, 0, 0, errors, warnings);
        }

        List<Venda> vendasImportadas = new ArrayList<>();
        int lineNumber = 0;
        int successCount = 0;
        int errorCount = 0;

        try (BufferedReader reader = Files.newBufferedReader(textFile.toPath())) {
            String line;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                    continue;
                }

                try {
                    Venda venda = parseVendaFromText(line, lineNumber);

                    if (venda != null) {
                        vendaController.salvarVenda(venda);
                        vendasImportadas.add(venda);
                        successCount++;
                        System.out.println(" Venda TXT salva: " + venda.getProduto());
                    } else {
                        errorCount++;
                    }
                } catch (Exception e) {
                    addError(lineNumber, "Erro ao processar linha: " + e.getMessage());
                    errorCount++;
                    System.err.println(" Erro TXT na linha " + lineNumber + ": " + e.getMessage());
                }
            }

        } catch (IOException e) {
            addError(0, "Erro ao ler arquivo: " + e.getMessage());
            return new ImportResult(false, 0, 0, errors, warnings);
        }

        System.out.println(" Importação TXT concluída: " + successCount + " sucessos, " + errorCount + " erros");
        return new ImportResult(true, successCount, errorCount, errors, warnings);
    }

    public ImportResult importFromWhatsApp(String whatsappText) {
        clearErrorsAndWarnings();
        System.out.println(" Iniciando importação WhatsApp");

        if (whatsappText == null || whatsappText.trim().isEmpty()) {
            addError(0, "Texto do WhatsApp está vazio");
            return new ImportResult(false, 0, 0, errors, warnings);
        }

        List<Venda> vendasImportadas = new ArrayList<>();
        String[] lines = whatsappText.split("\n");
        int lineNumber = 0;
        int successCount = 0;
        int errorCount = 0;

        for (String line : lines) {
            lineNumber++;
            line = line.trim();

            if (line.isEmpty() || line.startsWith("DICA:") || line.startsWith("-")) {
                continue;
            }

            try {
                Venda venda = parseVendaFromWhatsApp(line, lineNumber);

                if (venda != null) {
                    vendaController.salvarVenda(venda);
                    vendasImportadas.add(venda);
                    successCount++;
                    System.out.println(" Venda WhatsApp salva: " + venda.getProduto() + " - " + venda.getQuantidade() + " - R$ " + venda.getValorUnitario());
                } else {
                    addWarning(lineNumber, "Linha ignorada (sem dados de venda): " + line);
                }
            } catch (Exception e) {
                addError(lineNumber, "Erro ao processar mensagem: " + e.getMessage());
                errorCount++;
                System.err.println(" Erro WhatsApp na linha " + lineNumber + ": " + e.getMessage());
            }
        }

        System.out.println(" Importação WhatsApp concluída: " + successCount + " sucessos, " + errorCount + " erros");
        return new ImportResult(true, successCount, errorCount, errors, warnings);
    }

    private Venda parseVendaFromCSV(String[] values, int lineNumber) {
        if (values.length < 4) {
            addError(lineNumber, "Linha incompleta. Esperado 4 campos: produto,quantidade,valor,data");
            return null;
        }

        try {
            String produto = cleanText(values[0]);
            int quantidade = parseInteger(values[1], lineNumber, "quantidade");
            double valor = parseDecimal(values[2], lineNumber, "valor unitário");
            Date data = parseDate(values[3], lineNumber);

            if (produto.isEmpty()) {
                addError(lineNumber, "Nome do produto não pode estar vazio");
                return null;
            }

            if (quantidade <= 0) {
                addError(lineNumber, "Quantidade deve ser maior que zero");
                return null;
            }

            if (valor <= 0) {
                addError(lineNumber, "Valor unitário deve ser maior que zero");
                return null;
            }

            return new Venda(produto, quantidade, valor, data);

        } catch (Exception e) {
            addError(lineNumber, "Erro na validação: " + e.getMessage());
            return null;
        }
    }

    private Venda parseVendaFromText(String line, int lineNumber) {
        try {
            if (line.contains(" - ")) {
                String[] parts = line.split(" - ");
                if (parts.length >= 3) {
                    String produto = cleanText(parts[0]);
                    String quantidadeStr = parts[1].replaceAll("[^0-9]", "");
                    String valorStr = parts[2].replaceAll("[^0-9.,]", "").replace(",", ".");
                    String dataStr = parts.length > 3 ? parts[3] : LocalDate.now().toString();

                    if (!quantidadeStr.isEmpty() && !valorStr.isEmpty()) {
                        int quantidade = Integer.parseInt(quantidadeStr);
                        double valor = Double.parseDouble(valorStr);
                        Date data = parseDate(dataStr, lineNumber);

                        if (quantidade > 0 && valor > 0) {
                            return new Venda(produto, quantidade, valor, data);
                        }
                    }
                }
            }

            if (line.contains(":") && line.contains("x")) {
                String[] mainParts = line.split(":");
                if (mainParts.length >= 2) {
                    String produto = cleanText(mainParts[0]);
                    String resto = mainParts[1];

                    String quantidadeStr = resto.split("x")[0].replaceAll("[^0-9]", "");
                    String valorStr = resto.replaceAll("[^0-9.,]", "").replace(",", ".");

                    if (!quantidadeStr.isEmpty() && !valorStr.isEmpty()) {
                        int quantidade = Integer.parseInt(quantidadeStr);
                        double valor = Double.parseDouble(valorStr);
                        Date data = Date.valueOf(LocalDate.now());

                        if (quantidade > 0 && valor > 0) {
                            return new Venda(produto, quantidade, valor, data);
                        }
                    }
                }
            }

            addWarning(lineNumber, "Formato não reconhecido, linha ignorada: " + line);
            return null;

        } catch (Exception e) {
            addError(lineNumber, "Erro ao interpretar linha: " + e.getMessage());
            return null;
        }
    }

    private Venda parseVendaFromWhatsApp(String line, int lineNumber) {
        try {
            line = line.replaceFirst("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2} - ", "");
            line = line.replaceFirst("^[^:]+: ", "");

            System.out.println(" Analisando linha WhatsApp: " + line);

            String produto = "";
            int quantidade = 1;
            double valor = 0.0;

            Matcher quantMatcher = QUANTITY_PATTERN.matcher(line);
            if (quantMatcher.find()) {
                quantidade = Integer.parseInt(quantMatcher.group(1));
                System.out.println(" Quantidade encontrada: " + quantidade);
            }

            Matcher moneyMatcher = MONEY_PATTERN.matcher(line);
            if (moneyMatcher.find()) {
                String valorStr = moneyMatcher.group(1).replace(",", ".");
                valor = Double.parseDouble(valorStr);
                System.out.println(" Valor encontrado: " + valor);
            } else {
                Matcher decimalMatcher = DECIMAL_PATTERN.matcher(line);
                List<Double> valores = new ArrayList<>();
                while (decimalMatcher.find()) {
                    try {
                        double val = Double.parseDouble(decimalMatcher.group().replace(",", "."));
                        if (val > 10) {
                            valores.add(val);
                        }
                    } catch (NumberFormatException ignored) {}
                }
                if (!valores.isEmpty()) {
                    valor = valores.get(valores.size() - 1);
                    System.out.println(" Valor decimal encontrado: " + valor);
                }
            }

            produto = line.replaceAll("\\d+[.,]?\\d*", "")
                    .replaceAll("R\\$?", "")
                    .replaceAll("por|cada|unidades?|vendeu|comprei|vendidos?", "")
                    .replaceAll("\\s+", " ")
                    .trim();

            if (produto.startsWith(":")) produto = produto.substring(1).trim();
            if (produto.endsWith("-")) produto = produto.substring(0, produto.length() - 1).trim();

            if (produto.isEmpty()) {
                produto = "Produto WhatsApp " + lineNumber;
            }

            System.out.println(" Produto extraído: '" + produto + "'");
            System.out.println(" Dados finais: " + produto + " | " + quantidade + " | " + valor);

            if (valor > 0 && !produto.trim().isEmpty()) {
                Date data = Date.valueOf(LocalDate.now());
                return new Venda(produto.trim(), quantidade, valor, data);
            } else {
                addWarning(lineNumber, "Não foi possível extrair dados válidos: " + line);
                return null;
            }

        } catch (Exception e) {
            addError(lineNumber, "Erro ao processar mensagem WhatsApp: " + e.getMessage());
            System.err.println(" Erro detalhado WhatsApp: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String[] parseCSVLine(String line) {
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

    private boolean validateHeaders(String[] headers) {
        if (headers.length < 4) return false;

        String headerString = String.join(",", headers).toLowerCase();
        return headerString.contains("produto") &&
                headerString.contains("quantidade") &&
                (headerString.contains("valor") || headerString.contains("preco")) &&
                headerString.contains("data");
    }

    private boolean validateFile(File file, String expectedExtension) {
        if (file == null || !file.exists()) {
            addError(0, "Arquivo não encontrado");
            return false;
        }

        if (!file.getName().toLowerCase().endsWith(expectedExtension)) {
            addError(0, "Tipo de arquivo inválido. Esperado: " + expectedExtension);
            return false;
        }

        if (file.length() == 0) {
            addError(0, "Arquivo está vazio");
            return false;
        }

        if (file.length() > 10 * 1024 * 1024) {
            addError(0, "Arquivo muito grande (máximo 10MB)");
            return false;
        }

        return true;
    }

    private String cleanText(String text) {
        if (text == null) return "";
        return text.trim().replaceAll("^\"|\"$", "");
    }

    private int parseInteger(String value, int lineNumber, String fieldName) {
        try {
            String cleaned = value.replaceAll("[^0-9]", "");
            if (cleaned.isEmpty()) {
                throw new NumberFormatException("Vazio");
            }
            return Integer.parseInt(cleaned);
        } catch (NumberFormatException e) {
            addError(lineNumber, "Valor inválido para " + fieldName + ": '" + value + "'");
            return 0;
        }
    }

    private double parseDecimal(String value, int lineNumber, String fieldName) {
        try {
            String cleaned = value.replaceAll("[^0-9.,]", "").replace(",", ".");
            if (cleaned.isEmpty()) {
                throw new NumberFormatException("Vazio");
            }
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            addError(lineNumber, "Valor inválido para " + fieldName + ": '" + value + "'");
            return 0.0;
        }
    }

    private Date parseDate(String dateStr, int lineNumber) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return Date.valueOf(LocalDate.now());
        }

        String cleaned = cleanText(dateStr);

        if (cleaned.equalsIgnoreCase("hoje") || cleaned.equalsIgnoreCase("today")) {
            return Date.valueOf(LocalDate.now());
        }
        if (cleaned.equalsIgnoreCase("ontem") || cleaned.equalsIgnoreCase("yesterday")) {
            return Date.valueOf(LocalDate.now().minusDays(1));
        }

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                LocalDate date = LocalDate.parse(cleaned, formatter);
                return Date.valueOf(date);
            } catch (DateTimeParseException ignored) {
            }
        }

        addWarning(lineNumber, "Formato de data não reconhecido: '" + dateStr + "'. Usando data atual.");
        return Date.valueOf(LocalDate.now());
    }

    private void addError(int lineNumber, String message) {
        errors.add(new ImportError(lineNumber, message));
        System.err.println(" Erro linha " + lineNumber + ": " + message);
    }

    private void addWarning(int lineNumber, String message) {
        warnings.add(new ImportWarning(lineNumber, message));
        System.out.println(" Aviso linha " + lineNumber + ": " + message);
    }

    private void clearErrorsAndWarnings() {
        errors.clear();
        warnings.clear();
    }

    public static class ImportResult {
        private final boolean success;
        private final int successCount;
        private final int errorCount;
        private final List<ImportError> errors;
        private final List<ImportWarning> warnings;

        public ImportResult(boolean success, int successCount, int errorCount,
                            List<ImportError> errors, List<ImportWarning> warnings) {
            this.success = success;
            this.successCount = successCount;
            this.errorCount = errorCount;
            this.errors = new ArrayList<>(errors);
            this.warnings = new ArrayList<>(warnings);
        }

        public boolean isSuccess() { return success; }
        public int getSuccessCount() { return successCount; }
        public int getErrorCount() { return errorCount; }
        public List<ImportError> getErrors() { return errors; }
        public List<ImportWarning> getWarnings() { return warnings; }
        public boolean hasErrors() { return !errors.isEmpty(); }
        public boolean hasWarnings() { return !warnings.isEmpty(); }

        public String getSummary() {
            return String.format("Importação concluída: %d sucessos, %d erros, %d avisos",
                    successCount, errorCount, warnings.size());
        }
    }

    public static class ImportError {
        private final int lineNumber;
        private final String message;

        public ImportError(int lineNumber, String message) {
            this.lineNumber = lineNumber;
            this.message = message;
        }

        public int getLineNumber() { return lineNumber; }
        public String getMessage() { return message; }

        @Override
        public String toString() {
            return String.format("Linha %d: %s", lineNumber, message);
        }
    }

    public static class ImportWarning {
        private final int lineNumber;
        private final String message;

        public ImportWarning(int lineNumber, String message) {
            this.lineNumber = lineNumber;
            this.message = message;
        }

        public int getLineNumber() { return lineNumber; }
        public String getMessage() { return message; }

        @Override
        public String toString() {
            return String.format("Linha %d: %s", lineNumber, message);
        }
    }
}