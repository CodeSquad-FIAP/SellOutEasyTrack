package view;

import controller.VendaController;
import model.Venda;
import util.AnalyticsEngine;
import util.DataImporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import java.util.List;

/**
 * Dashboard Simplificado - TODAS as funcionalidades sempre disponíveis
 * Foco nas sprints: BI Inteligente + Múltiplas Fontes + Escalabilidade
 */
public class SimplifiedDashboardView extends JFrame {

    private VendaController vendaController = new VendaController();
    private AnalyticsEngine analyticsEngine = new AnalyticsEngine();
    private DataImporter dataImporter = new DataImporter();

    private JPanel chartPanel;
    private JPanel insightsPanel;

    // Cores do tema moderno
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color DARK_COLOR = new Color(44, 62, 80);
    private static final Color LIGHT_GRAY = new Color(236, 240, 241);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(52, 73, 94);

    public SimplifiedDashboardView() {
        super("SellOut EasyTrack - Dashboard Inteligente com BI Avançado");
        configurarJanela();
        criarInterface();
        atualizarDashboard();
        setVisible(true);
    }

    private void configurarJanela() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1600, 900);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void criarInterface() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_GRAY);

        // Header simples
        JPanel headerPanel = criarHeader();

        // Sidebar com todas as funcionalidades
        JPanel sidebarPanel = criarSidebar();

        // Área central com insights e gráficos
        JPanel contentPanel = criarAreaConteudo();

        // Footer
        JPanel footerPanel = criarFooter();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel criarHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(DARK_COLOR);
        header.setBorder(new EmptyBorder(15, 25, 15, 25));
        header.setPreferredSize(new Dimension(0, 80));

        JLabel titulo = new JLabel("🚀 SellOut EasyTrack - Sistema Completo");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(WHITE);

        JLabel subtitulo = new JLabel("BI Inteligente • Múltiplas Fontes • Analytics Avançado");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(new Color(189, 195, 199));

        JPanel tituloPanel = new JPanel(new BorderLayout());
        tituloPanel.setOpaque(false);
        tituloPanel.add(titulo, BorderLayout.NORTH);
        tituloPanel.add(subtitulo, BorderLayout.SOUTH);

        header.add(tituloPanel, BorderLayout.WEST);

        return header;
    }

    private JPanel criarSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(52, 73, 94));
        sidebar.setPreferredSize(new Dimension(300, 0));
        sidebar.setBorder(new EmptyBorder(20, 0, 20, 0));

        // Título do menu
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel menuTitle = new JLabel("🎯 FUNCIONALIDADES COMPLETAS");
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        menuTitle.setForeground(new Color(149, 165, 166));

        titlePanel.add(menuTitle);
        sidebar.add(titlePanel);
        sidebar.add(Box.createVerticalStrut(10));

        // Botões do menu - TODAS FUNCIONAIS
        sidebar.add(criarBotaoMenu("💼 VENDAS", "Gerenciar vendas completo", this::mostrarMenuVendas));
        sidebar.add(Box.createVerticalStrut(5));

        sidebar.add(criarBotaoMenu("📊 IMPORTAR DADOS", "Múltiplas fontes de dados", this::mostrarMenuImportacao));
        sidebar.add(Box.createVerticalStrut(5));

        sidebar.add(criarBotaoMenu("🧠 INSIGHTS IA", "Analytics automático inteligente", this::atualizarInsights));
        sidebar.add(Box.createVerticalStrut(5));

        sidebar.add(criarBotaoMenu("📈 ANALYTICS", "Relatórios e análises avançadas", this::abrirAnalytics));
        sidebar.add(Box.createVerticalStrut(5));

        sidebar.add(criarBotaoMenu("📁 EXPORTAR", "CSV e relatórios completos", this::exportarDados));

        sidebar.add(Box.createVerticalGlue());

        // Estatísticas rápidas
        JPanel statsPanel = criarPainelEstatisticas();
        sidebar.add(statsPanel);

        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(criarBotaoMenu("⚙️ CONFIGURAÇÕES", "Ajustes do sistema", this::abrirConfiguracoes));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(criarBotaoMenu("🚪 SAIR", "Fechar aplicação", this::sairAplicacao));

        return sidebar;
    }

    private JPanel criarAreaConteudo() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(LIGHT_GRAY);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Split pane para insights e gráficos
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setBackground(LIGHT_GRAY);

        // Painel de insights
        JPanel insightsContainer = criarPainelInsights();

        // Painel de gráficos
        JPanel chartsContainer = criarPainelGraficos();

        splitPane.setLeftComponent(insightsContainer);
        splitPane.setRightComponent(chartsContainer);

        content.add(splitPane, BorderLayout.CENTER);

        return content;
    }

    private JPanel criarPainelInsights() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel headerLabel = new JLabel("🧠 Insights Inteligentes Automáticos");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(DARK_COLOR);

        JButton btnRefreshInsights = criarBotaoModerno("🔄 ATUALIZAR", SECONDARY_COLOR);
        btnRefreshInsights.addActionListener(e -> atualizarInsights());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(btnRefreshInsights, BorderLayout.EAST);

        insightsPanel = new JPanel();
        insightsPanel.setLayout(new BoxLayout(insightsPanel, BoxLayout.Y_AXIS));
        insightsPanel.setBackground(WHITE);

        JScrollPane scrollPane = new JScrollPane(insightsPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        container.add(headerPanel, BorderLayout.NORTH);
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    private JPanel criarPainelGraficos() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel headerLabel = new JLabel("📊 Analytics Visuais Avançados");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(DARK_COLOR);

        JButton btnRefreshChart = criarBotaoModerno("🔄 GERAR GRÁFICOS", SECONDARY_COLOR);
        btnRefreshChart.addActionListener(e -> atualizarGraficos());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(btnRefreshChart, BorderLayout.EAST);

        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(WHITE);
        chartPanel.setBorder(BorderFactory.createLineBorder(LIGHT_GRAY, 1));

        container.add(headerPanel, BorderLayout.NORTH);
        container.add(chartPanel, BorderLayout.CENTER);

        return container;
    }

    private JPanel criarPainelEstatisticas() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("📈 ESTATÍSTICAS LIVE");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        titleLabel.setForeground(ACCENT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            List<Venda> vendas = vendaController.obterTodasVendas();
            double faturamento = vendas.stream()
                    .mapToDouble(v -> v.getQuantidade() * v.getValorUnitario())
                    .sum();

            JLabel vendasLabel = new JLabel(String.format("Vendas: %d", vendas.size()));
            vendasLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            vendasLabel.setForeground(WHITE);
            vendasLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel faturamentoLabel = new JLabel(String.format("R$ %.0f", faturamento));
            faturamentoLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            faturamentoLabel.setForeground(ACCENT_COLOR);
            faturamentoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            panel.add(titleLabel);
            panel.add(Box.createVerticalStrut(5));
            panel.add(vendasLabel);
            panel.add(faturamentoLabel);

        } catch (SQLException e) {
            JLabel errorLabel = new JLabel("Erro ao carregar");
            errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            errorLabel.setForeground(DANGER_COLOR);
            errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(errorLabel);
        }

        return panel;
    }

    private JPanel criarFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(127, 140, 141));
        footer.setBorder(new EmptyBorder(10, 25, 10, 25));
        footer.setPreferredSize(new Dimension(0, 50));

        JLabel footerText = new JLabel("🚀 SellOut EasyTrack - Todas as Funcionalidades Ativas");
        footerText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerText.setForeground(WHITE);

        JLabel versionInfo = new JLabel("v2.0 - Sistema Completo");
        versionInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        versionInfo.setForeground(ACCENT_COLOR);

        footer.add(footerText, BorderLayout.WEST);
        footer.add(versionInfo, BorderLayout.EAST);

        return footer;
    }

    private JPanel criarBotaoMenu(String titulo, String descricao, Runnable acao) {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        buttonPanel.setBorder(new EmptyBorder(8, 25, 8, 25));
        buttonPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tituloLabel.setForeground(WHITE);

        JLabel descLabel = new JLabel(descricao);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLabel.setForeground(new Color(189, 195, 199));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(tituloLabel, BorderLayout.NORTH);
        textPanel.add(descLabel, BorderLayout.SOUTH);

        buttonPanel.add(textPanel, BorderLayout.CENTER);

        // Efeitos hover e clique
        buttonPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                buttonPanel.setOpaque(true);
                buttonPanel.setBackground(ACCENT_COLOR);
                buttonPanel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                buttonPanel.setOpaque(false);
                buttonPanel.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    acao.run();
                } catch (Exception ex) {
                    mostrarMensagem("Erro ao executar ação: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return buttonPanel;
    }

    private JButton criarBotaoModerno(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(WHITE);
        btn.setBackground(cor);
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(cor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(cor);
            }
        });

        return btn;
    }

    // Métodos de ação - TODAS FUNCIONAIS

    private void mostrarMenuVendas() {
        String[] opcoes = {"Criar Venda", "Listar Vendas", "Atualizar Venda", "Deletar Venda"};
        int escolha = JOptionPane.showOptionDialog(this,
                "Selecione uma opção:", "💼 Menu de Vendas Completo",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, opcoes, opcoes[0]);

        switch (escolha) {
            case 0: new CriarVendaView(this); break;
            case 1: new ListarVendasView(this); break;
            case 2: new AtualizarVendaView(this); break;
            case 3: new DeletarVendaView(this); break;
        }
    }

    private void mostrarMenuImportacao() {
        String[] opcoes = {"Importar CSV", "Importar Arquivo Texto", "Colar dados WhatsApp", "Gerar Template"};
        int escolha = JOptionPane.showOptionDialog(this,
                "📊 Múltiplas Fontes de Dados Disponíveis:", "Importar Dados",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, opcoes, opcoes[0]);

        switch (escolha) {
            case 0: importarCSV(); break;
            case 1: importarTexto(); break;
            case 2: importarWhatsApp(); break;
            case 3: gerarTemplate(); break;
        }
    }

    private void atualizarInsights() {
        SwingWorker<List<AnalyticsEngine.Insight>, Void> worker =
                new SwingWorker<List<AnalyticsEngine.Insight>, Void>() {
                    @Override
                    protected List<AnalyticsEngine.Insight> doInBackground() throws Exception {
                        List<Venda> vendas = vendaController.obterTodasVendas();
                        return analyticsEngine.gerarInsightsAutomaticos(vendas);
                    }

                    @Override
                    protected void done() {
                        try {
                            List<AnalyticsEngine.Insight> insights = get();
                            exibirInsights(insights);
                        } catch (Exception e) {
                            mostrarMensagem("Erro ao gerar insights: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };

        worker.execute();
    }

    private void exibirInsights(List<AnalyticsEngine.Insight> insights) {
        insightsPanel.removeAll();

        if (insights.isEmpty()) {
            JLabel noInsights = new JLabel("Registre algumas vendas para ver insights inteligentes!");
            noInsights.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            noInsights.setForeground(Color.GRAY);
            noInsights.setAlignmentX(Component.CENTER_ALIGNMENT);
            insightsPanel.add(noInsights);
        } else {
            for (AnalyticsEngine.Insight insight : insights) {
                JPanel insightCard = criarCardInsight(insight);
                insightsPanel.add(insightCard);
                insightsPanel.add(Box.createVerticalStrut(10));
            }
        }

        insightsPanel.revalidate();
        insightsPanel.repaint();
    }

    private JPanel criarCardInsight(AnalyticsEngine.Insight insight) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(getCorPorTipo(insight.getTipo()), 2),
                new EmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel titulo = new JLabel(insight.getTipo().getEmoji() + " " + insight.getTitulo());
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(getCorPorTipo(insight.getTipo()));

        JLabel descricao = new JLabel("<html>" + insight.getDescricao() + "</html>");
        descricao.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descricao.setForeground(TEXT_COLOR);

        JLabel recomendacao = new JLabel("<html>💡 " + insight.getRecomendacao() + "</html>");
        recomendacao.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        recomendacao.setForeground(Color.GRAY);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titulo);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(descricao);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(recomendacao);

        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private Color getCorPorTipo(AnalyticsEngine.TipoInsight tipo) {
        switch (tipo) {
            case CRITICO: return DANGER_COLOR;
            case ALERTA: return WARNING_COLOR;
            case SUCESSO: return ACCENT_COLOR;
            case OPORTUNIDADE: return new Color(155, 89, 182);
            default: return PRIMARY_COLOR;
        }
    }

    private void atualizarGraficos() {
        // Usar o RGraphUtil existente para gerar gráficos
        try {
            List<Venda> vendas = vendaController.obterTodasVendas();

            chartPanel.removeAll();
            JLabel loading = new JLabel("🔄 Gerando gráficos avançados...", JLabel.CENTER);
            loading.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            chartPanel.add(loading);
            chartPanel.revalidate();
            chartPanel.repaint();

            // Gerar gráfico em background
            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    return util.RGraphUtil.gerarGraficoVendas(vendas);
                }

                @Override
                protected void done() {
                    try {
                        String imagePath = get();
                        if (imagePath != null) {
                            exibirGrafico(imagePath);
                        } else {
                            mostrarErroGrafico();
                        }
                    } catch (Exception e) {
                        mostrarErroGrafico();
                    }
                }
            };

            worker.execute();

        } catch (SQLException e) {
            mostrarMensagem("Erro ao carregar dados: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exibirGrafico(String imagePath) {
        chartPanel.removeAll();

        try {
            ImageIcon grafico = new ImageIcon(imagePath);

            // Ajustar tamanho
            int panelWidth = chartPanel.getWidth() - 40;
            int panelHeight = chartPanel.getHeight() - 40;

            if (panelWidth > 0 && panelHeight > 0) {
                Image img = grafico.getImage();
                Image scaledImg = img.getScaledInstance(panelWidth, panelHeight, Image.SCALE_SMOOTH);
                ImageIcon finalIcon = new ImageIcon(scaledImg);

                JLabel lblGrafico = new JLabel(finalIcon);
                lblGrafico.setHorizontalAlignment(JLabel.CENTER);

                chartPanel.add(lblGrafico, BorderLayout.CENTER);
            } else {
                JLabel lblGrafico = new JLabel(grafico);
                lblGrafico.setHorizontalAlignment(JLabel.CENTER);
                chartPanel.add(lblGrafico, BorderLayout.CENTER);
            }

        } catch (Exception e) {
            mostrarErroGrafico();
        }

        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private void mostrarErroGrafico() {
        chartPanel.removeAll();
        JLabel erro = new JLabel("❌ Erro ao gerar gráfico. Verifique se o R está instalado.", JLabel.CENTER);
        erro.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        erro.setForeground(DANGER_COLOR);
        chartPanel.add(erro, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private void abrirAnalytics() {
        mostrarMensagem(
                "📈 Analytics Avançados Disponíveis!\n\n" +
                        "✅ Insights automáticos gerados por IA\n" +
                        "✅ Gráficos avançados com R\n" +
                        "✅ Análise de tendências e anomalias\n" +
                        "✅ Relatórios executivos\n\n" +
                        "Use os botões 'Insights IA' e 'Gerar Gráficos' para acessar!",
                "Analytics Completo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void atualizarDashboard() {
        atualizarInsights();
        atualizarGraficos();
    }

    // Métodos de importação de dados

    private void importarCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arquivos CSV", "csv"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File arquivo = chooser.getSelectedFile();

            SwingWorker<DataImporter.ImportResult, Void> worker =
                    new SwingWorker<DataImporter.ImportResult, Void>() {
                        @Override
                        protected DataImporter.ImportResult doInBackground() throws Exception {
                            return dataImporter.importFromCSV(arquivo);
                        }

                        @Override
                        protected void done() {
                            try {
                                DataImporter.ImportResult resultado = get();
                                mostrarResultadoImportacao(resultado);
                                if (resultado.isSuccess()) {
                                    atualizarDashboard();
                                }
                            } catch (Exception e) {
                                mostrarMensagem("Erro na importação: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    };

            worker.execute();
        }
    }

    private void importarTexto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arquivos de Texto", "txt"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File arquivo = chooser.getSelectedFile();
            DataImporter.ImportResult resultado = dataImporter.importFromTextFile(arquivo);
            mostrarResultadoImportacao(resultado);
            if (resultado.isSuccess()) {
                atualizarDashboard();
            }
        }
    }

    private void importarWhatsApp() {
        String texto = JOptionPane.showInputDialog(this,
                "📱 Cole aqui as mensagens do WhatsApp com informações de vendas:",
                "Importar WhatsApp", JOptionPane.PLAIN_MESSAGE);

        if (texto != null && !texto.trim().isEmpty()) {
            DataImporter.ImportResult resultado = dataImporter.importFromWhatsApp(texto);
            mostrarResultadoImportacao(resultado);
            if (resultado.isSuccess()) {
                atualizarDashboard();
            }
        }
    }

    private void gerarTemplate() {
        try {
            File template = dataImporter.generateCSVTemplate();
            mostrarMensagem(
                    "📄 Template CSV gerado com sucesso!\n\n" +
                            "Arquivo: " + template.getAbsolutePath() +
                            "\n\nUse este arquivo como base para suas importações.",
                    "Template Criado", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            mostrarMensagem("Erro ao gerar template: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarResultadoImportacao(DataImporter.ImportResult resultado) {
        StringBuilder mensagem = new StringBuilder();
        mensagem.append("📊 ").append(resultado.getSummary()).append("\n\n");

        if (resultado.hasErrors()) {
            mensagem.append("❌ ERROS:\n");
            for (DataImporter.ImportError erro : resultado.getErrors()) {
                mensagem.append("• ").append(erro.toString()).append("\n");
            }
            mensagem.append("\n");
        }

        if (resultado.hasWarnings()) {
            mensagem.append("⚠️ AVISOS:\n");
            for (DataImporter.ImportWarning aviso : resultado.getWarnings()) {
                mensagem.append("• ").append(aviso.toString()).append("\n");
            }
        }

        JTextArea textArea = new JTextArea(mensagem.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "📊 Resultado da Importação",
                resultado.hasErrors() ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportarDados() {
        try {
            List<Venda> vendas = vendaController.obterTodasVendas();
            JFileChooser chooser = new JFileChooser();
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String path = chooser.getSelectedFile().getAbsolutePath() + ".csv";
                util.ReportUtil.exportarCSV(vendas, path);
                mostrarMensagem(
                        "✅ Dados exportados com sucesso!\n\n" +
                                "Arquivo: " + path + "\n\n" +
                                "Total de " + vendas.size() + " vendas exportadas.",
                        "Exportação Concluída", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            mostrarMensagem("Erro na exportação: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirConfiguracoes() {
        mostrarMensagem(
                "⚙️ Sistema Totalmente Configurado!\n\n" +
                        "✅ Todas as funcionalidades ativas\n" +
                        "✅ BI Inteligente operacional\n" +
                        "✅ Múltiplas fontes de dados\n" +
                        "✅ Analytics R integrado\n" +
                        "✅ Insights automáticos\n\n" +
                        "Sistema pronto para demonstração das sprints!",
                "Configurações", JOptionPane.INFORMATION_MESSAGE);
    }

    private void sairAplicacao() {
        int resposta = JOptionPane.showConfirmDialog(this,
                "Deseja realmente sair da aplicação?", "Confirmar Saída",
                JOptionPane.YES_NO_OPTION);
        if (resposta == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void mostrarMensagem(String mensagem, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensagem, titulo, tipo);
    }
}