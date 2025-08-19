package view;

import controller.VendaController;
import model.Venda;
import util.RGraphUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardView extends JFrame {

    private VendaController vendaController = new VendaController();
    private JPanel chartPanel;
    private JPopupMenu crudMenu;

    // Cores do tema moderno
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);      // Azul principal
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);    // Azul claro
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);       // Verde
    private static final Color DANGER_COLOR = new Color(231, 76, 60);        // Vermelho
    private static final Color DARK_COLOR = new Color(44, 62, 80);           // Azul escuro
    private static final Color LIGHT_GRAY = new Color(236, 240, 241);        // Cinza claro
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(52, 73, 94);           // Texto escuro

    public DashboardView() {
        super("SellOut EasyTrack - Dashboard Executivo");
        configurarJanela();
        criarInterface();
        setVisible(true);
    }

    private void configurarJanela() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Configurar ícone da aplicação (se houver)
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
        } catch (Exception e) {
            // Ícone não encontrado, ignorar
        }
    }

    private void criarInterface() {
        // Painel principal com gradiente
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_GRAY);

        // Header com título e informações
        JPanel headerPanel = criarHeader();

        // Sidebar com menu
        JPanel sidebarPanel = criarSidebar();

        // Área central com conteúdo
        JPanel contentPanel = criarAreaConteudo();

        // Footer com informações
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

        // Título principal
        JLabel titulo = new JLabel("SellOut EasyTrack");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(WHITE);

        JLabel subtitulo = new JLabel("Sistema de Gestão de Vendas");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(new Color(189, 195, 199));

        JPanel tituloPanel = new JPanel(new BorderLayout());
        tituloPanel.setOpaque(false);
        tituloPanel.add(titulo, BorderLayout.CENTER);
        tituloPanel.add(subtitulo, BorderLayout.SOUTH);

        // Informações do usuário
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);

        JLabel userInfo = new JLabel("Dashboard Executivo");
        userInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userInfo.setForeground(new Color(189, 195, 199));

        userPanel.add(userInfo);

        header.add(tituloPanel, BorderLayout.WEST);
        header.add(userPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel criarSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(52, 73, 94));
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setBorder(new EmptyBorder(20, 0, 20, 0));

        // Título do menu - CENTRALIZADO
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel menuTitle = new JLabel("MENU PRINCIPAL");
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        menuTitle.setForeground(new Color(149, 165, 166));

        titlePanel.add(menuTitle);
        sidebar.add(titlePanel);
        sidebar.add(Box.createVerticalStrut(10));

        // Botões do menu
        sidebar.add(criarBotaoMenu("VENDAS", "Gerenciar vendas", this::mostrarMenuVendas));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(criarBotaoMenu("RELATÓRIOS", "Visualizar relatórios", this::abrirRelatorios));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(criarBotaoMenu("GRÁFICOS", "Atualizar gráficos", this::atualizarGrafico));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(criarBotaoMenu("EXPORTAR", "Exportar dados", this::exportarCSV));

        sidebar.add(Box.createVerticalGlue());

        // Botão sair
        sidebar.add(criarBotaoMenu("SAIR", "Fechar aplicação", this::sairAplicacao));
        sidebar.add(Box.createVerticalStrut(20));

        return sidebar;
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
                // Adicionar feedback visual do clique
                buttonPanel.setBackground(PRIMARY_COLOR.darker());
                buttonPanel.repaint();

                // Executar ação após breve delay para feedback visual
                Timer timer = new Timer(100, evt -> {
                    try {
                        acao.run();
                    } catch (Exception ex) {
                        System.err.println("Erro ao executar ação do menu: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                buttonPanel.setBackground(PRIMARY_COLOR.darker());
                buttonPanel.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (buttonPanel.contains(e.getPoint())) {
                    buttonPanel.setBackground(PRIMARY_COLOR);
                } else {
                    buttonPanel.setOpaque(false);
                }
                buttonPanel.repaint();
            }
        });

        return buttonPanel;
    }

    private JPanel criarAreaConteudo() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(LIGHT_GRAY);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Card do gráfico
        JPanel cardGrafico = criarCardGrafico();
        content.add(cardGrafico, BorderLayout.CENTER);

        return content;
    }

    private JPanel criarCardGrafico() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        // Header do card
        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);
        cardHeader.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel cardTitle = new JLabel("Análise de Vendas");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        cardTitle.setForeground(DARK_COLOR);

        JLabel cardSubtitle = new JLabel("Produtos com melhor desempenho");
        cardSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cardSubtitle.setForeground(TEXT_COLOR);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(cardTitle, BorderLayout.NORTH);
        titlePanel.add(cardSubtitle, BorderLayout.SOUTH);

        // Botão de atualização
        JButton btnRefresh = criarBotaoModerno("ATUALIZAR", SECONDARY_COLOR);
        btnRefresh.addActionListener(e -> atualizarGrafico());

        cardHeader.add(titlePanel, BorderLayout.WEST);
        cardHeader.add(btnRefresh, BorderLayout.EAST);

        // Área do gráfico
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(WHITE);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_GRAY, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        chartPanel.setPreferredSize(new Dimension(0, 400));

        // Carregar gráfico inicial
        gerarGraficoR();

        card.add(cardHeader, BorderLayout.NORTH);
        card.add(chartPanel, BorderLayout.CENTER);

        return card;
    }

    private JButton criarBotaoModerno(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(WHITE);
        btn.setBackground(cor);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efeito hover
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

    private JPanel criarFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(127, 140, 141));
        footer.setBorder(new EmptyBorder(10, 25, 10, 25));
        footer.setPreferredSize(new Dimension(0, 40));

        JLabel footerText = new JLabel("SellOut EasyTrack - Sistema de Gestão de Vendas");
        footerText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerText.setForeground(WHITE);

        JLabel status = new JLabel("Sistema Online");
        status.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        status.setForeground(new Color(189, 195, 199));

        footer.add(footerText, BorderLayout.WEST);
        footer.add(status, BorderLayout.EAST);

        return footer;
    }

    private void configurarMenuCrud() {
        crudMenu = new JPopupMenu();
        crudMenu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 0, 5, 0)
        ));
        crudMenu.setBackground(WHITE);

        JMenuItem criarItem = criarMenuItem("CRIAR VENDA", "Registrar nova venda no sistema");
        JMenuItem lerItem = criarMenuItem("LISTAR VENDAS", "Visualizar todas as vendas registradas");
        JMenuItem atualizarItem = criarMenuItem("ATUALIZAR VENDA", "Editar informações de venda existente");
        JMenuItem deletarItem = criarMenuItem("DELETAR VENDA", "Remover venda do sistema");

        criarItem.addActionListener(e -> {
            crudMenu.setVisible(false);
            new CriarVendaView(this);
        });

        lerItem.addActionListener(e -> {
            crudMenu.setVisible(false);
            new ListarVendasView(this);
        });

        atualizarItem.addActionListener(e -> {
            crudMenu.setVisible(false);
            new AtualizarVendaView(this);
        });

        deletarItem.addActionListener(e -> {
            crudMenu.setVisible(false);
            new DeletarVendaView(this);
        });

        crudMenu.add(criarItem);
        crudMenu.addSeparator();
        crudMenu.add(lerItem);
        crudMenu.addSeparator();
        crudMenu.add(atualizarItem);
        crudMenu.addSeparator();
        crudMenu.add(deletarItem);
    }

    private JMenuItem criarMenuItem(String titulo, String descricao) {
        JMenuItem item = new JMenuItem();
        item.setLayout(new BorderLayout());
        item.setBorder(new EmptyBorder(12, 20, 12, 20));
        item.setBackground(WHITE);
        item.setPreferredSize(new Dimension(250, 50));

        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tituloLabel.setForeground(DARK_COLOR);

        JLabel descLabel = new JLabel(descricao);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLabel.setForeground(TEXT_COLOR);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(tituloLabel, BorderLayout.NORTH);
        textPanel.add(descLabel, BorderLayout.SOUTH);

        item.add(textPanel, BorderLayout.CENTER);

        // Efeito hover no menu item
        item.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                item.setBackground(LIGHT_GRAY);
                item.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                item.setBackground(WHITE);
                item.repaint();
            }
        });

        return item;
    }

    // Métodos de ação
    private void mostrarMenuVendas() {
        System.out.println("🔍 [DEBUG] Botão VENDAS clicado");

        try {
            if (crudMenu == null) {
                System.out.println("🔍 [DEBUG] Configurando menu CRUD...");
                configurarMenuCrud();
            }

            // Encontrar posição ideal para mostrar o menu
            Point frameLocation = this.getLocationOnScreen();
            int menuX = 280; // Largura do sidebar
            int menuY = 180; // Posição aproximada do botão VENDAS

            System.out.println("🔍 [DEBUG] Mostrando menu na posição: " + menuX + ", " + menuY);

            crudMenu.show(this, menuX, menuY);

        } catch (Exception e) {
            System.err.println("❌ [ERROR] Erro ao mostrar menu VENDAS: " + e.getMessage());
            e.printStackTrace();

            // Fallback: mostrar menu simples
            mostrarMenuVendasSimples();
        }
    }

    private void mostrarMenuVendasSimples() {
        String[] opcoes = {"Criar Venda", "Listar Vendas", "Atualizar Venda", "Deletar Venda", "Cancelar"};

        int escolha = JOptionPane.showOptionDialog(
                this,
                "Selecione uma opção:",
                "Menu de Vendas",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoes,
                opcoes[0]
        );

        switch (escolha) {
            case 0:
                new CriarVendaView(this);
                break;
            case 1:
                new ListarVendasView(this);
                break;
            case 2:
                new AtualizarVendaView(this);
                break;
            case 3:
                new DeletarVendaView(this);
                break;
            default:
                // Cancelar ou fechar
                break;
        }
    }

    private void abrirRelatorios() {
        try {
            System.out.println("🔍 [DEBUG] Gerando relatório automático...");

            List<Venda> vendas = vendaController.obterTodasVendas();

            if (vendas.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Nenhuma venda encontrada para gerar relatório.",
                        "Relatório Vazio",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Calcular estatísticas
            int totalVendas = vendas.size();
            double somaTotal = vendas.stream().mapToDouble(v -> v.getQuantidade() * v.getValorUnitario()).sum();
            double ticketMedio = somaTotal / totalVendas;

            // Encontrar produto mais vendido
            Map<String, Integer> produtosMaisVendidos = vendas.stream()
                    .collect(Collectors.groupingBy(
                            Venda::getProduto,
                            Collectors.summingInt(Venda::getQuantidade)
                    ));

            String produtoMaisVendido = produtosMaisVendidos.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("Nenhum");

            int quantidadeMaisVendido = produtosMaisVendidos.getOrDefault(produtoMaisVendido, 0);

            // Criar relatório
            StringBuilder relatorio = new StringBuilder();
            relatorio.append("====== RELATÓRIO DE VENDAS ======\n");
            relatorio.append("SellOut EasyTrack\n");
            relatorio.append("Gerado em: ").append(java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n\n");

            relatorio.append("RESUMO EXECUTIVO:\n");
            relatorio.append("• Total de vendas registradas: ").append(totalVendas).append("\n");
            relatorio.append("• Faturamento total: R$ ").append(String.format("%.2f", somaTotal)).append("\n");
            relatorio.append("• Ticket médio: R$ ").append(String.format("%.2f", ticketMedio)).append("\n");
            relatorio.append("• Produto mais vendido: ").append(produtoMaisVendido)
                    .append(" (").append(quantidadeMaisVendido).append(" unidades)\n\n");

            relatorio.append("DETALHAMENTO POR PRODUTO:\n");
            produtosMaisVendidos.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .forEach(entry -> {
                        double valorTotalProduto = vendas.stream()
                                .filter(v -> v.getProduto().equals(entry.getKey()))
                                .mapToDouble(v -> v.getQuantidade() * v.getValorUnitario())
                                .sum();
                        relatorio.append("• ").append(entry.getKey())
                                .append(": ").append(entry.getValue()).append(" unidades")
                                .append(" - R$ ").append(String.format("%.2f", valorTotalProduto)).append("\n");
                    });

            // Exibir relatório em janela
            JTextArea areaRelatorio = new JTextArea(relatorio.toString());
            areaRelatorio.setFont(new Font("Courier New", Font.PLAIN, 12));
            areaRelatorio.setEditable(false);
            areaRelatorio.setBackground(WHITE);
            areaRelatorio.setBorder(new EmptyBorder(20, 20, 20, 20));

            JScrollPane scrollPane = new JScrollPane(areaRelatorio);
            scrollPane.setPreferredSize(new Dimension(600, 400));

            JPanel painelRelatorio = new JPanel(new BorderLayout());
            painelRelatorio.add(scrollPane, BorderLayout.CENTER);

            // Botão para salvar relatório
            JPanel painelBotoes = new JPanel(new FlowLayout());
            JButton btnSalvar = criarBotaoModerno("SALVAR RELATÓRIO", SECONDARY_COLOR);
            btnSalvar.addActionListener(e -> salvarRelatorio(relatorio.toString()));
            painelBotoes.add(btnSalvar);

            painelRelatorio.add(painelBotoes, BorderLayout.SOUTH);

            JOptionPane.showMessageDialog(this, painelRelatorio,
                    "Relatório de Vendas - SellOut EasyTrack",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao acessar banco de dados: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao gerar relatório: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvarRelatorio(String conteudo) {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Salvar Relatório");
            chooser.setSelectedFile(new java.io.File("Relatorio_Vendas_" +
                    java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy")) + ".txt"));

            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try (java.io.FileWriter writer = new java.io.FileWriter(chooser.getSelectedFile())) {
                    writer.write(conteudo);
                    JOptionPane.showMessageDialog(this,
                            "Relatório salvo com sucesso!\n\nArquivo: " + chooser.getSelectedFile().getAbsolutePath(),
                            "Relatório Salvo",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar relatório: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarGrafico() {
        gerarGraficoR();
    }

    private void exportarCSV() {
        try {
            List<Venda> vendas = vendaController.obterTodasVendas();
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Exportar Dados de Vendas");
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String path = chooser.getSelectedFile().getAbsolutePath() + ".csv";
                util.ReportUtil.exportarCSV(vendas, path);

                JOptionPane.showMessageDialog(this,
                        "Dados exportados com sucesso!\n\nArquivo: " + path,
                        "Exportação Concluída",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao exportar dados: " + e.getMessage(),
                    "Erro de Exportação",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sairAplicacao() {
        int resposta = JOptionPane.showConfirmDialog(this,
                "Deseja realmente sair da aplicação?",
                "Confirmar Saída",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (resposta == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void gerarGraficoR() {
        try {
            List<Venda> vendas = vendaController.obterTodasVendas();
            System.out.println("Gerando gráfico 1080p com " + vendas.size() + " vendas");

            // Mostrar loading
            mostrarLoading();

            // Gera o gráfico usando R
            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    return RGraphUtil.gerarGraficoVendas(vendas);
                }

                @Override
                protected void done() {
                    try {
                        String imagePath = get();
                        if (imagePath != null && !imagePath.isEmpty()) {
                            exibirGrafico(imagePath);
                        } else {
                            mostrarErroGrafico("Erro ao gerar gráfico");
                        }
                    } catch (Exception e) {
                        mostrarErroGrafico("Erro: " + e.getMessage());
                    }
                }
            };

            worker.execute();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + e.getMessage());
            mostrarErroGrafico("Erro de banco de dados");
        }
    }

    private void mostrarLoading() {
        chartPanel.removeAll();
        JLabel loading = new JLabel("Gerando gráfico...", JLabel.CENTER);
        loading.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        loading.setForeground(TEXT_COLOR);
        chartPanel.add(loading);
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private void exibirGrafico(String imagePath) {
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            ImageIcon grafico = new ImageIcon(imagePath);

            // Calcular dimensões otimizadas
            int originalWidth = grafico.getIconWidth();
            int originalHeight = grafico.getIconHeight();

            int panelWidth = chartPanel.getWidth() - 40;
            int panelHeight = chartPanel.getHeight() - 40;

            if (panelWidth <= 0) panelWidth = 800;
            if (panelHeight <= 0) panelHeight = 400;

            double scaleX = (double) panelWidth / originalWidth;
            double scaleY = (double) panelHeight / originalHeight;
            double scale = Math.min(scaleX, scaleY);

            int scaledWidth = (int) (originalWidth * scale);
            int scaledHeight = (int) (originalHeight * scale);

            Image img = grafico.getImage();
            Image scaledImg = img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
            ImageIcon finalIcon = new ImageIcon(scaledImg);

            JLabel lblGrafico = new JLabel(finalIcon);
            lblGrafico.setHorizontalAlignment(JLabel.CENTER);
            lblGrafico.setVerticalAlignment(JLabel.CENTER);

            chartPanel.removeAll();
            chartPanel.add(lblGrafico, BorderLayout.CENTER);
            chartPanel.revalidate();
            chartPanel.repaint();

            System.out.println("Gráfico 1080p exibido com sucesso!");
        }
    }

    private void mostrarErroGrafico(String mensagem) {
        chartPanel.removeAll();

        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.setOpaque(false);

        JLabel iconError = new JLabel("⚠", JLabel.CENTER);
        iconError.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        iconError.setForeground(DANGER_COLOR);

        JLabel textError = new JLabel(mensagem, JLabel.CENTER);
        textError.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textError.setForeground(TEXT_COLOR);

        errorPanel.add(iconError, BorderLayout.CENTER);
        errorPanel.add(textError, BorderLayout.SOUTH);

        chartPanel.add(errorPanel, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }
}