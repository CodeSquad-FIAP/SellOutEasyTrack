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

public class SimplifiedDashboardView extends JFrame {

    private VendaController vendaController = new VendaController();
    private AnalyticsEngine analyticsEngine = new AnalyticsEngine();
    private DataImporter dataImporter = new DataImporter();

    private JPanel chartPanel;
    private JPanel insightsPanel;

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
        super("SellOut EasyTrack - Dashboard Inteligente");
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
        header.setBackground(DARK_COLOR);
        header.setBorder(new EmptyBorder(15, 25, 15, 25));
        header.setPreferredSize(new Dimension(0, 80));

        JLabel titulo = new JLabel("SellOut EasyTrack - Sistema Completo");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(WHITE);

        JLabel subtitulo = new JLabel("BI Inteligente - Múltiplas Fontes - Analytics Avançado");
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

        JPanel statsPanel = criarPainelEstatisticas();
        sidebar.add(statsPanel);

        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(criarBotaoMenu("SAIR", "Fechar aplicação", this::sairAplicacao));

        return sidebar;
    }

    private JPanel criarAreaConteudo() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(LIGHT_GRAY);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setBackground(LIGHT_GRAY);
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
        container.setBackground(WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel headerLabel = new JLabel("Insights Inteligentes Automáticos");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(DARK_COLOR);

        JButton btnRefreshInsights = criarBotaoModerno("ATUALIZAR", SECONDARY_COLOR);
        btnRefreshInsights.addActionListener(e -> atualizarInsights());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(btnRefreshInsights, BorderLayout.EAST);

        insightsPanel = new JPanel();
        insightsPanel.setLayout(new BoxLayout(insightsPanel, BoxLayout.Y_AXIS));
        insightsPanel.setBackground(WHITE);

        JScrollPane scrollPane = new JScrollPane(insightsPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

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

        JLabel headerLabel = new JLabel("Analytics Visuais Avançados");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(DARK_COLOR);

        JButton btnRefreshChart = criarBotaoModerno("GERAR GRÁFICOS", SECONDARY_COLOR);
        btnRefreshChart.addActionListener(e -> atualizarGraficos());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(btnRefreshChart, BorderLayout.EAST);

        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(WHITE);
        chartPanel.setBorder(BorderFactory.createLineBorder(LIGHT_GRAY, 1));
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

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);

        JLabel footerText = new JLabel("SellOut EasyTrack");
        footerText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerText.setForeground(WHITE);

        leftPanel.add(footerText);

        JLabel versionInfo = new JLabel("v2.0 - Sistema Completo");
        versionInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        versionInfo.setForeground(ACCENT_COLOR);

        footer.add(leftPanel, BorderLayout.WEST);
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

        buttonPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                buttonPanel.setOpaque(true);
                buttonPanel.setBackground(PRIMARY_COLOR);
                buttonPanel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                buttonPanel.setOpaque(false);
                buttonPanel.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                buttonPanel.setBackground(PRIMARY_COLOR.darker());
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

    private JButton criarBotaoDialog(String texto, String descricao) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout());
        btn.setBackground(Color.WHITE);
        btn.setForeground(TEXT_COLOR);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JLabel lblTitulo = new JLabel(texto);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setForeground(TEXT_COLOR);

        JLabel lblDesc = new JLabel(descricao);
        lblDesc.setFont(new Font("Arial", Font.PLAIN, 11));
        lblDesc.setForeground(Color.GRAY);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(lblTitulo, BorderLayout.NORTH);
        textPanel.add(lblDesc, BorderLayout.SOUTH);

        btn.add(textPanel, BorderLayout.CENTER);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(LIGHT_GRAY);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                        BorderFactory.createEmptyBorder(14, 19, 14, 19)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(15, 20, 15, 20)));
            }
        });

        return btn;
    }

    private void mostrarMenuVendas() {
        JDialog dialog = new JDialog(this, "Menu de Vendas Completo", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 0, 15, 0));

        JLabel titulo = new JLabel("MENU DE VENDAS", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        headerPanel.add(titulo);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton btnCriar = criarBotaoDialog("Criar Venda", "Registrar nova venda");
        JButton btnListar = criarBotaoDialog("Listar Vendas", "Visualizar todas as vendas");
        JButton btnAtualizar = criarBotaoDialog("Atualizar Venda", "Editar venda existente");
        JButton btnDeletar = criarBotaoDialog("Deletar Venda", "Remover venda");

        btnCriar.addActionListener(e -> { dialog.dispose(); new CriarVendaView(this); });
        btnListar.addActionListener(e -> { dialog.dispose(); new ListarVendasView(this); });
        btnAtualizar.addActionListener(e -> { dialog.dispose(); new AtualizarVendaView(this); });
        btnDeletar.addActionListener(e -> { dialog.dispose(); new DeletarVendaView(this); });

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        contentPanel.add(btnCriar, gbc);
        gbc.gridy = 1;
        contentPanel.add(btnListar, gbc);
        gbc.gridy = 2;
        contentPanel.add(btnAtualizar, gbc);
        gbc.gridy = 3;
        contentPanel.add(btnDeletar, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(245, 245, 245));
        JButton btnCancelar = criarBotaoDialog("Cancelar", "Fechar");
        btnCancelar.setBackground(new Color(149, 165, 166));
        btnCancelar.addActionListener(e -> dialog.dispose());
        buttonPanel.add(btnCancelar);

        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void mostrarMenuImportacao() {
        JDialog dialog = new JDialog(this, "Importar Dados", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 0, 15, 0));

        JLabel titulo = new JLabel("IMPORTAR DADOS", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        headerPanel.add(titulo);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton btnCSV = criarBotaoDialog("Importar CSV", "Importar arquivo CSV");
        JButton btnTexto = criarBotaoDialog("Importar Arquivo Texto", "Importar arquivo TXT");
        JButton btnWhatsApp = criarBotaoDialog("Colar dados WhatsApp", "Importar mensagens");
        JButton btnTemplate = criarBotaoDialog("Gerar Template", "Criar arquivo modelo");

        btnCSV.addActionListener(e -> { dialog.dispose(); importarCSV(); });
        btnTexto.addActionListener(e -> { dialog.dispose(); importarTexto(); });
        btnWhatsApp.addActionListener(e -> { dialog.dispose(); importarWhatsApp(); });
        btnTemplate.addActionListener(e -> { dialog.dispose(); gerarTemplate(); });

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        contentPanel.add(btnCSV, gbc);
        gbc.gridy = 1;
        contentPanel.add(btnTexto, gbc);
        gbc.gridy = 2;
        contentPanel.add(btnWhatsApp, gbc);
        gbc.gridy = 3;
        contentPanel.add(btnTemplate, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(245, 245, 245));
        JButton btnCancelar = criarBotaoDialog("Cancelar", "Fechar");
        btnCancelar.setBackground(new Color(149, 165, 166));
        btnCancelar.addActionListener(e -> dialog.dispose());
        buttonPanel.add(btnCancelar);

        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void atualizarInsights() {
        insightsPanel.removeAll();

        JLabel loading = new JLabel("Gerando insights inteligentes...", JLabel.CENTER);
        loading.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        loading.setForeground(Color.GRAY);
        insightsPanel.add(loading);
        insightsPanel.revalidate();
        insightsPanel.repaint();

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
                            JOptionPane.showMessageDialog(SimplifiedDashboardView.this,
                                    "Erro ao gerar insights: " + e.getMessage(),
                                    "Erro",
                                    JOptionPane.ERROR_MESSAGE);
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

    private void mostrarErroInsights(String mensagem) {
        insightsPanel.removeAll();
        JLabel erro = new JLabel("<html><center>" + mensagem + "</center></html>");
        erro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        erro.setForeground(DANGER_COLOR);
        erro.setHorizontalAlignment(JLabel.CENTER);
        erro.setBorder(new EmptyBorder(20, 20, 20, 20));
        insightsPanel.add(erro);
        insightsPanel.revalidate();
        insightsPanel.repaint();
    }

    private JPanel criarCardInsight(AnalyticsEngine.Insight insight) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(getCorPorTipo(insight.getTipo()), 2),
                new EmptyBorder(12, 12, 12, 12)
        ));
        card.setBackground(WHITE);
        card.setPreferredSize(new Dimension(450, 100));
        card.setMaximumSize(new Dimension(450, 100));
        card.setMinimumSize(new Dimension(450, 100));

        JLabel titulo = new JLabel(insight.getTitulo());
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titulo.setForeground(getCorPorTipo(insight.getTipo()));

        JLabel descricao = new JLabel("<html><div style='width: 400px;'>" + insight.getDescricao() + "</div></html>");
        descricao.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descricao.setForeground(TEXT_COLOR);

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
            case CRITICO: return DANGER_COLOR;
            case ALERTA: return WARNING_COLOR;
            case SUCESSO: return ACCENT_COLOR;
            case OPORTUNIDADE: return new Color(155, 89, 182);
            default: return PRIMARY_COLOR;
        }
    }

    private void atualizarGraficos() {
        try {
            List<Venda> vendas = vendaController.obterTodasVendas();

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

            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    return util.RGraphUtil.gerarGraficoVendas(vendas);
                }

                @Override
                protected void done() {
                    try {
                        String imagePath = get();
                        if (imagePath != null && new File(imagePath).exists()) {
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
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar dados: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
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
            mostrarErroGrafico();
        }

        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private void mostrarErroGrafico() {
        chartPanel.removeAll();
        JLabel erro = new JLabel("<html><center>Erro ao gerar gráfico.<br>Verifique se o R está instalado.</center></html>");
        erro.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        erro.setForeground(DANGER_COLOR);
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

            StringBuilder analytics = new StringBuilder();
            analytics.append("ANALYTICS AVANÇADOS\n");
            analytics.append("==================\n\n");

            int totalVendas = vendas.size();
            double somaTotal = vendas.stream().mapToDouble(v -> v.getQuantidade() * v.getValorUnitario()).sum();
            double ticketMedio = somaTotal / totalVendas;

            analytics.append("MÉTRICAS PRINCIPAIS:\n");
            analytics.append(String.format("• Total de vendas: %d\n", totalVendas));
            analytics.append(String.format("• Faturamento total: R$ %.2f\n", somaTotal));
            analytics.append(String.format("• Ticket médio: R$ %.2f\n\n", ticketMedio));

            analytics.append("PRODUTOS MAIS VENDIDOS:\n");
            vendas.stream()
                    .collect(java.util.stream.Collectors.groupingBy(
                            Venda::getProduto,
                            java.util.stream.Collectors.summingInt(Venda::getQuantidade)
                    ))
                    .entrySet().stream()
                    .sorted(java.util.Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(5)
                    .forEach(entry -> analytics.append(String.format("• %s: %d unidades\n", entry.getKey(), entry.getValue())));

            JTextArea areaAnalytics = new JTextArea(analytics.toString());
            areaAnalytics.setFont(new Font("Courier New", Font.PLAIN, 12));
            areaAnalytics.setEditable(false);
            areaAnalytics.setBackground(WHITE);
            areaAnalytics.setBorder(new EmptyBorder(20, 20, 20, 20));

            JScrollPane scrollPane = new JScrollPane(areaAnalytics);
            scrollPane.setPreferredSize(new Dimension(600, 400));

            JOptionPane.showMessageDialog(this, scrollPane, "Analytics Avançados", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao gerar analytics: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarDashboard() {
        atualizarInsights();
        atualizarGraficos();
    }

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
                                JOptionPane.showMessageDialog(SimplifiedDashboardView.this,
                                        "Erro na importação: " + e.getMessage(),
                                        "Erro",
                                        JOptionPane.ERROR_MESSAGE);
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
        JDialog dialog = new JDialog(this, "Importar WhatsApp", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 0, 15, 0));

        JLabel titulo = new JLabel("IMPORTAR WHATSAPP", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        headerPanel.add(titulo);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 245, 245));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel instrucao = new JLabel("Cole aqui as mensagens do WhatsApp com informações de vendas:");
        instrucao.setFont(new Font("Arial", Font.BOLD, 14));
        instrucao.setForeground(TEXT_COLOR);

        JTextArea textArea = new JTextArea(10, 40);
        textArea.setFont(new Font("Arial", Font.PLAIN, 12));
        textArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        textArea.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        contentPanel.add(instrucao, BorderLayout.NORTH);
        contentPanel.add(Box.createVerticalStrut(10), BorderLayout.CENTER);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        textPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(textPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton btnImportar = new JButton("Importar");
        btnImportar.setBackground(ACCENT_COLOR);
        btnImportar.setForeground(Color.WHITE);
        btnImportar.setFont(new Font("Arial", Font.BOLD, 16));
        btnImportar.setFocusPainted(false);
        btnImportar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(149, 165, 166));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 16));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        btnImportar.addActionListener(e -> {
            String texto = textArea.getText().trim();
            if (!texto.isEmpty()) {
                dialog.dispose();
                DataImporter.ImportResult resultado = dataImporter.importFromWhatsApp(texto);
                mostrarResultadoImportacao(resultado);
                if (resultado.isSuccess()) {
                    atualizarDashboard();
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Por favor, cole o texto do WhatsApp!", "Erro", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnImportar);
        buttonPanel.add(btnCancelar);

        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void gerarTemplate() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Salvar Template CSV");
            chooser.setSelectedFile(new File("template_vendas.csv"));

            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                String path = selectedFile.getAbsolutePath();
                if (!path.toLowerCase().endsWith(".csv")) {
                    path += ".csv";
                }

                try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(path))) {
                    writer.println("produto,quantidade,valor_unitario,data");
                    writer.println("\"Produto Exemplo\",5,25.50,\"" + java.time.LocalDate.now() + "\"");
                    writer.println("\"Outro Produto\",2,150.00,\"" + java.time.LocalDate.now() + "\"");
                }

                JOptionPane.showMessageDialog(this,
                        "Template CSV gerado com sucesso!\n\n" +
                                "Arquivo: " + path +
                                "\n\nUse este arquivo como base para suas importações.",
                        "Template Criado",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao gerar template: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
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
        textArea.setBackground(WHITE);
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
}