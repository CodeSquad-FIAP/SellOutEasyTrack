package util;

import model.Venda;
import controller.VendaController;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Sistema de Importação de Múltiplas Fontes de Dados
 * Suporta CSV, TXT, e futuramente Excel e APIs
 */
public class DataImporter {

    private final VendaController vendaController;
    private final List<ImportError> errors;
    private final List<ImportWarning> warnings;

    // Padrões de validação
    private static final Pattern DECIMAL_PATTERN = Pattern.compile("^\\d+([.,]\\d{1,2})?$");
    private static final Pattern INTEGER_PATTERN = Pattern.compile("^\\d+$");

    // Formatadores de data aceitos
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

    /**
     * Importa vendas de arquivo CSV
     */
    public ImportResult importFromCSV(File csvFile) {
        clearErrorsAndWarnings();

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

                if (isFirstLine) {
                    headers = parseCSVLine(line);
                    if (!validateHeaders(headers)) {
                        addError(lineNumber, "Cabeçalhos inválidos. Esperado: produto,quantidade,valor_unitario,data");
                        return new ImportResult(false, 0, 0, errors, warnings);
                    }
                    isFirstLine = false;
                    continue;
                }

                try {
                    String[] values = parseCSVLine(line);
                    Venda venda = parseVendaFromCSV(values, lineNumber);

                    if (venda != null) {
                        vendaController.salvarVenda(venda);
                        vendasImportadas.add(venda);
                        successCount++;
                    } else {
                        errorCount++;
                    }
                } catch (Exception e) {
                    addError(lineNumber, "Erro ao processar linha: " + e.getMessage());
                    errorCount++;
                }
            }

        } catch (IOException e) {
            addError(0, "Erro ao ler arquivo: " + e.getMessage());
            return new ImportResult(false, 0, 0, errors, warnings);
        }

        return new ImportResult(true, successCount, errorCount, errors, warnings);
    }

    /**
     * Importa vendas de arquivo de texto (formato livre)
     */
    public ImportResult importFromTextFile(File textFile) {
        clearErrorsAndWarnings();

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
                    continue; // Pular comentários e linhas vazias
                }

                try {
                    Venda venda = parseVendaFromText(line, lineNumber);

                    if (venda != null) {
                        vendaController.salvarVenda(venda);
                        vendasImportadas.add(venda);
                        successCount++;
                    } else {
                        errorCount++;
                    }
                } catch (Exception e) {
                    addError(lineNumber, "Erro ao processar linha: " + e.getMessage());
                    errorCount++;
                }
            }

        } catch (IOException e) {
            addError(0, "Erro ao ler arquivo: " + e.getMessage());
            return new ImportResult(false, 0, 0, errors, warnings);
        }

        return new ImportResult(true, successCount, errorCount, errors, warnings);
    }

    /**
     * Importa vendas de dados WhatsApp (formato específico)
     */
    public ImportResult importFromWhatsApp(String whatsappText) {
        clearErrorsAndWarnings();

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

            if (line.isEmpty()) continue;

            try {
                Venda venda = parseVendaFromWhatsApp(line, lineNumber);

                if (venda != null) {
                    vendaController.salvarVenda(venda);
                    vendasImportadas.add(venda);
                    successCount++;
                } else {
                    errorCount++;
                }
            } catch (Exception e) {
                addError(lineNumber, "Erro ao processar mensagem: " + e.getMessage());
                errorCount++;
            }
        }

        return new ImportResult(true, successCount, errorCount, errors, warnings);
    }

    /**
     * Gera template CSV para download
     */
    public File generateCSVTemplate() throws IOException {
        File template = new File("template_vendas.csv");

        try (PrintWriter writer = new PrintWriter(new FileWriter(template))) {
            writer.println("produto,quantidade,valor_unitario,data");
            writer.println("\"Produto Exemplo\",5,25.50,\"" + LocalDate.now() + "\"");
            writer.println("\"Outro Produto\",2,150.00,\"" + LocalDate.now() + "\"");
        }

        return template;
    }

    // Métodos privados de parsing

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
        // Formatos aceitos:
        // "Produto - 5 unidades - R$ 25,50 - 2024-01-15"
        // "Produto: 5 x R$ 25,50 em 15/01/2024"
        // "5x Produto por R$ 25,50 hoje"

        try {
            // Padrão 1: Produto - quantidade - valor - data
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

                        return new Venda(produto, quantidade, valor, data);
                    }
                }
            }

            // Padrão 2: Produto: quantidade x valor em data
            if (line.contains(":") && line.contains("x")) {
                String[] mainParts = line.split(":");
                if (mainParts.length >= 2) {
                    String produto = cleanText(mainParts[0]);
                    String resto = mainParts[1];

                    // Extrair quantidade
                    String quantidadeStr = resto.split("x")[0].replaceAll("[^0-9]", "");

                    // Extrair valor
                    String valorStr = resto.replaceAll("[^0-9.,]", "").replace(",", ".");

                    if (!quantidadeStr.isEmpty() && !valorStr.isEmpty()) {
                        int quantidade = Integer.parseInt(quantidadeStr);
                        double valor = Double.parseDouble(valorStr);
                        Date data = Date.valueOf(LocalDate.now()); // Default hoje

                        return new Venda(produto, quantidade, valor, data);
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
        // Formato WhatsApp: "João: Comprei 5 Notebooks por R$ 2500 cada"
        // "Cliente: Produto - quantidade - valor"

        try {
            // Remover timestamp do WhatsApp se presente
            line = line.replaceFirst("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2} - ", "");

            // Se tem nome do cliente
            if (line.contains(":")) {
                String[] parts = line.split(":", 2);
                if (parts.length >= 2) {
                    line = parts[1].trim(); // Pegar só a mensagem
                }
            }

            // Extrair informações usando regex ou padrões
            String produto = "Produto WhatsApp";
            int quantidade = 1;
            double valor = 0.0;

            // Procurar por padrões de quantidade
            if (line.matches(".*\\d+.*")) {
                String[] words = line.split("\\s+");
                for (int i = 0; i < words.length; i++) {
                    String word = words[i];

                    // Procurar quantidade
                    if (word.matches("\\d+") && i + 1 < words.length) {
                        quantidade = Integer.parseInt(word);
                        if (i + 1 < words.length) {
                            produto = words[i + 1];
                        }
                    }

                    // Procurar valor (R$ 150, 150.50, etc)
                    if (word.matches("R\\$?\\d+([.,]\\d{2})?") || word.matches("\\d+([.,]\\d{2})?")) {
                        String valorStr = word.replaceAll("[^0-9.,]", "").replace(",", ".");
                        if (!valorStr.isEmpty()) {
                            valor = Double.parseDouble(valorStr);
                        }
                    }
                }
            }

            if (valor > 0) {
                Date data = Date.valueOf(LocalDate.now());
                return new Venda(produto, quantidade, valor, data);
            } else {
                addWarning(lineNumber, "Não foi possível extrair valor da mensagem: " + line);
                return null;
            }

        } catch (Exception e) {
            addError(lineNumber, "Erro ao processar mensagem WhatsApp: " + e.getMessage());
            return null;
        }
    }

    // Métodos auxiliares

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

        String[] expectedHeaders = {"produto", "quantidade", "valor", "data"};
        for (int i = 0; i < expectedHeaders.length; i++) {
            if (i >= headers.length) return false;
            String header = headers[i].toLowerCase().replaceAll("[^a-z]", "");
            if (!header.contains(expectedHeaders[i].replaceAll("_", ""))) {
                return false;
            }
        }
        return true;
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

        if (file.length() > 10 * 1024 * 1024) { // 10MB limit
            addError(0, "Arquivo muito grande (máximo 10MB)");
            return false;
        }

        return true;
    }

    private String cleanText(String text) {
        if (text == null) return "";
        return text.trim().replaceAll("^\"|\"$", ""); // Remove aspas
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

        // Tentar hoje/ontem
        if (cleaned.equalsIgnoreCase("hoje") || cleaned.equalsIgnoreCase("today")) {
            return Date.valueOf(LocalDate.now());
        }
        if (cleaned.equalsIgnoreCase("ontem") || cleaned.equalsIgnoreCase("yesterday")) {
            return Date.valueOf(LocalDate.now().minusDays(1));
        }

        // Tentar formatadores de data
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                LocalDate date = LocalDate.parse(cleaned, formatter);
                return Date.valueOf(date);
            } catch (DateTimeParseException ignored) {
                // Continuar tentando
            }
        }

        addWarning(lineNumber, "Formato de data não reconhecido: '" + dateStr + "'. Usando data atual.");
        return Date.valueOf(LocalDate.now());
    }

    private void addError(int lineNumber, String message) {
        errors.add(new ImportError(lineNumber, message));
    }

    private void addWarning(int lineNumber, String message) {
        warnings.add(new ImportWarning(lineNumber, message));
    }

    private void clearErrorsAndWarnings() {
        errors.clear();
        warnings.clear();
    }

    // Classes auxiliares

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

        // Getters
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