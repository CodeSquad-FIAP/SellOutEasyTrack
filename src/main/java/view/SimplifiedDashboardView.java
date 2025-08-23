package view;

import controller.VendaController;
import model.Venda;
import util.AnalyticsEngine;
import util.DataImporter;
import listener.VendaListener;
import util.ColorPalette;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class SimplifiedDashboardView extends JFrame implements VendaListener {

    private final VendaController vendaController = new VendaController();
    private final AnalyticsEngine analyticsEngine = new AnalyticsEngine();
    private final DataImporter dataImporter = new DataImporter();

    private JPanel chartPanel;
    private JPanel insightsPanel;
    private JPanel statsPanel;

    public SimplifiedDashboardView() {
        super("SellOut EasyTrack - Dashboard Inteligente");
        configurarJanela();
        criarInterface();
        atualizarDashboard();
        setVisible(true);
    }

    @Override
    public void onVendasChanged() {
        atualizarDashboard();
    }

    private void configurarJanela() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1600, 900);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void criarInterface() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel headerPanel = criarHeader();
        JPanel sidebarPanel = criarSidebar();
        JPanel contentPanel = criarAreaConteudo();
        JPanel footerPanel = criarFooter();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel criarHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(38, 38, 38));
        header.setBorder(new EmptyBorder(15, 25, 15, 25));
        header.setPreferredSize(new Dimension(0, 80));

        JLabel titulo = new JLabel("SellOut EasyTrack - Sistema Completo");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel("BI Inteligente - Múltiplas Fontes - Analytics Avançado");
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
        sidebar.setName("sidebar");
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(38, 38, 38));
        sidebar.setPreferredSize(new Dimension(300, 0));
        sidebar.setBorder(new EmptyBorder(20, 0, 20, 0));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel menuTitle = new JLabel("FUNCIONALIDADES COMPLETAS");
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        menuTitle.setForeground(new Color(149, 165, 166));

        titlePanel.add(menuTitle);
        sidebar.add(titlePanel);
        sidebar.add(Box.createVerticalStrut(10));

        sidebar.add(criarBotaoMenu("VENDAS", "Gerenciar vendas completo", this::mostrarMenuVendas));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(criarBotaoMenu("IMPORTAR DADOS", "Múltiplas fontes de dados", this::mostrarMenuImportacao));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(criarBotaoMenu("INSIGHTS IA", "Analytics automático inteligente", this::atualizarInsights));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(criarBotaoMenu("ANALYTICS", "Relatórios e análises avançadas", this::abrirAnalytics));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(criarBotaoMenu("EXPORTAR", "CSV e relatórios completos", this::exportarDados));

        sidebar.add(Box.createVerticalGlue());

        statsPanel = criarPainelEstatisticas();
        statsPanel.setName("statsPanel");
        sidebar.add(statsPanel);

        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(criarBotaoMenu("SAIR", "Fechar aplicação", this::sairAplicacao));

        return sidebar;
    }

    private JPanel criarAreaConteudo() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(64, 64, 64));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);
        splitPane.setDividerSize(5);

        JPanel insightsContainer = criarPainelInsights();
        JPanel chartsContainer = criarPainelGraficos();

        splitPane.setLeftComponent(insightsContainer);
        splitPane.setRightComponent(chartsContainer);

        content.add(splitPane, BorderLayout.CENTER);

        return content;
    }

    private JPanel criarPainelInsights() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor")),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel headerLabel = new JLabel("Insights Inteligentes Automáticos");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton btnRefreshInsights = criarBotaoModerno("ATUALIZAR", new Color(242, 48, 100));
        btnRefreshInsights.addActionListener(e -> atualizarInsights());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(btnRefreshInsights, BorderLayout.EAST);

        insightsPanel = new JPanel();
        insightsPanel.setLayout(new BoxLayout(insightsPanel, BoxLayout.Y_AXIS));
        insightsPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(insightsPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        container.add(headerPanel, BorderLayout.NORTH);
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    private JPanel criarPainelGraficos() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor")),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel headerLabel = new JLabel("Analytics Visuais Avançados");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton btnRefreshChart = criarBotaoModerno("GERAR GRÁFICOS", new Color(242, 48, 100));
        btnRefreshChart.addActionListener(e -> atualizarGraficos());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(btnRefreshChart, BorderLayout.EAST);

        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setOpaque(false);
        chartPanel.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor")));
        chartPanel.setMinimumSize(new Dimension(600, 400));

        container.add(headerPanel, BorderLayout.NORTH);
        container.add(chartPanel, BorderLayout.CENTER);

        return container;
    }

    private JPanel criarPainelEstatisticas() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("ESTATÍSTICAS LIVE");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            List<Venda> vendas = vendaController.obterTodasVendas();
            double faturamento = vendas.stream()
                    .mapToDouble(v -> v.getQuantidade() * v.getValorUnitario())
                    .sum();

            JLabel vendasLabel = new JLabel(String.format("Vendas: %d", vendas.size()));
            vendasLabel.setForeground(Color.WHITE);
            vendasLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel faturamentoLabel = new JLabel(String.format("R$ %.0f", faturamento));
            faturamentoLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            faturamentoLabel.setForeground(new Color(242, 48, 100));
            faturamentoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            panel.add(titleLabel);
            panel.add(Box.createVerticalStrut(5));
            panel.add(vendasLabel);
            panel.add(faturamentoLabel);

        } catch (SQLException e) {
            JLabel errorLabel = new JLabel("Erro ao carregar");
            errorLabel.setForeground(new Color(231, 76, 60));
            errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(errorLabel);
        }

        return panel;
    }

    private JPanel criarFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(38, 38, 38));
        footer.setBorder(new EmptyBorder(0, 25, 0, 25));
        footer.setPreferredSize(new Dimension(0, 50));

        JLabel footerText = new JLabel("SellOut EasyTrack");
        footerText.setForeground(Color.WHITE);

        JLabel versionInfo = new JLabel("v2.0 - Sistema Completo");
        versionInfo.setForeground(Color.WHITE);

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
        tituloLabel.setForeground(Color.WHITE);

        JLabel descLabel = new JLabel(descricao);
        descLabel.setForeground(new Color(189, 195, 199));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(tituloLabel, BorderLayout.NORTH);
        textPanel.add(descLabel, BorderLayout.SOUTH);

        buttonPanel.add(textPanel, BorderLayout.CENTER);

        buttonPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                buttonPanel.setOpaque(true);
                buttonPanel.setBackground(new Color(191, 59, 94));
                buttonPanel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                buttonPanel.setOpaque(false);
                buttonPanel.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                buttonPanel.setBackground(new Color(242, 48, 100).darker());
                buttonPanel.repaint();

                Timer timer = new Timer(100, evt -> {
                    try {
                        acao.run();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(SimplifiedDashboardView.this,
                                "Erro ao executar ação: " + ex.getMessage(),
                                "Erro",
                                JOptionPane.ERROR_MESSAGE);
                    } finally {
                        buttonPanel.setOpaque(false);
                        buttonPanel.repaint();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });

        return buttonPanel;
    }

    private JButton criarBotaoModerno(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setForeground(Color.WHITE);
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

    private JButton criarBotaoDialog(String texto, String descricao) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout());
        btn.setBackground(new Color(60, 63, 65));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(400, 70));
        btn.setMaximumSize(new Dimension(400, 70));
        btn.setMinimumSize(new Dimension(400, 70));

        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));

        JLabel lblTitulo = new JLabel(texto);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblDesc = new JLabel("<html><div style='width: 350px;'>" + descricao + "</div></html>");
        lblDesc.setForeground(ColorPalette.ASTERIA_SILVER);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(lblTitulo, BorderLayout.NORTH);
        textPanel.add(Box.createVerticalStrut(3), BorderLayout.CENTER);
        textPanel.add(lblDesc, BorderLayout.SOUTH);

        btn.add(textPanel, BorderLayout.CENTER);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(75, 78, 80));
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(242, 48, 100), 2),
                        BorderFactory.createEmptyBorder(11, 14, 11, 14)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(60, 63, 65));
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(80, 80, 80)),
                        BorderFactory.createEmptyBorder(12, 15, 12, 15)));
            }
        });

        return btn;
    }

    private void mostrarMenuVendas() {
        JDialog dialog = new JDialog(this, "Menu de Vendas Completo", true);
        dialog.setSize(480, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(242, 48, 100));
        headerPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        JLabel titulo = new JLabel("MENU DE VENDAS", JLabel.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        headerPanel.add(titulo);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(64, 64, 64));

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setOpaque(false);
        optionsPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JButton btnCriar = criarBotaoDialog("Criar Venda", "Registrar nova venda no sistema");
        JButton btnListar = criarBotaoDialog("Listar Vendas", "Visualizar todas as vendas registradas");
        JButton btnAtualizar = criarBotaoDialog("Atualizar Venda", "Editar informações de venda existente");
        JButton btnDeletar = criarBotaoDialog("Deletar Venda", "Remover venda do sistema");

        btnCriar.addActionListener(e -> { dialog.dispose(); new CriarVendaView(this, this); });
        btnListar.addActionListener(e -> { dialog.dispose(); new ListarVendasView(this); });
        btnAtualizar.addActionListener(e -> { dialog.dispose(); new AtualizarVendaView(this, this); });
        btnDeletar.addActionListener(e -> { dialog.dispose(); new DeletarVendaView(this, this); });

        btnCriar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnListar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAtualizar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDeletar.setAlignmentX(Component.CENTER_ALIGNMENT);

        optionsPanel.add(Box.createVerticalGlue());
        optionsPanel.add(btnCriar);
        optionsPanel.add(Box.createVerticalStrut(8));
        optionsPanel.add(btnListar);
        optionsPanel.add(Box.createVerticalStrut(8));
        optionsPanel.add(btnAtualizar);
        optionsPanel.add(Box.createVerticalStrut(8));
        optionsPanel.add(btnDeletar);
        optionsPanel.add(Box.createVerticalGlue());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(140, 140, 140));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnCancelar.addActionListener(e -> dialog.dispose());
        buttonPanel.add(btnCancelar);

        mainPanel.add(optionsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(mainPanel, BorderLayout.CENTER);

        dialog.setVisible(true);
    }

    private void mostrarMenuImportacao() {
        JDialog dialog = new JDialog(this, "Importar Dados - Múltiplas Fontes", true);
        dialog.setSize(480, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(242, 48, 100));
        headerPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        JLabel titulo = new JLabel("IMPORTAR DADOS", JLabel.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        headerPanel.add(titulo);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(64, 64, 64));

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setOpaque(false);
        optionsPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JButton btnCSV = criarBotaoDialog("Importar CSV", "Carregar arquivo CSV estruturado");
        JButton btnTexto = criarBotaoDialog("Importar Arquivo Texto", "Processar arquivo TXT com dados");
        JButton btnWhatsApp = criarBotaoDialog("Colar dados WhatsApp", "Importar mensagens do WhatsApp");
        JButton btnTemplate = criarBotaoDialog("Gerar Template", "Criar arquivo modelo para importação");

        btnCSV.addActionListener(e -> { dialog.dispose(); importarCSV(); });
        btnTexto.addActionListener(e -> { dialog.dispose(); importarTexto(); });
        btnWhatsApp.addActionListener(e -> { dialog.dispose(); importarWhatsApp(); });
        btnTemplate.addActionListener(e -> { dialog.dispose(); gerarTemplate(); });

        btnCSV.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnTexto.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnWhatsApp.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnTemplate.setAlignmentX(Component.CENTER_ALIGNMENT);

        optionsPanel.add(Box.createVerticalGlue());
        optionsPanel.add(btnCSV);
        optionsPanel.add(Box.createVerticalStrut(8));
        optionsPanel.add(btnTexto);
        optionsPanel.add(Box.createVerticalStrut(8));
        optionsPanel.add(btnWhatsApp);
        optionsPanel.add(Box.createVerticalStrut(8));
        optionsPanel.add(btnTemplate);
        optionsPanel.add(Box.createVerticalGlue());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(140, 140, 140));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnCancelar.addActionListener(e -> dialog.dispose());
        buttonPanel.add(btnCancelar);

        mainPanel.add(optionsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(mainPanel, BorderLayout.CENTER);

        dialog.setVisible(true);
    }

    private void atualizarInsights() {
        System.out.println(" Atualizando insights...");

        insightsPanel.removeAll();

        JLabel loading = new JLabel("Gerando insights inteligentes...", JLabel.CENTER);
        loading.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        loading.setForeground(Color.GRAY);
        insightsPanel.add(loading);
        insightsPanel.revalidate();
        insightsPanel.repaint();

        SwingWorker<List<AnalyticsEngine.Insight>, Void> worker =
                new SwingWorker<>() {
                    @Override
                    protected List<AnalyticsEngine.Insight> doInBackground() throws Exception {
                        List<Venda> vendas = vendaController.obterTodasVendas();
                        System.out.println(" Carregando " + vendas.size() + " vendas para insights");
                        return analyticsEngine.gerarInsightsAutomaticos(vendas);
                    }

                    @Override
                    protected void done() {
                        try {
                            List<AnalyticsEngine.Insight> insights = get();
                            System.out.println(" " + insights.size() + " insights gerados");
                            exibirInsights(insights);
                        } catch (Exception e) {
                            System.err.println(" Erro ao gerar insights: " + e.getMessage());
                            mostrarErroInsights("Erro ao gerar insights: " + e.getMessage());
                        }
                    }
                };

        worker.execute();
    }

    private void exibirInsights(List<AnalyticsEngine.Insight> insights) {
        insightsPanel.removeAll();

        if (insights.isEmpty()) {
            JLabel noInsights = new JLabel("<html><center>Registre algumas vendas para ver<br>insights inteligentes!</center></html>");
            noInsights.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            noInsights.setForeground(Color.GRAY);
            noInsights.setHorizontalAlignment(JLabel.CENTER);
            noInsights.setBorder(new EmptyBorder(50, 20, 50, 20));
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
                new EmptyBorder(12, 12, 12, 12)
        ));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(450, 100));
        card.setMaximumSize(new Dimension(450, 100));
        card.setMinimumSize(new Dimension(450, 100));

        JLabel titulo = new JLabel(insight.getTitulo());
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titulo.setForeground(getCorPorTipo(insight.getTipo()));

        JLabel descricao = new JLabel("<html><div style='width: 400px;'>" + insight.getDescricao() + "</div></html>");
        descricao.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descricao.setForeground(new Color(38, 38, 38));

        JLabel recomendacao = new JLabel("<html><div style='width: 400px;'>Recomendação: " + insight.getRecomendacao() + "</div></html>");
        recomendacao.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        recomendacao.setForeground(Color.GRAY);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titulo);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(descricao);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(recomendacao);

        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private Color getCorPorTipo(AnalyticsEngine.TipoInsight tipo) {
        switch (tipo) {
            case CRITICO: return new Color(231, 76, 60);
            case ALERTA: return new Color(243, 156, 18);
            case SUCESSO: return new Color(46, 204, 113);
            case OPORTUNIDADE: return new Color(142, 68, 173);
            default: return new Color(242, 48, 100);
        }
    }

    private void atualizarGraficos() {
        System.out.println(" Atualizando gráficos...");

        try {
            List<Venda> vendas = vendaController.obterTodasVendas();
            System.out.println(" Carregando " + vendas.size() + " vendas para gráficos");

            if (vendas.isEmpty()) {
                mostrarMensagemGrafico("Nenhuma venda registrada para gerar gráficos.");
                return;
            }

            chartPanel.removeAll();
            JLabel loading = new JLabel("Gerando gráficos avançados...", JLabel.CENTER);
            loading.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            loading.setForeground(Color.GRAY);
            chartPanel.add(loading);
            chartPanel.revalidate();
            chartPanel.repaint();

            SwingWorker<String, Void> worker = new SwingWorker<>() {
                @Override
                protected String doInBackground() throws Exception {
                    return util.RGraphUtil.gerarGraficoVendas(vendas);
                }

                @Override
                protected void done() {
                    try {
                        String imagePath = get();
                        if (imagePath != null && new File(imagePath).exists()) {
                            System.out.println(" Gráfico gerado: " + imagePath);
                            exibirGrafico(imagePath);
                        } else {
                            System.err.println(" Gráfico não foi gerado");
                            mostrarErroGrafico("O arquivo de imagem não foi gerado pelo R.");
                        }
                    } catch (Exception e) {
                        System.err.println(" Erro ao gerar gráfico: " + e.getMessage());
                        mostrarErroGrafico("Erro ao processar a imagem do gráfico: " + e.getMessage());
                    }
                }
            };

            worker.execute();

        } catch (Exception e) {
            System.err.println(" Erro ao atualizar gráficos: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar dados para gráficos: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            mostrarErroGrafico("Erro ao carregar dados para gráficos.");
        }
    }

    private void exibirGrafico(String imagePath) {
        chartPanel.removeAll();

        try {
            ImageIcon grafico = new ImageIcon(imagePath);

            int panelWidth = chartPanel.getWidth() - 20;
            int panelHeight = chartPanel.getHeight() - 20;

            if (panelWidth > 100 && panelHeight > 100) {
                int originalWidth = grafico.getIconWidth();
                int originalHeight = grafico.getIconHeight();

                double scaleX = (double) panelWidth / originalWidth;
                double scaleY = (double) panelHeight / originalHeight;
                double scale = Math.min(scaleX, scaleY) * 0.95;

                int scaledWidth = (int) (originalWidth * scale);
                int scaledHeight = (int) (originalHeight * scale);

                Image img = grafico.getImage();
                Image scaledImg = img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                ImageIcon finalIcon = new ImageIcon(scaledImg);

                JLabel lblGrafico = new JLabel(finalIcon);
                lblGrafico.setHorizontalAlignment(JLabel.CENTER);
                lblGrafico.setVerticalAlignment(JLabel.CENTER);

                chartPanel.add(lblGrafico, BorderLayout.CENTER);
            } else {
                JLabel lblGrafico = new JLabel(grafico);
                lblGrafico.setHorizontalAlignment(JLabel.CENTER);
                chartPanel.add(lblGrafico, BorderLayout.CENTER);
            }

        } catch (Exception e) {
            mostrarErroGrafico("Erro ao exibir gráfico: " + e.getMessage());
        }

        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private void mostrarErroGrafico(String mensagem) {
        chartPanel.removeAll();
        JLabel erro = new JLabel("<html><center>Erro ao gerar gráfico.<br>" + mensagem + "</center></html>");
        erro.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        erro.setForeground(new Color(231, 76, 60));
        erro.setHorizontalAlignment(JLabel.CENTER);
        chartPanel.add(erro, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private void mostrarMensagemGrafico(String mensagem) {
        chartPanel.removeAll();
        JLabel msg = new JLabel("<html><center>" + mensagem + "</center></html>");
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        msg.setForeground(Color.GRAY);
        msg.setHorizontalAlignment(JLabel.CENTER);
        chartPanel.add(msg, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private void abrirAnalytics() {
        try {
            List<Venda> vendas = vendaController.obterTodasVendas();

            if (vendas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhuma venda encontrada para gerar analytics.", "Analytics", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JDialog dialog = new JDialog(this, "Analytics Avançados", true);
            dialog.setSize(650, 550);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());
            dialog.getContentPane().setBackground(new Color(64, 64, 64));

            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            headerPanel.setBackground(new Color(242, 48, 100));
            headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
            JLabel titleLabel = new JLabel("ANALYTICS AVANÇADOS");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            titleLabel.setForeground(Color.WHITE);
            headerPanel.add(titleLabel);

            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setOpaque(false);
            contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

            int totalVendas = vendas.size();
            double somaTotal = vendas.stream().mapToDouble(v -> v.getQuantidade() * v.getValorUnitario()).sum();
            double ticketMedio = (totalVendas > 0) ? somaTotal / totalVendas : 0.0;

            contentPanel.add(createSectionTitle("MÉTRICAS PRINCIPAIS"));
            contentPanel.add(Box.createVerticalStrut(10));
            contentPanel.add(createMetricPanel("Total de vendas:", String.format("%d", totalVendas)));
            contentPanel.add(Box.createVerticalStrut(5));
            contentPanel.add(createMetricPanel("Faturamento total:", String.format("R$ %.2f", somaTotal)));
            contentPanel.add(Box.createVerticalStrut(5));
            contentPanel.add(createMetricPanel("Ticket médio:", String.format("R$ %.2f", ticketMedio)));

            contentPanel.add(Box.createVerticalStrut(25));

            contentPanel.add(createSectionTitle("PRODUTOS MAIS VENDIDOS"));
            contentPanel.add(Box.createVerticalStrut(10));

            vendas.stream()
                    .collect(Collectors.groupingBy(
                            Venda::getProduto,
                            Collectors.summingInt(Venda::getQuantidade)
                    ))
                    .entrySet().stream()
                    .sorted(java.util.Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(5)
                    .forEach(entry -> {
                        contentPanel.add(createMetricPanel(entry.getKey() + ":", String.format("%d unidades", entry.getValue())));
                        contentPanel.add(Box.createVerticalStrut(5));
                    });

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
            buttonPanel.setOpaque(false);
            buttonPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

            JButton btnFechar = new JButton("Fechar");
            btnFechar.setBackground(new Color(140, 140, 140));
            btnFechar.setForeground(Color.WHITE);
            btnFechar.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnFechar.setFocusPainted(false);
            btnFechar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            btnFechar.addActionListener(e -> dialog.dispose());
            buttonPanel.add(btnFechar);

            dialog.add(headerPanel, BorderLayout.NORTH);
            dialog.add(contentPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);

            dialog.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao gerar analytics: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel createSectionTitle(String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(new Color(242, 48, 100));
        label.setBorder(new EmptyBorder(0, 0, 5, 0));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JPanel createMetricPanel(String labelText, String valueText) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Monospaced", Font.PLAIN, 12));
        label.setForeground(new Color(221, 221, 221));

        JLabel value = new JLabel(" " + valueText);
        value.setFont(new Font("Monospaced", Font.BOLD, 12));
        value.setForeground(Color.WHITE);

        panel.add(label, BorderLayout.WEST);
        panel.add(value, BorderLayout.EAST);
        return panel;
    }

    private void atualizarDashboard() {
        SwingUtilities.invokeLater(() -> {
            atualizarInsights();
            atualizarGraficos();
            updateStatsInSidebar();
        });
    }

    private void importarCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arquivos CSV", "csv"));
        chooser.setDialogTitle("Selecionar arquivo CSV para importar");
        chooser.setCurrentDirectory(new java.io.File("."));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File arquivo = chooser.getSelectedFile();
            JDialog loadingDialog = criarLoadingDialog("Importando arquivo CSV...", "Processando...");

            SwingWorker<DataImporter.ImportResult, String> worker = new SwingWorker<>() {
                @Override
                protected DataImporter.ImportResult doInBackground() throws Exception {
                    return dataImporter.importFromCSV(arquivo);
                }

                @Override
                protected void done() {
                    try {
                        DataImporter.ImportResult resultado = get();
                        if (resultado.isSuccess() && resultado.getSuccessCount() > 0) {
                            atualizarDashboard();
                        }
                        mostrarResultadoImportacao(resultado);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(SimplifiedDashboardView.this, "Erro na importação CSV:\n" + e.getMessage(), "Erro de Importação", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        loadingDialog.dispose();
                    }
                }
            };
            worker.execute();
            loadingDialog.setVisible(true);
        }
    }

    private void importarTexto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arquivos de Texto", "txt"));
        chooser.setDialogTitle("Selecionar arquivo TXT para importar");

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File arquivo = chooser.getSelectedFile();
            JDialog loadingDialog = criarLoadingDialog("Importando arquivo de Texto...", "Processando...");

            SwingWorker<DataImporter.ImportResult, Void> worker = new SwingWorker<>() {
                @Override
                protected DataImporter.ImportResult doInBackground() throws Exception {
                    return dataImporter.importFromTextFile(arquivo);
                }

                @Override
                protected void done() {
                    try {
                        DataImporter.ImportResult resultado = get();
                        if (resultado.isSuccess() && resultado.getSuccessCount() > 0) {
                            atualizarDashboard();
                        }
                        mostrarResultadoImportacao(resultado);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(SimplifiedDashboardView.this, "Erro na importação de Texto:\n" + e.getMessage(), "Erro de Importação", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        loadingDialog.dispose();
                    }
                }
            };
            worker.execute();
            loadingDialog.setVisible(true);
        }
    }

    private void importarWhatsApp() {
        JDialog dialog = new JDialog(this, "Importar WhatsApp", true);
        dialog.setSize(650, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(64, 64, 64));

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(242, 48, 100));
        headerPanel.setBorder(new EmptyBorder(15, 0, 15, 0));

        JLabel titulo = new JLabel("IMPORTAR WHATSAPP", JLabel.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        headerPanel.add(titulo);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel instrucao = new JLabel("Cole aqui as mensagens do WhatsApp com informações de vendas:");
        instrucao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        instrucao.setForeground(Color.WHITE);

        String exemploTexto = "Vendeu 5 Mouse Logitech por R$ 85,50 cada\n" +
                "João: Comprei 2 Teclado Mecânico R$ 320,00\n" +
                "Maria vendeu 3 Notebook Dell - R$ 2500 cada\n" +
                "10 Carregador USB-C vendidos por 45,90 reais\n" +
                "Smartphone Samsung: 4 unidades x R$ 1200\n" +
                "\n" +
                "DICA: O sistema reconhece automaticamente:\n" +
                "- Produtos mencionados\n" +
                "- Quantidades (números)\n" +
                "- Valores (R$ ou reais)\n" +
                "- Formatos variados de texto";

        JTextArea textArea = new JTextArea(12, 50);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.FIAP_GRAY_MEDIUM),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        textArea.setBackground(new Color(38, 38, 38));
        textArea.setForeground(Color.WHITE);
        textArea.setCaretColor(Color.WHITE);
        textArea.setText(exemploTexto);
        textArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(null);

        JPanel instrucaoPanel = new JPanel(new BorderLayout());
        instrucaoPanel.setOpaque(false);
        instrucaoPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel subtitulo = new JLabel("<html><i>Substitua o exemplo acima pelas suas mensagens reais do WhatsApp</i></html>");
        subtitulo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        subtitulo.setForeground(ColorPalette.ASTERIA_SILVER);

        instrucaoPanel.add(instrucao, BorderLayout.NORTH);
        instrucaoPanel.add(Box.createVerticalStrut(5), BorderLayout.CENTER);
        instrucaoPanel.add(subtitulo, BorderLayout.SOUTH);

        contentPanel.add(instrucaoPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        buttonPanel.setOpaque(false);

        Dimension buttonSize = new Dimension(140, 45);

        JButton btnLimpar = new JButton("Limpar");
        btnLimpar.setBackground(ColorPalette.WARNING_AMBER);
        btnLimpar.setForeground(Color.WHITE);
        btnLimpar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLimpar.setFocusPainted(false);
        btnLimpar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btnLimpar.setPreferredSize(buttonSize);
        btnLimpar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnImportar = new JButton("Importar");
        btnImportar.setBackground(new Color(242, 48, 100));
        btnImportar.setForeground(Color.WHITE);
        btnImportar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnImportar.setFocusPainted(false);
        btnImportar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btnImportar.setPreferredSize(buttonSize);
        btnImportar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(140, 140, 140));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btnCancelar.setPreferredSize(buttonSize);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        adicionarEfeitoHoverWhatsApp(btnLimpar, ColorPalette.WARNING_AMBER);
        adicionarEfeitoHoverWhatsApp(btnImportar, new Color(242, 48, 100));
        adicionarEfeitoHoverWhatsApp(btnCancelar, new Color(140, 140, 140));

        btnLimpar.addActionListener(e -> {
            textArea.setText("");
            textArea.requestFocus();
        });

        btnImportar.addActionListener(e -> {
            String texto = textArea.getText().trim();
            if (!texto.isEmpty() && !texto.equals(exemploTexto)) {
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        " Por favor, substitua o exemplo pelas suas mensagens reais do WhatsApp!",
                        "Dados Necessários",
                        JOptionPane.WARNING_MESSAGE);
                textArea.selectAll();
                textArea.requestFocus();
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnLimpar);
        buttonPanel.add(btnImportar);
        buttonPanel.add(btnCancelar);

        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);

        SwingUtilities.invokeLater(() -> {
            textArea.requestFocus();
            textArea.selectAll();
        });
    }

    private void adicionarEfeitoHoverWhatsApp(JButton botao, Color corOriginal) {
        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (botao.isEnabled()) {
                    botao.setBackground(corOriginal.darker());
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (botao.isEnabled()) {
                    botao.setBackground(corOriginal);
                }
            }
        });
    }

    private JDialog criarLoadingDialog(String titulo, String mensagem) {
        JDialog loadingDialog = new JDialog(this, titulo, true);
        loadingDialog.setSize(350, 120);
        loadingDialog.setLocationRelativeTo(this);
        loadingDialog.setLayout(new BorderLayout());
        loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel loadingLabel = new JLabel(mensagem, JLabel.CENTER);
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setString("Processando...");
        progressBar.setStringPainted(true);

        contentPanel.add(loadingLabel, BorderLayout.CENTER);
        contentPanel.add(progressBar, BorderLayout.SOUTH);

        loadingDialog.add(contentPanel, BorderLayout.CENTER);

        return loadingDialog;
    }

    private void updateLoadingDialog(JDialog loadingDialog, String mensagem) {
        if (loadingDialog != null && loadingDialog.isVisible()) {
            JPanel contentPanel = (JPanel) loadingDialog.getContentPane().getComponent(0);
            JLabel loadingLabel = (JLabel) contentPanel.getComponent(0);
            loadingLabel.setText(mensagem);
        }
    }

    private void gerarTemplate() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Salvar Template CSV");
        chooser.setSelectedFile(new File("template_vendas_com_dados.csv"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            String path = selectedFile.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".csv")) {
                path += ".csv";
                selectedFile = new File(path);
            }

            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(selectedFile), StandardCharsets.UTF_8))) {
                writer.write("\uFEFF");

                // Cabeçalho exato
                writer.write("produto,quantidade,valor_unitario,data\n");

                int linhasGeradas = 0;

                // Buscar vendas do banco de dados
                try {
                    List<Venda> todasVendas = vendaController.obterTodasVendas();
                    System.out.println("Template: encontradas " + todasVendas.size() + " vendas no banco");

                    if (!todasVendas.isEmpty()) {
                        // Para cada venda, escrever linha por linha
                        for (Venda venda : todasVendas) {
                            // Produto (limpar e colocar entre aspas se necessário)
                            String produto = venda.getProduto().trim();
                            if (produto.contains(",")) {
                                produto = "\"" + produto + "\"";
                            }

                            // Quantidade (número inteiro)
                            int quantidade = venda.getQuantidade();

                            // Valor (FORÇAR PONTO DECIMAL EM VEZ DE VÍRGULA)
                            double valor = venda.getValorUnitario();
                            String valorStr = String.format(java.util.Locale.US, "%.2f", valor);

                            // Data (formato ISO)
                            String data = venda.getData().toString();

                            // Montar linha manualmente
                            String linha = produto + "," + quantidade + "," + valorStr + "," + data;

                            // Escrever linha
                            writer.write(linha + "\n");

                            linhasGeradas++;

                            // Debug - só mostrar as primeiras 3 para não poluir
                            if (linhasGeradas <= 3) {
                                System.out.println("Linha gerada: " + linha);
                            }
                        }
                    } else {
                        // Se não tem vendas, adicionar exemplos
                        writer.write("Mouse Gamer RGB,2,85.50,2025-08-23\n");
                        writer.write("Teclado Mecânico,1,320.00,2025-08-23\n");
                        writer.write("Monitor 24 polegadas,1,899.99,2025-08-23\n");
                        linhasGeradas = 3;
                    }

                } catch (Exception e) {
                    System.err.println("Erro ao buscar vendas: " + e.getMessage());
                    e.printStackTrace();

                    // Em caso de erro, adicionar exemplos
                    writer.write("Mouse Gamer RGB,2,85.50,2025-08-23\n");
                    writer.write("Teclado Mecânico,1,320.00,2025-08-23\n");
                    writer.write("Monitor 24 polegadas,1,899.99,2025-08-23\n");
                    linhasGeradas = 3;
                }

                JOptionPane.showMessageDialog(this,
                        "Template CSV criado com dados do banco!\n\n" +
                                "Arquivo: " + selectedFile.getName() + "\n" +
                                "Linhas geradas: " + linhasGeradas + "\n\n" +
                                "Agora com formato decimal correto (ponto em vez de vírgula)!",
                        "Template Criado",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao criar template: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostrarResultadoImportacao(DataImporter.ImportResult resultado) {
        StringBuilder mensagem = new StringBuilder();
        mensagem.append(resultado.getSummary()).append("\n\n");

        if (resultado.hasErrors()) {
            mensagem.append("ERROS:\n");
            for (DataImporter.ImportError erro : resultado.getErrors()) {
                mensagem.append("• ").append(erro.toString()).append("\n");
            }
            mensagem.append("\n");
        }

        if (resultado.hasWarnings()) {
            mensagem.append("AVISOS:\n");
            for (DataImporter.ImportWarning aviso : resultado.getWarnings()) {
                mensagem.append("• ").append(aviso.toString()).append("\n");
            }
        }

        JTextArea textArea = new JTextArea(mensagem.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBackground(Color.WHITE);
        textArea.setBorder(new EmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Resultado da Importação",
                resultado.hasErrors() ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportarDados() {
        try {
            List<Venda> vendas = vendaController.obterTodasVendas();

            if (vendas.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Não há vendas para exportar.",
                        "Exportação",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Exportar Dados de Vendas");
            chooser.setSelectedFile(new File("vendas_export.csv"));

            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String path = chooser.getSelectedFile().getAbsolutePath();
                if (!path.toLowerCase().endsWith(".csv")) {
                    path += ".csv";
                }

                util.ReportUtil.exportarCSV(vendas, path);
                JOptionPane.showMessageDialog(this,
                        "Dados exportados com sucesso!\n\n" +
                                "Arquivo: " + path + "\n\n" +
                                "Total de " + vendas.size() + " vendas exportadas.",
                        "Exportação Concluída",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro na exportação: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sairAplicacao() {
        int resposta = JOptionPane.showConfirmDialog(this,
                "Deseja realmente sair da aplicação?", "Confirmar Saída",
                JOptionPane.YES_NO_OPTION);
        if (resposta == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void mostrarErroInsights(String mensagem) {
        insightsPanel.removeAll();
        JLabel erroLabel = new JLabel("<html><center>" + mensagem.replace("\n", "<br>") + "</center></html>");
        erroLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        erroLabel.setForeground(new Color(231, 76, 60));
        erroLabel.setHorizontalAlignment(JLabel.CENTER);
        insightsPanel.add(erroLabel);
        insightsPanel.revalidate();
        insightsPanel.repaint();
    }

    private void updateStatsInSidebar() {
        JPanel sidebar = null;
        for (Component comp : ((JPanel) this.getContentPane().getComponent(0)).getComponents()) {
            if (comp instanceof JPanel && "sidebar".equals(comp.getName())) {
                sidebar = (JPanel) comp;
                break;
            }
        }

        if (sidebar != null) {
            for (int i = 0; i < sidebar.getComponentCount(); i++) {
                Component sidebarComp = sidebar.getComponent(i);
                if (sidebarComp instanceof JPanel && "statsPanel".equals(sidebarComp.getName())) {
                    sidebar.remove(i);
                    JPanel newStatsPanel = criarPainelEstatisticas();
                    newStatsPanel.setName("statsPanel");
                    sidebar.add(newStatsPanel, i);
                    sidebar.revalidate();
                    sidebar.repaint();
                    return;
                }
            }
        }
    }
}