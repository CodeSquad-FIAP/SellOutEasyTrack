package util;

import model.Venda;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.nio.charset.StandardCharsets;

public class RGraphUtil {

    private static final String R_SCRIPT_PATH = "temp_graph_script.R";
    private static final String CSV_DATA_PATH = "temp_vendas_data.csv";
    private static final String OUTPUT_IMAGE_PATH = "vendas_grafico.png";

    public static String gerarGraficoVendas(List<Venda> vendas) {
        System.out.println("Iniciando geração de gráfico R com paleta FIAP + Asteria...");

        try {
            if (!verificarRDisponivel()) {
                System.err.println("R não está disponível no sistema!");
                return null;
            }

            Map<String, Integer> dadosAgregados = agregareVendasPorProduto(vendas);
            System.out.println("Dados agregados: " + dadosAgregados);

            if (dadosAgregados.isEmpty()) {
                System.out.println("Nenhum dado para gerar gráfico");
                return criarGraficoVazio();
            }

            criarArquivoCSVTemp(dadosAgregados);
            System.out.println("Arquivo CSV criado: " + CSV_DATA_PATH);

            criarScriptRComPaletaFiapAsteria();
            System.out.println("Script R criado com paleta FIAP + Asteria: " + R_SCRIPT_PATH);

            boolean sucesso = executarScriptR();

            if (sucesso) {
                File imagemGerada = new File(OUTPUT_IMAGE_PATH);
                if (imagemGerada.exists()) {
                    System.out.println("Gráfico gerado com sucesso usando paleta FIAP + Asteria: " + imagemGerada.getAbsolutePath());
                    return imagemGerada.getAbsolutePath();
                } else {
                    System.err.println("Arquivo de imagem não foi criado");
                }
            }

        } catch (Exception e) {
            System.err.println("Erro ao gerar gráfico R: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private static boolean verificarRDisponivel() {
        String[] comandosR = {
                "C:\\Program Files\\R\\R-4.5.1\\bin\\Rscript.exe",
                "C:\\Program Files\\R\\R-4.4.1\\bin\\Rscript.exe",
                "C:\\Program Files\\R\\R-4.4.0\\bin\\Rscript.exe",
                "C:\\Program Files\\R\\R-4.3.2\\bin\\Rscript.exe",
                "C:\\Program Files\\R\\R-4.3.1\\bin\\Rscript.exe",
                "C:\\Program Files\\R\\R-4.3.0\\bin\\Rscript.exe",
                "C:\\Program Files (x86)\\R\\R-4.5.1\\bin\\Rscript.exe",
                "C:\\Program Files (x86)\\R\\R-4.4.1\\bin\\Rscript.exe",
                "C:\\R\\R-4.5.1\\bin\\Rscript.exe",
                "C:\\R\\R-4.4.1\\bin\\Rscript.exe",
                "Rscript",
                "R",
                "/usr/bin/Rscript",
                "/usr/local/bin/Rscript"
        };

        for (String comando : comandosR) {
            try {
                ProcessBuilder pb = new ProcessBuilder(comando, "--version");
                pb.redirectErrorStream(true);
                Process process = pb.start();

                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    System.out.println("R encontrado com comando: " + comando);
                    return true;
                }
            } catch (Exception e) {
                System.out.println("Comando " + comando + " não funcionou: " + e.getMessage());
            }
        }

        System.err.println("R não encontrado em nenhum dos comandos testados");
        return false;
    }

    private static Map<String, Integer> agregareVendasPorProduto(List<Venda> vendas) {
        return vendas.stream()
                .collect(Collectors.groupingBy(
                        Venda::getProduto,
                        Collectors.summingInt(Venda::getQuantidade)
                ));
    }

    private static void criarArquivoCSVTemp(Map<String, Integer> dados) throws IOException {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(CSV_DATA_PATH), StandardCharsets.UTF_8))) {
            writer.write('\uFEFF');
            writer.println("Produto,Quantidade");
            for (Map.Entry<String, Integer> entry : dados.entrySet()) {
                String produto = entry.getKey().replace("\"", "'");
                if (produto.contains(",")) {
                    writer.println("\"" + produto + "\"," + entry.getValue());
                } else {
                    writer.println(produto + "," + entry.getValue());
                }
            }
        }
        System.out.println("CSV criado com " + dados.size() + " produtos");
    }

    private static void criarScriptRComPaletaFiapAsteria() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(R_SCRIPT_PATH))) {
            writer.println("cat('Iniciando script R com paleta FIAP + Asteria...\\n')");
            writer.println("");
            writer.println("library(ggplot2)");
            writer.println("");
            writer.println("# ===== PALETA DE CORES FIAP + ASTERIA =====");
            writer.println("cores_fiap_asteria <- c(");
            writer.println("  fiap_pink_vibrant = '" + ColorPalette.toHex(ColorPalette.FIAP_PINK_VIBRANT) + "',");
            writer.println("  fiap_pink_dark = '" + ColorPalette.toHex(ColorPalette.FIAP_PINK_DARK) + "',");
            writer.println("  asteria_midnight_blue = '" + ColorPalette.toHex(ColorPalette.ASTERIA_MIDNIGHT_BLUE) + "',");
            writer.println("  asteria_amethyst = '" + ColorPalette.toHex(ColorPalette.ASTERIA_AMETHYST) + "',");
            writer.println("  asteria_ocean_blue = '" + ColorPalette.toHex(ColorPalette.ASTERIA_OCEAN_BLUE) + "',");
            writer.println("  success_emerald = '" + ColorPalette.toHex(ColorPalette.SUCCESS_EMERALD) + "',");
            writer.println("  warning_amber = '" + ColorPalette.toHex(ColorPalette.WARNING_AMBER) + "',");
            writer.println("  pure_white = '" + ColorPalette.toHex(ColorPalette.PURE_WHITE) + "',");
            writer.println("  light_gray = '" + ColorPalette.toHex(ColorPalette.LIGHT_GRAY) + "',");
            writer.println("  soft_charcoal = '" + ColorPalette.toHex(ColorPalette.SOFT_CHARCOAL) + "'");
            writer.println(")");
            writer.println("");
            writer.println("# Paleta para séries de dados");
            writer.println("cores_series <- c(");
            String[] rColorPalette = ColorPalette.getRColorPalette();
            for (int i = 0; i < rColorPalette.length; i++) {
                writer.print("  '" + rColorPalette[i] + "'");
                if (i < rColorPalette.length - 1) {
                    writer.println(",");
                } else {
                    writer.println();
                }
            }
            writer.println(")");
            writer.println("");
            writer.println("dados <- read.csv('" + CSV_DATA_PATH + "', stringsAsFactors = FALSE)");
            writer.println("cat(paste('Dados lidos:', nrow(dados), 'linhas\\n'))");
            writer.println("");
            writer.println("dados <- dados[order(-dados$Quantidade), ]");
            writer.println("if(nrow(dados) > 10) dados <- dados[1:10, ]");
            writer.println("");
            writer.println("# Atribuir cores dinamicamente baseado no número de produtos");
            writer.println("num_produtos <- nrow(dados)");
            writer.println("cores_usadas <- cores_series[1:min(num_produtos, length(cores_series))]");
            writer.println("if(num_produtos > length(cores_series)) {");
            writer.println("  cores_extras <- rep(cores_series, ceiling(num_produtos / length(cores_series)))");
            writer.println("  cores_usadas <- cores_extras[1:num_produtos]");
            writer.println("}");
            writer.println("");
            writer.println("cat('Criando gráfico 4K com paleta FIAP + Asteria...\\n')");
            writer.println("grafico <- ggplot(dados, aes(x = reorder(Produto, Quantidade), y = Quantidade)) +");
            writer.println("  geom_col(");
            writer.println("    fill = cores_fiap_asteria[['fiap_pink_vibrant']],");
            writer.println("    color = cores_fiap_asteria[['pure_white']],");
            writer.println("    linewidth = 1.0,");
            writer.println("    alpha = 0.9");
            writer.println("  ) +");
            writer.println("  geom_text(");
            writer.println("    aes(label = paste(Quantidade, 'un')),");
            writer.println("    vjust = -0.5,");
            writer.println("    size = 5,");
            writer.println("    fontface = 'bold',");
            writer.println("    color = cores_fiap_asteria[['soft_charcoal']]");
            writer.println("  ) +");
            writer.println("  labs(");
            writer.println("    title = 'Produtos Mais Vendidos',");
            writer.println("    subtitle = 'SellOut EasyTrack - Análise de Vendas',");
            writer.println("    x = 'Produtos',");
            writer.println("    y = 'Quantidade Vendida',");
            writer.println("    caption = 'SellOut EasyTrack - Análise de Vendas'");
            writer.println("  ) +");
            writer.println("  scale_y_continuous(expand = c(0, 0, 0.1, 0)) +");
            writer.println("  theme_minimal(base_size = 16) +");
            writer.println("  theme(");
            writer.println("    plot.title = element_text(");
            writer.println("      size = 24,");
            writer.println("      face = 'bold',");
            writer.println("      hjust = 0.5,");
            writer.println("      color = cores_fiap_asteria[['asteria_midnight_blue']],");
            writer.println("      margin = margin(b = 10)");
            writer.println("    ),");
            writer.println("    plot.subtitle = element_text(");
            writer.println("      size = 16,");
            writer.println("      hjust = 0.5,");
            writer.println("      color = cores_fiap_asteria[['fiap_pink_dark']],");
            writer.println("      margin = margin(b = 20)");
            writer.println("    ),");
            writer.println("    plot.caption = element_text(");
            writer.println("      size = 12,");
            writer.println("      color = '" + ColorPalette.toHex(ColorPalette.FIAP_GRAY_MEDIUM) + "',");
            writer.println("      hjust = 1");
            writer.println("    ),");
            writer.println("    axis.title.x = element_text(");
            writer.println("      size = 18,");
            writer.println("      face = 'bold',");
            writer.println("      color = cores_fiap_asteria[['soft_charcoal']],");
            writer.println("      margin = margin(t = 15)");
            writer.println("    ),");
            writer.println("    axis.title.y = element_text(");
            writer.println("      size = 18,");
            writer.println("      face = 'bold',");
            writer.println("      color = cores_fiap_asteria[['soft_charcoal']],");
            writer.println("      margin = margin(r = 15)");
            writer.println("    ),");
            writer.println("    axis.text.x = element_text(");
            writer.println("      size = 14,");
            writer.println("      angle = 45,");
            writer.println("      hjust = 1,");
            writer.println("      color = cores_fiap_asteria[['asteria_midnight_blue']]");
            writer.println("    ),");
            writer.println("    axis.text.y = element_text(");
            writer.println("      size = 14,");
            writer.println("      color = cores_fiap_asteria[['asteria_midnight_blue']]");
            writer.println("    ),");
            writer.println("    panel.grid.minor = element_blank(),");
            writer.println("    panel.grid.major.x = element_blank(),");
            writer.println("    panel.grid.major.y = element_line(");
            writer.println("      color = cores_fiap_asteria[['light_gray']],");
            writer.println("      linewidth = 0.6");
            writer.println("    ),");
            writer.println("    plot.background = element_rect(");
            writer.println("      fill = cores_fiap_asteria[['pure_white']],");
            writer.println("      color = NA");
            writer.println("    ),");
            writer.println("    panel.background = element_rect(");
            writer.println("      fill = cores_fiap_asteria[['pure_white']],");
            writer.println("      color = NA");
            writer.println("    ),");
            writer.println("    plot.margin = margin(25, 25, 25, 25)");
            writer.println("  )");
            writer.println("");
            writer.println("cat('Salvando gráfico 4K com paleta FIAP + Asteria...\\n')");
            writer.println("ggsave(");
            writer.println("  filename = '" + OUTPUT_IMAGE_PATH + "',");
            writer.println("  plot = grafico,");
            writer.println("  width = 16,");
            writer.println("  height = 12,");
            writer.println("  dpi = 150,");
            writer.println("  units = 'in',");
            writer.println("  device = 'png',");
            writer.println("  type = 'cairo-png',");
            writer.println("  bg = cores_fiap_asteria[['pure_white']]");
            writer.println(")");
            writer.println("");
            writer.println("if(file.exists('" + OUTPUT_IMAGE_PATH + "')) {");
            writer.println("  cat('Gráfico salvo com sucesso usando paleta FIAP + Asteria!\\n')");
            writer.println("  cat('Cores aplicadas:\\n')");
            writer.println("  cat('• FIAP Pink Vibrant:', cores_fiap_asteria[['fiap_pink_vibrant']], '\\n')");
            writer.println("  cat('• Asteria Midnight Blue:', cores_fiap_asteria[['asteria_midnight_blue']], '\\n')");
            writer.println("  cat('• Asteria Ocean Blue:', cores_fiap_asteria[['asteria_ocean_blue']], '\\n')");
            writer.println("} else {");
            writer.println("  stop('Erro ao salvar gráfico com paleta FIAP + Asteria')");
            writer.println("}");
            writer.println("");
            writer.println("cat('Script R concluído com paleta FIAP + Asteria!\\n')");
        }
        System.out.println("Script R com paleta FIAP + Asteria criado");
    }

    private static boolean executarScriptR() throws IOException, InterruptedException {
        String[] comandosR = {
                "C:\\Program Files\\R\\R-4.5.1\\bin\\Rscript.exe",
                "Rscript"
        };

        for (String comando : comandosR) {
            try {
                System.out.println("Executando: " + comando + " com paleta FIAP + Asteria");

                ProcessBuilder pb = new ProcessBuilder(comando, R_SCRIPT_PATH);
                pb.redirectErrorStream(true);
                pb.directory(new File("."));
                Process process = pb.start();

                StringBuilder output = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String linha;
                    while ((linha = reader.readLine()) != null) {
                        output.append(linha).append("\n");
                        System.out.println("[R] " + linha);
                    }
                }

                int exitCode = process.waitFor();
                System.out.println("Código de saída: " + exitCode);

                if (exitCode == 0) {
                    System.out.println("Script R executado com sucesso usando paleta FIAP + Asteria!");
                    return true;
                } else {
                    System.err.println("R falhou com código: " + exitCode);
                }

            } catch (IOException e) {
                System.err.println("Erro ao executar " + comando + ": " + e.getMessage());
                continue;
            }
        }

        return false;
    }

    private static String criarGraficoVazio() {
        return null;
    }

    private static void limparArquivosTemp() {
        try {
            new File(R_SCRIPT_PATH).delete();
            new File(CSV_DATA_PATH).delete();
        } catch (Exception e) {
        }
    }

    public static boolean isRDisponivel() {
        return verificarRDisponivel();
    }

    public static void testarIntegracaoR() {
        System.out.println("=== TESTE DE INTEGRAÇÃO R COM PALETA FIAP + ASTERIA ===");

        if (verificarRDisponivel()) {
            System.out.println("R está disponível");
            System.out.println("Paleta FIAP + Asteria será aplicada aos gráficos");

            System.out.println("\nCORES DA PALETA:");
            System.out.println("FIAP Pink Vibrant: " + ColorPalette.toHex(ColorPalette.FIAP_PINK_VIBRANT));
            System.out.println("FIAP Pink Dark: " + ColorPalette.toHex(ColorPalette.FIAP_PINK_DARK));
            System.out.println("Asteria Midnight Blue: " + ColorPalette.toHex(ColorPalette.ASTERIA_MIDNIGHT_BLUE));
            System.out.println("Asteria Amethyst: " + ColorPalette.toHex(ColorPalette.ASTERIA_AMETHYST));
            System.out.println("Asteria Ocean Blue: " + ColorPalette.toHex(ColorPalette.ASTERIA_OCEAN_BLUE));
            System.out.println("Success Emerald: " + ColorPalette.toHex(ColorPalette.SUCCESS_EMERALD));
        } else {
            System.out.println("R não está disponível");
            System.out.println("Gráficos não poderão ser gerados");
        }

        System.out.println("=== FIM DO TESTE ===");
    }
}