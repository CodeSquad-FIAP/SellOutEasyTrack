package view;

import controller.VendaController;
import model.Venda;
import util.ColorPalette;

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
        getContentPane().setBackground(new Color(38, 38, 38));

        JPanel painelTitulo = new JPanel();
        painelTitulo.setBackground(new Color(242, 48, 100));
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel titulo = new JLabel("LISTA DE VENDAS", JLabel.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
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
        tabelaVendas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaVendas.setBackground(new Color(64, 64, 64));
        tabelaVendas.setForeground(Color.WHITE);
        tabelaVendas.setGridColor(ColorPalette.FIAP_GRAY_MEDIUM);
        tabelaVendas.setSelectionBackground(ColorPalette.FIAP_PINK_DARK);
        tabelaVendas.setSelectionForeground(Color.WHITE);

        JTableHeader header = tabelaVendas.getTableHeader();
        header.setBackground(new Color(38, 38, 38));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createLineBorder(ColorPalette.FIAP_GRAY_DARK));

        JScrollPane scrollPane = new JScrollPane(tabelaVendas);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        scrollPane.getViewport().setBackground(new Color(64, 64, 64));

        JPanel painelEstatisticas = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        painelEstatisticas.setOpaque(false);
        painelEstatisticas.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ColorPalette.FIAP_GRAY_MEDIUM),
                "Estatísticas",
                0, 0, new Font("Segoe UI", Font.BOLD, 14), Color.WHITE));

        lblTotalVendas = new JLabel("Total de Vendas: 0");
        lblTotalVendas.setForeground(new Color(242, 48, 100));

        lblValorTotal = new JLabel("Valor Total: R$ 0,00");
        lblValorTotal.setForeground(new Color(242, 48, 100));

        painelEstatisticas.add(lblTotalVendas);
        JLabel separador = new JLabel("|");
        separador.setForeground(ColorPalette.FIAP_GRAY_MEDIUM);
        painelEstatisticas.add(separador);
        painelEstatisticas.add(lblValorTotal);

        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.setOpaque(false);

        JButton btnAtualizar = new JButton("Atualizar Lista");
        btnAtualizar.setBackground(new Color(242, 48, 100));
        btnAtualizar.setForeground(Color.WHITE);
        btnAtualizar.setFocusPainted(false);
        btnAtualizar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton btnFechar = new JButton("Fechar");
        btnFechar.setBackground(new Color(140, 140, 140));
        btnFechar.setForeground(Color.WHITE);
        btnFechar.setFocusPainted(false);
        btnFechar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btnAtualizar.addActionListener(e -> carregarVendas());
        btnFechar.addActionListener(e -> dispose());

        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnFechar);

        add(painelTitulo, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        JPanel painelSul = new JPanel(new BorderLayout());
        painelSul.setOpaque(false);
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