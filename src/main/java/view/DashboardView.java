package view;

import controller.VendaController;
import model.Venda;
import util.RGraphUtil;
import listener.VendaListener;

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

public class DashboardView extends JFrame implements VendaListener {

    private VendaController vendaController = new VendaController();
    private JPanel chartPanel;
    private JPopupMenu crudMenu;

    public DashboardView() {
        super("SellOut EasyTrack - Dashboard Executivo");
        configurarJanela();
        criarInterface();
        setVisible(true);
    }

    @Override
    public void onVendasChanged() {
        atualizarGrafico();
    }

    private void configurarJanela() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage("agent-seller-svgrepo-com"));
        } catch (Exception e) {
        }
    }

    private void criarInterface() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 241));

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
        header.setBackground(new Color(44, 62, 80));
        header.setBorder(new EmptyBorder(15, 25, 15, 25));
        header.setPreferredSize(new Dimension(0, 80));

        JLabel titulo = new JLabel("SellOut EasyTrack");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel("Sistema de Gestão de Vendas");
        subtitulo.setForeground(new Color(189, 195, 199));

        JPanel tituloPanel = new JPanel(new BorderLayout());
        tituloPanel.setOpaque(false);
        tituloPanel.add(titulo, BorderLayout.CENTER);
        tituloPanel.add(subtitulo, BorderLayout.SOUTH);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);

        JLabel userInfo = new JLabel("Dashboard Executivo");
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

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel menuTitle = new JLabel("MENU PRINCIPAL");
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        menuTitle.setForeground(new Color(149, 165, 166));

        titlePanel.add(menuTitle);
        sidebar.add(titlePanel);
        sidebar.add(Box.createVerticalStrut(10));

        sidebar.add(criarBotaoMenu("VENDAS", "Gerenciar vendas", this::mostrarMenuVendas));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(criarBotaoMenu("RELATÓRIOS", "Visualizar relatórios", this::abrirRelatorios));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(criarBotaoMenu("GRÁFICOS", "Atualizar gráficos", this::atualizarGrafico));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(criarBotaoMenu("EXPORTAR", "Exportar dados", this::exportarCSV));

        sidebar.add(Box.createVerticalGlue());

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
                        System.err.println("Erro ao executar ação do menu: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                buttonPanel.setBackground(new Color(191, 59, 94).darker());
                buttonPanel.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (buttonPanel.contains(e.getPoint())) {
                    buttonPanel.setBackground(new Color(191, 59, 94));
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
        content.setBackground(new Color(236, 240, 241));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel cardGrafico = criarCardGrafico();
        content.add(cardGrafico, BorderLayout.CENTER);

        return content;
    }

    private JPanel criarCardGrafico() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);
        cardHeader.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel cardTitle = new JLabel("Análise de Vendas");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        cardTitle.setForeground(new Color(44, 62, 80));

        JLabel cardSubtitle = new JLabel("Produtos com melhor desempenho");
        cardSubtitle.setForeground(new Color(38, 38, 38));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(cardTitle, BorderLayout.NORTH);
        titlePanel.add(cardSubtitle, BorderLayout.SOUTH);

        JButton btnRefresh = criarBotaoModerno("ATUALIZAR", new Color(52, 152, 219));
        btnRefresh.addActionListener(e -> atualizarGrafico());

        cardHeader.add(titlePanel, BorderLayout.WEST);
        cardHeader.add(btnRefresh, BorderLayout.EAST);

        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(236, 240, 241), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        chartPanel.setPreferredSize(new Dimension(0, 400));

        gerarGraficoR();

        card.add(cardHeader, BorderLayout.NORTH);
        card.add(chartPanel, BorderLayout.CENTER);

        return card;
    }

    private JButton criarBotaoModerno(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(cor);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
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

    private JPanel criarFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(127, 140, 141));
        footer.setBorder(new EmptyBorder(12, 25, 12, 25));
        footer.setPreferredSize(new Dimension(0, 45));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);

        JLabel footerText = new JLabel("SellOut EasyTrack - Sistema de Gestão de Vendas");
        footerText.setForeground(Color.WHITE);

        leftPanel.add(footerText);

        JLabel status = new JLabel("Sistema Online");
        status.setForeground(new Color(189, 195, 199));

        footer.add(leftPanel, BorderLayout.WEST);
        footer.add(status, BorderLayout.EAST);

        return footer;
    }

    private void configurarMenuCrud() {
        crudMenu = new JPopupMenu();
        crudMenu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 0, 5, 0)
        ));
        crudMenu.setBackground(Color.WHITE);

        JMenuItem criarItem = criarMenuItem("CRIAR VENDA", "Registrar nova venda no sistema");
        JMenuItem lerItem = criarMenuItem("LISTAR VENDAS", "Visualizar todas as vendas registradas");
        JMenuItem atualizarItem = criarMenuItem("ATUALIZAR VENDA", "Editar informações de venda existente");
        JMenuItem deletarItem = criarMenuItem("DELETAR VENDA", "Remover venda do sistema");

        criarItem.addActionListener(e -> {
            crudMenu.setVisible(false);
            new CriarVendaView(this, this);
        });

        lerItem.addActionListener(e -> {
            crudMenu.setVisible(false);
            new ListarVendasView(this);
        });

        atualizarItem.addActionListener(e -> {
            crudMenu.setVisible(false);
            new AtualizarVendaView(this, this);
        });

        deletarItem.addActionListener(e -> {
            crudMenu.setVisible(false);
            new DeletarVendaView(this, this);
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
        item.setBackground(Color.WHITE);
        item.setPreferredSize(new Dimension(250, 50));

        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tituloLabel.setForeground(new Color(44, 62, 80));

        JLabel descLabel = new JLabel(descricao);
        descLabel.setForeground(new Color(38, 38, 38));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(tituloLabel, BorderLayout.NORTH);
        textPanel.add(descLabel, BorderLayout.SOUTH);

        item.add(textPanel, BorderLayout.CENTER);

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                item.setBackground(new Color(236, 240, 241));
                item.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                item.setBackground(Color.WHITE);
                item.repaint();
            }
        });

        return item;
    }

    private void mostrarMenuVendas() {
        System.out.println(" Botão VENDAS clicado");
        try {
            if (crudMenu == null) {
                System.out.println(" Configurando menu CRUD...");
                configurarMenuCrud();
            }
            Point frameLocation = this.getLocationOnScreen();
            int menuX = 280;
            int menuY = 180;
            System.out.println(" Mostrando menu na posição: " + menuX + ", " + menuY);
            crudMenu.show(this, menuX, menuY);
        } catch (Exception e) {
            System.err.println(" Erro ao mostrar menu VENDAS: " + e.getMessage());
            e.printStackTrace();
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
            case 0: new CriarVendaView(this, this); break;
            case 1: new ListarVendasView(this); break;
            case 2: new AtualizarVendaView(this, this); break;
            case 3: new DeletarVendaView(this, this); break;
            default: break;
        }
    }

    private void abrirRelatorios() {
        try {
            System.out.println(" Gerando relatório automático...");
            List<Venda> vendas = vendaController.obterTodasVendas();
            if (vendas.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Nenhuma venda encontrada para gerar relatório.",
                        "Relatório Vazio",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int totalVendas = vendas.size();
            double somaTotal = vendas.stream().mapToDouble(v -> v.getQuantidade() * v.getValorUnitario()).sum();
            double ticketMedio = somaTotal / totalVendas;
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
            JTextArea areaRelatorio = new JTextArea(relatorio.toString());
            areaRelatorio.setFont(new Font("Courier New", Font.PLAIN, 12));
            areaRelatorio.setEditable(false);
            areaRelatorio.setBackground(Color.WHITE);
            areaRelatorio.setBorder(new EmptyBorder(20, 20, 20, 20));
            JScrollPane scrollPane = new JScrollPane(areaRelatorio);
            scrollPane.setPreferredSize(new Dimension(600, 400));
            JPanel painelRelatorio = new JPanel(new BorderLayout());
            painelRelatorio.add(scrollPane, BorderLayout.CENTER);
            JPanel painelBotoes = new JPanel(new FlowLayout());
            JButton btnSalvar = criarBotaoModerno("SALVAR RELATÓRIO", new Color(52, 152, 219));
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
            mostrarLoading();
            SwingWorker<String, Void> worker = new SwingWorker<>() {
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
        loading.setForeground(new Color(38, 38, 38));
        chartPanel.add(loading);
        chartPanel.revalidate();
        chartPanel.repaint();
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

                // Código para redimensionamento de alta qualidade
                java.awt.Image img = grafico.getImage();
                java.awt.image.BufferedImage resizedImg = new java.awt.image.BufferedImage(scaledWidth, scaledHeight, java.awt.image.BufferedImage.TYPE_INT_ARGB);
                java.awt.Graphics2D g2 = resizedImg.createGraphics();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.drawImage(img, 0, 0, scaledWidth, scaledHeight, null);
                g2.dispose();

                ImageIcon finalIcon = new ImageIcon(resizedImg);
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
        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.setOpaque(false);
        JLabel iconError = new JLabel("⚠", JLabel.CENTER);
        iconError.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        iconError.setForeground(new Color(231, 76, 60));
        JLabel textError = new JLabel(mensagem, JLabel.CENTER);
        textError.setForeground(new Color(38, 38, 38));
        errorPanel.add(iconError, BorderLayout.CENTER);
        errorPanel.add(textError, BorderLayout.SOUTH);
        chartPanel.add(errorPanel, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }
}