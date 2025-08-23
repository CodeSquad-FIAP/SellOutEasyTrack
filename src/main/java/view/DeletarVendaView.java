package view;

import controller.VendaController;
import model.Venda;
import listener.VendaListener;
import util.ColorPalette;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DeletarVendaView extends JDialog {

    private final VendaController vendaController = new VendaController();
    private JTable tabelaVendas;
    private DefaultTableModel modeloTabela;
    private JButton btnDeletar;
    private final VendaListener vendaListener;

    public DeletarVendaView(JFrame parent, VendaListener listener) {
        super(parent, "Deletar Venda", true);
        this.vendaListener = listener;
        setSize(850, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(64, 64, 64));

        criarInterface();
        carregarVendas();
        setVisible(true);
    }

    private void criarInterface() {
        JPanel painelTitulo = new JPanel();
        painelTitulo.setBackground(ColorPalette.FIAP_PINK_DARK);
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel titulo = new JLabel("DELETAR VENDA", JLabel.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        painelTitulo.add(titulo);

        JPanel painelInstrucoes = new JPanel();
        painelInstrucoes.setBackground(new Color(100, 100, 100));
        painelInstrucoes.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.WARNING_AMBER, 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        JLabel lblInstrucoes = new JLabel(
                "<html><b style='color:white;'> ATENÇÃO:</b><font color='white'> Selecione uma venda na tabela abaixo e clique em 'Deletar Selecionada' para remover permanentemente.</font></html>");

        painelInstrucoes.add(lblInstrucoes);

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
        tabelaVendas.setGridColor(ColorPalette.FIAP_GRAY_DARK);
        tabelaVendas.setSelectionBackground(ColorPalette.Buttons.DANGER_HOVER);
        tabelaVendas.setSelectionForeground(Color.WHITE);

        JTableHeader header = tabelaVendas.getTableHeader();
        header.setBackground(new Color(38, 38, 38));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createLineBorder(ColorPalette.FIAP_GRAY_DARK));

        tabelaVendas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnDeletar.setEnabled(tabelaVendas.getSelectedRow() != -1);
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabelaVendas);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        scrollPane.getViewport().setBackground(new Color(64, 64, 64));

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        painelBotoes.setOpaque(false);

        btnDeletar = new JButton("Deletar Selecionada");
        btnDeletar.setBackground(ColorPalette.Buttons.DANGER_NORMAL);
        btnDeletar.setForeground(ColorPalette.Buttons.DANGER_TEXT);
        btnDeletar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btnDeletar.setEnabled(false);

        JButton btnAtualizar = new JButton("Atualizar Lista");
        btnAtualizar.setBackground(new Color(242, 48, 100));
        btnAtualizar.setForeground(Color.WHITE);
        btnAtualizar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JButton btnFechar = new JButton("Fechar");
        btnFechar.setBackground(new Color(140, 140, 140));
        btnFechar.setForeground(Color.WHITE);
        btnFechar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        btnDeletar.addActionListener(e -> deletarVendaSelecionada());
        btnAtualizar.addActionListener(e -> carregarVendas());
        btnFechar.addActionListener(e -> dispose());

        painelBotoes.add(btnDeletar);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnFechar);

        JPanel painelCentral = new JPanel(new BorderLayout());
        painelCentral.setOpaque(false);
        painelCentral.add(painelInstrucoes, BorderLayout.NORTH);
        painelCentral.add(scrollPane, BorderLayout.CENTER);

        add(painelTitulo, BorderLayout.NORTH);
        add(painelCentral, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void carregarVendas() {
        try {
            tabelaVendas.clearSelection();
            btnDeletar.setEnabled(false);
            modeloTabela.setRowCount(0);

            List<Venda> vendas = vendaController.obterTodasVendas();

            if (vendas.isEmpty()) {
                Object[] linhaSemDados = {"—", "Nenhuma venda encontrada", "—", "—", "—", "—"};
                modeloTabela.addRow(linhaSemDados);
                return;
            }

            NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

            for (Venda venda : vendas) {
                double valorTotal = venda.getQuantidade() * venda.getValorUnitario();

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

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    " Erro ao carregar vendas:\n\n" + e.getMessage(),
                    "Erro de Banco de Dados",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    " Erro inesperado:\n\n" + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletarVendaSelecionada() {
        int linhaSelecionada = tabelaVendas.getSelectedRow();

        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this,
                    " Por favor, selecione uma venda para deletar!",
                    "Nenhuma Seleção",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object idObj = modeloTabela.getValueAt(linhaSelecionada, 0);
        if (!(idObj instanceof Integer)) {
            JOptionPane.showMessageDialog(this,
                    " Não é possível deletar: nenhuma venda válida selecionada!",
                    "Seleção Inválida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (Integer) idObj;
        String produto = (String) modeloTabela.getValueAt(linhaSelecionada, 1);

        String[] opcoes = {" Deletar", " Cancelar"};
        int resposta = JOptionPane.showOptionDialog(this,
                "Confirma a exclusão permanente da venda do produto " + produto + "?",
                "Confirmar Exclusão Permanente",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                opcoes,
                opcoes[1]);

        if (resposta == 0) {
            try {
                vendaController.deletarVenda(id);

                if (vendaListener != null) {
                    vendaListener.onVendasChanged();
                }

                JOptionPane.showMessageDialog(this,
                        " Venda deletada com sucesso!",
                        "Exclusão Realizada",
                        JOptionPane.INFORMATION_MESSAGE);

                carregarVendas();

            } catch (SQLException ex) {
                String mensagemErro = " Erro ao deletar venda do banco de dados:\n\n" + ex.getMessage();
                if (ex.getMessage().contains("não encontrada")) {
                    mensagemErro = " A venda selecionada não existe mais no banco de dados.\n" +
                            "A lista será atualizada automaticamente.";
                    carregarVendas();
                }

                JOptionPane.showMessageDialog(this,
                        mensagemErro,
                        "Erro de Banco de Dados",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        " Erro inesperado ao deletar venda:\n\n" + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}