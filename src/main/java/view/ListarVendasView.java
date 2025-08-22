package view;

import controller.VendaController;
import model.Venda;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ListarVendasView extends JDialog {

    private final VendaController vendaController = new VendaController();
    private JTable tabelaVendas;
    private DefaultTableModel modeloTabela;
    private JLabel lblTotalVendas;
    private JLabel lblValorTotal;

    public ListarVendasView(JFrame parent) {
        super(parent, "Listar Vendas", true);
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        JPanel painelTitulo = new JPanel();
        painelTitulo.setBackground(new Color(52, 152, 219));
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel titulo = new JLabel("LISTA DE VENDAS", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        painelTitulo.add(titulo);

        String[] colunas = {"ID", "Produto", "Quantidade", "Valor Unit. (R$)", "Total (R$)", "Data"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaVendas = new JTable(modeloTabela);
        tabelaVendas.setRowHeight(30);
        tabelaVendas.setFont(new Font("Arial", Font.PLAIN, 12));
        tabelaVendas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaVendas.setGridColor(new Color(230, 230, 230));

        JTableHeader header = tabelaVendas.getTableHeader();
        header.setBackground(new Color(240, 240, 240));
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setBorder(BorderFactory.createRaisedBevelBorder());

        tabelaVendas.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabelaVendas.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabelaVendas.getColumnModel().getColumn(2).setPreferredWidth(80);
        tabelaVendas.getColumnModel().getColumn(3).setPreferredWidth(100);
        tabelaVendas.getColumnModel().getColumn(4).setPreferredWidth(100);
        tabelaVendas.getColumnModel().getColumn(5).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(tabelaVendas);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JPanel painelEstatisticas = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        painelEstatisticas.setBackground(new Color(245, 245, 245));
        painelEstatisticas.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Estatísticas",
                0, 0, new Font("Arial", Font.BOLD, 14)));

        lblTotalVendas = new JLabel("Total de Vendas: 0");
        lblTotalVendas.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotalVendas.setForeground(new Color(242, 48, 100));

        lblValorTotal = new JLabel("Valor Total: R$ 0,00");
        lblValorTotal.setFont(new Font("Arial", Font.BOLD, 14));
        lblValorTotal.setForeground(new Color(46, 204, 113));

        painelEstatisticas.add(lblTotalVendas);
        painelEstatisticas.add(new JLabel("|"));
        painelEstatisticas.add(lblValorTotal);

        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.setBackground(Color.WHITE);

        JButton btnAtualizar = new JButton("Atualizar Lista");
        btnAtualizar.setBackground(new Color(52, 152, 219));
        btnAtualizar.setForeground(Color.WHITE);
        btnAtualizar.setFont(new Font("Arial", Font.BOLD, 14));
        btnAtualizar.setFocusPainted(false);
        btnAtualizar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton btnFechar = new JButton("Fechar");
        btnFechar.setBackground(Color.GRAY);
        btnFechar.setForeground(Color.WHITE);
        btnFechar.setFont(new Font("Arial", Font.BOLD, 14));
        btnFechar.setFocusPainted(false);
        btnFechar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btnAtualizar.addActionListener(e -> carregarVendas());
        btnFechar.addActionListener(e -> dispose());

        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnFechar);

        add(painelTitulo, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        JPanel painelSul = new JPanel(new BorderLayout());
        painelSul.add(painelEstatisticas, BorderLayout.CENTER);
        painelSul.add(painelBotoes, BorderLayout.SOUTH);
        add(painelSul, BorderLayout.SOUTH);

        carregarVendas();

        setVisible(true);
    }

    private void carregarVendas() {
        try {
            modeloTabela.setRowCount(0);

            List<Venda> vendas = vendaController.obterTodasVendas();

            if (vendas.isEmpty()) {
                // Lógica para mostrar mensagem de "nenhuma venda"
                return;
            }

            NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            double valorTotalGeral = 0;

            for (Venda venda : vendas) {
                double valorTotal = venda.getQuantidade() * venda.getValorUnitario();
                valorTotalGeral += valorTotal;

                Object[] linha = {
                        venda.getId(),
                        venda.getProduto(),
                        venda.getQuantidade(),
                        formatoMoeda.format(venda.getValorUnitario()),
                        formatoMoeda.format(valorTotal),
                        venda.getData().toString()
                };

                modeloTabela.addRow(linha);
            }

            lblTotalVendas.setText("Total de Vendas: " + vendas.size());
            lblValorTotal.setText("Valor Total: " + formatoMoeda.format(valorTotalGeral));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar vendas: " + e.getMessage(),
                    "Erro de Banco de Dados",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro inesperado: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}