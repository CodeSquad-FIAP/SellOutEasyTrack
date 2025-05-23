package view;

import controller.VendaController;
import model.Venda;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class DashboardView extends JFrame {

    private VendaController vendaController = new VendaController();
    private ChartPanel chartPanel; // Mantém referência ao gráfico

    public DashboardView() {
        super("SellOut EasyTrack - Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Menu lateral refinado
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(new Color(40, 44, 52));
        menuPanel.setLayout(new GridLayout(6, 1, 0, 20)); // Aumentei para 6 posições
        menuPanel.setPreferredSize(new Dimension(200, 0));

        JButton btnRegistro = criarBotaoMenu("Registrar Venda");
        JButton btnRelatorio = criarBotaoMenu("Gerar Relatório");
        JButton btnExportarCSV = criarBotaoMenu("Exportar CSV");
        JButton btnAtualizarGrafico = criarBotaoMenu("Atualizar Gráfico"); // Novo botão
        JButton btnSair = criarBotaoMenu("Sair");

        btnRegistro.addActionListener(e -> new RegistroVendaView(this));
        btnRelatorio.addActionListener(e -> new RelatorioView(this));
        btnExportarCSV.addActionListener(e -> exportarCSV());
        btnAtualizarGrafico.addActionListener(e -> atualizarGrafico()); // Atualiza apenas o gráfico
        btnSair.addActionListener(e -> System.exit(0));

        menuPanel.add(new JLabel("<html><center><font color='white' size='5'>Menu</font></center></html>", JLabel.CENTER));
        menuPanel.add(btnRegistro);
        menuPanel.add(btnRelatorio);
        menuPanel.add(btnExportarCSV);
        menuPanel.add(btnAtualizarGrafico); // Adicionei aqui
        menuPanel.add(btnSair);

        // Painel central com gráfico
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.white);

        JLabel title = new JLabel("Produto Mais Vendido", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        contentPanel.add(title, BorderLayout.NORTH);

        // Mantém o ChartPanel como atributo
        chartPanel = new ChartPanel(gerarGraficoMaisVendido());
        contentPanel.add(chartPanel, BorderLayout.CENTER);

        add(menuPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JButton criarBotaoMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(60, 63, 65));
        btn.setForeground(Color.white);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        return btn;
    }

    private JFreeChart gerarGraficoMaisVendido() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try {
            List<Venda> vendas = vendaController.obterTodasVendas();
            Map<String, Integer> mapaVendas = new HashMap<>();

            for (Venda v : vendas) {
                mapaVendas.put(v.getProduto(),
                        mapaVendas.getOrDefault(v.getProduto(), 0) + v.getQuantidade());
            }

            for (Map.Entry<String, Integer> entry : mapaVendas.entrySet()) {
                dataset.addValue(entry.getValue(), "Quantidade Vendida", entry.getKey());
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados do gráfico: " + e.getMessage());
        }

        return ChartFactory.createBarChart(
                "Mais Vendidos",
                "Produto",
                "Quantidade",
                dataset
        );
    }

    // Atualiza apenas o gráfico no painel central
    private void atualizarGrafico() {
        chartPanel.setChart(gerarGraficoMaisVendido());
    }

    private void exportarCSV() {
        try {
            List<Venda> vendas = vendaController.obterTodasVendas();
            JFileChooser chooser = new JFileChooser();
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String path = chooser.getSelectedFile().getAbsolutePath() + ".csv";
                util.ReportUtil.exportarCSV(vendas, path);
                JOptionPane.showMessageDialog(this, "Exportado com sucesso para: " + path);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao exportar CSV: " + e.getMessage());
        }
    }
}
