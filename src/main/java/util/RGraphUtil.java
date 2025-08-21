package util;

import model.Venda;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class RGraphUtil {

    private static final String R_SCRIPT_PATH = "temp_graph_script.R";
    private static final String CSV_DATA_PATH = "temp_vendas_data.csv";
    private static final String OUTPUT_IMAGE_PATH = "vendas_grafico.png";

    public static String gerarGraficoVendas(List<Venda> vendas) {
        System.out.println("🔍 [DEBUG] Iniciando geração de gráfico R...");

        try {
            // 1. Verificar se R está disponível
            if (!verificarRDisponivel()) {
                System.err.println("❌ R não está disponível no sistema!");
                return null;
            }

            // 2. Prepara os dados agregados por produto
            Map<String, Integer> dadosAgregados = agregareVendasPorProduto(vendas);
            System.out.println("📊 [DEBUG] Dados agregados: " + dadosAgregados);

            if (dadosAgregados.isEmpty()) {
                System.out.println("⚠️ [DEBUG] Nenhum dado para gerar gráfico");
                return criarGraficoVazio();
            }

            // 3. Cria arquivo CSV temporário com os dados
            criarArquivoCSVTemp(dadosAgregados);
            System.out.println("📄 [DEBUG] Arquivo CSV criado: " + CSV_DATA_PATH);

            // 4. Gera script R simplificado
            criarScriptRSimplificado();
            System.out.println("📝 [DEBUG] Script R criado: " + R_SCRIPT_PATH);

            // 5. Executa o script R
            boolean sucesso = executarScriptR();

            if (sucesso) {
                // 6. Verifica se a imagem foi gerada
                File imagemGerada = new File(OUTPUT_IMAGE_PATH);
                if (imagemGerada.exists()) {
                    System.out.println("✅ [DEBUG] Gráfico gerado com sucesso: " + imagemGerada.getAbsolutePath());
                    return imagemGerada.getAbsolutePath();
                } else {
                    System.err.println("❌ [DEBUG] Arquivo de imagem não foi criado");
                }
            }

        } catch (Exception e) {
            System.err.println("❌ [DEBUG] Erro ao gerar gráfico R: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Não limpa arquivos para debug
            // limparArquivosTemp();
        }

        return null;
    }

    private static boolean verificarRDisponivel() {
        String[] comandosR = {
                "C:\\Program Files\\R\\R-4.5.1\\bin\\Rscript.exe",
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
                    System.out.println("✅ [DEBUG] R encontrado com comando: " + comando);
                    return true;
                }
            } catch (Exception e) {
                System.out.println("⚠️ [DEBUG] Comando " + comando + " não funcionou: " + e.getMessage());
            }
        }

        System.err.println("❌ [DEBUG] R não encontrado em nenhum dos comandos testados");
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
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_DATA_PATH))) {
            writer.println("Produto,Quantidade");
            for (Map.Entry<String, Integer> entry : dados.entrySet()) {
                writer.println("\"" + entry.getKey() + "\"," + entry.getValue());
            }
        }
        System.out.println("📄 [DEBUG] CSV criado com " + dados.size() + " produtos");
    }

    private static void criarScriptRSimplificado() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(R_SCRIPT_PATH))) {
            writer.println("# Script R simplificado para gerar gráfico de vendas");
            writer.println("cat('Iniciando script R...\\n')");
            writer.println("");

            // Carregar pacotes
            writer.println("library(ggplot2)");
            writer.println("");

            writer.println("# Ler dados");
            writer.println("dados <- read.csv('" + CSV_DATA_PATH + "', stringsAsFactors = FALSE)");
            writer.println("cat(paste('Dados lidos:', nrow(dados), 'linhas\\n'))");
            writer.println("");

            writer.println("# Ordenar dados");
            writer.println("dados <- dados[order(-dados$Quantidade), ]");
            writer.println("if(nrow(dados) > 10) dados <- dados[1:10, ]");
            writer.println("");

            writer.println("# Criar gráfico vertical em 1080p");
            writer.println("cat('Criando gráfico 1080p...\\n')");
            writer.println("grafico <- ggplot(dados, aes(x = reorder(Produto, Quantidade), y = Quantidade)) +");
            writer.println("  geom_col(fill = '#3498db', color = '#2980b9', linewidth = 1.0) +");
            writer.println("  geom_text(aes(label = Quantidade), vjust = -0.5, size = 5, fontface = 'bold', color = '#2c3e50') +");
            writer.println("  labs(");
            writer.println("    title = 'Produtos Mais Vendidos',");
            writer.println("    subtitle = 'SellOut EasyTrack - Sistema de Vendas',");
            writer.println("    x = 'Produtos',");
            writer.println("    y = 'Quantidade Vendida',");
            writer.println("    caption = 'Gerado automaticamente'");
            writer.println("  ) +");
            writer.println("  scale_y_continuous(expand = c(0, 0, 0.1, 0)) +");
            writer.println("  theme_minimal(base_size = 14) +");  // Fonte base otimizada para 1080p
            writer.println("  theme(");
            writer.println("    plot.title = element_text(size = 20, face = 'bold', hjust = 0.5, color = '#2c3e50', margin = margin(b = 8)),");
            writer.println("    plot.subtitle = element_text(size = 14, hjust = 0.5, color = '#7f8c8d', margin = margin(b = 15)),");
            writer.println("    plot.caption = element_text(size = 10, color = '#95a5a6', hjust = 1),");
            writer.println("    axis.title.x = element_text(size = 16, face = 'bold', color = '#34495e', margin = margin(t = 10)),");
            writer.println("    axis.title.y = element_text(size = 16, face = 'bold', color = '#34495e', margin = margin(r = 10)),");
            writer.println("    axis.text.x = element_text(size = 12, angle = 45, hjust = 1, color = '#2c3e50'),");
            writer.println("    axis.text.y = element_text(size = 12, color = '#2c3e50'),");
            writer.println("    panel.grid.minor = element_blank(),");
            writer.println("    panel.grid.major.x = element_blank(),");
            writer.println("    panel.grid.major.y = element_line(color = '#ecf0f1', linewidth = 0.6),");
            writer.println("    plot.background = element_rect(fill = 'white', color = NA),");
            writer.println("    panel.background = element_rect(fill = 'white', color = NA),");
            writer.println("    plot.margin = margin(20, 20, 20, 20)");  // Margens otimizadas
            writer.println("  )");
            writer.println("");

            writer.println("# Salvar gráfico em 1080p Full HD");
            writer.println("cat('Salvando gráfico em 1080p...\\n')");
            writer.println("ggsave(");
            writer.println("  filename = '" + OUTPUT_IMAGE_PATH + "',");
            writer.println("  plot = grafico,");
            writer.println("  width = 12,");           // Largura otimizada para 1080p
            writer.println("  height = 9,");           // Altura otimizada para 1080p (16:9)
            writer.println("  dpi = 400,");            // DPI ideal para 1080p
            writer.println("  units = 'in',");
            writer.println("  device = 'png',");
            writer.println("  type = 'cairo-png'");    // Renderização Cairo para qualidade
            writer.println(")");
            writer.println("");

            writer.println("# Verificar resultado");
            writer.println("if(file.exists('" + OUTPUT_IMAGE_PATH + "')) {");
            writer.println("  cat('Grafico salvo com sucesso!\\n')");
            writer.println("} else {");
            writer.println("  stop('Erro ao salvar grafico')");
            writer.println("}");
            writer.println("");
            writer.println("cat('Script concluido!\\n')");
        }
        System.out.println("📝 [DEBUG] Script R simplificado criado");
    }

    private static boolean executarScriptR() throws IOException, InterruptedException {
        String[] comandosR = {
                "C:\\Program Files\\R\\R-4.5.1\\bin\\Rscript.exe",
                "Rscript"
        };

        for (String comando : comandosR) {
            try {
                System.out.println("🔄 [DEBUG] Executando: " + comando);

                ProcessBuilder pb = new ProcessBuilder(comando, R_SCRIPT_PATH);
                pb.redirectErrorStream(true);
                pb.directory(new File("."));
                Process process = pb.start();

                // Captura TODA a saída do R
                StringBuilder output = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String linha;
                    while ((linha = reader.readLine()) != null) {
                        output.append(linha).append("\n");
                        System.out.println("📟 [R] " + linha);
                    }
                }

                int exitCode = process.waitFor();
                System.out.println("🔍 [DEBUG] Código de saída: " + exitCode);

                if (exitCode == 0) {
                    System.out.println("✅ [DEBUG] Script R executado com sucesso!");
                    return true;
                } else {
                    System.err.println("❌ [DEBUG] R falhou com código: " + exitCode);
                }

            } catch (IOException e) {
                System.err.println("❌ [DEBUG] Erro ao executar " + comando + ": " + e.getMessage());
                continue;
            }
        }

        return false;
    }

    private static String criarGraficoVazio() {
        // Implementação para gráfico vazio (placeholder)
        return null;
    }

    private static void limparArquivosTemp() {
        try {
            new File(R_SCRIPT_PATH).delete();
            new File(CSV_DATA_PATH).delete();
        } catch (Exception e) {
            // Ignora erros na limpeza
        }
    }

    public static boolean isRDisponivel() {
        return verificarRDisponivel();
    }

    public static void testarIntegracaoR() {
        System.out.println("=== TESTE DE INTEGRAÇÃO R ===");

        if (verificarRDisponivel()) {
            System.out.println("✅ R está disponível");
        } else {
            System.out.println("❌ R não está disponível");
        }

        System.out.println("=== FIM DO TESTE ===");
    }
}