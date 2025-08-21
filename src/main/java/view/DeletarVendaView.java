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

public class DeletarVendaView extends JDialog {

    private VendaController vendaController = new VendaController();
    private JTable tabelaVendas;
    private DefaultTableModel modeloTabela;
    private JButton btnDeletar;

    public DeletarVendaView(JFrame parent) {
        super(parent, "Deletar Venda", true);
        setSize(850, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Painel do título
        JPanel painelTitulo = new JPanel();
        painelTitulo.setBackground(new Color(231, 76, 60));
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel titulo = new JLabel("DELETAR VENDA", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        painelTitulo.add(titulo);

        // Painel de instruções
        JPanel painelInstrucoes = new JPanel();
        painelInstrucoes.setBackground(new Color(255, 248, 220));
        painelInstrucoes.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 193, 7), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        JLabel lblInstrucoes = new JLabel(
                "<html><b>ATENÇÃO:</b> Selecione uma venda na tabela abaixo e clique em 'Deletar Selecionada' para remover permanentemente.</html>");
        lblInstrucoes.setFont(new Font("Arial", Font.PLAIN, 12));
        lblInstrucoes.setForeground(new Color(133, 100, 4));
        painelInstrucoes.add(lblInstrucoes);

        // Configurar tabela
        String[] colunas = {"ID", "Produto", "Quantidade", "Valor Unit. (R$)", "Total (R$)", "Data"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabela apenas para leitura
            }
        };

        tabelaVendas = new JTable(modeloTabela);
        tabelaVendas.setRowHeight(30);
        tabelaVendas.setFont(new Font("Arial", Font.PLAIN, 12));
        tabelaVendas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaVendas.setGridColor(new Color(230, 230, 230));

        // Configurar header da tabela
        JTableHeader header = tabelaVendas.getTableHeader();
        header.setBackground(new Color(240, 240, 240));
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setBorder(BorderFactory.createRaisedBevelBorder());

        // Configurar larguras das colunas
        tabelaVendas.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tabelaVendas.getColumnModel().getColumn(1).setPreferredWidth(200); // Produto
        tabelaVendas.getColumnModel().getColumn(2).setPreferredWidth(80);  // Quantidade
        tabelaVendas.getColumnModel().getColumn(3).setPreferredWidth(100); // Valor Unit.
        tabelaVendas.getColumnModel().getColumn(4).setPreferredWidth(100); // Total
        tabelaVendas.getColumnModel().getColumn(5).setPreferredWidth(120); // Data

        // Listener para seleção da tabela
        tabelaVendas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnDeletar.setEnabled(tabelaVendas.getSelectedRow() != -1);
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabelaVendas);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.setBackground(Color.WHITE);

        btnDeletar = new JButton("Deletar Selecionada");
        btnDeletar.setBackground(new Color(231, 76, 60));
        btnDeletar.setForeground(Color.WHITE);
        btnDeletar.setFont(new Font("Arial", Font.BOLD, 16));
        btnDeletar.setFocusPainted(false);
        btnDeletar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btnDeletar.setEnabled(false); // Inicia desabilitado

        JButton btnAtualizar = new JButton("Atualizar Lista");
        btnAtualizar.setBackground(new Color(52, 152, 219));
        btnAtualizar.setForeground(Color.WHITE);
        btnAtualizar.setFont(new Font("Arial", Font.BOLD, 14));
        btnAtualizar.setFocusPainted(false);
        btnAtualizar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton btnFechar = new JButton("Fechar");
        btnFechar.setBackground(new Color(149, 165, 166));
        btnFechar.setForeground(Color.WHITE);
        btnFechar.setFont(new Font("Arial", Font.BOLD, 14));
        btnFechar.setFocusPainted(false);
        btnFechar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Event listeners
        btnDeletar.addActionListener(e -> deletarVendaSelecionada());
        btnAtualizar.addActionListener(e -> carregarVendas());
        btnFechar.addActionListener(e -> dispose());

        painelBotoes.add(btnDeletar);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnFechar);

        // Montar o layout
        JPanel painelCentral = new JPanel(new BorderLayout());
        painelCentral.add(painelInstrucoes, BorderLayout.NORTH);
        painelCentral.add(scrollPane, BorderLayout.CENTER);

        add(painelTitulo, BorderLayout.NORTH);
        add(painelCentral, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        // Carregar dados iniciais
        carregarVendas();

        setVisible(true);
    }

    private void carregarVendas() {
        try {
            // Limpar seleção e desabilitar botão
            tabelaVendas.clearSelection();
            btnDeletar.setEnabled(false);

            // Limpar tabela
            modeloTabela.setRowCount(0);

            // Buscar vendas
            List<Venda> vendas = vendaController.obterTodasVendas();

            if (vendas.isEmpty()) {
                // Mostrar mensagem se não há vendas
                Object[] linhaSemDados = {"—", "Nenhuma venda encontrada", "—", "—", "—", "—"};
                modeloTabela.addRow(linhaSemDados);
                return;
            }

            NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

            // Preencher tabela
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

    private void deletarVendaSelecionada() {
        int linhaSelecionada = tabelaVendas.getSelectedRow();

        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, selecione uma venda para deletar!",
                    "Nenhuma Seleção",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obter dados da linha selecionada
        Object idObj = modeloTabela.getValueAt(linhaSelecionada, 0);
        if (idObj.equals("—")) {
            JOptionPane.showMessageDialog(this,
                    "Não é possível deletar: nenhuma venda válida selecionada!",
                    "Seleção Inválida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (Integer) idObj;
        String produto = (String) modeloTabela.getValueAt(linhaSelecionada, 1);
        Integer quantidade = (Integer) modeloTabela.getValueAt(linhaSelecionada, 2);
        String valorStr = (String) modeloTabela.getValueAt(linhaSelecionada, 3);
        String totalStr = (String) modeloTabela.getValueAt(linhaSelecionada, 4);
        String data = (String) modeloTabela.getValueAt(linhaSelecionada, 5);

        // Confirmação da exclusão com detalhes
        int resposta = JOptionPane.showConfirmDialog(this,
                "ATENÇÃO: Esta ação não pode ser desfeita!\n\n" +
                        "Confirma a exclusão permanente desta venda?\n\n" +
                        "Detalhes da venda:\n" +
                        "• ID: " + id + "\n" +
                        "• Produto: " + produto + "\n" +
                        "• Quantidade: " + quantidade + "\n" +
                        "• Valor Unitário: " + valorStr + "\n" +
                        "• Total: " + totalStr + "\n" +
                        "• Data: " + data,
                "Confirmar Exclusão Permanente",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (resposta == JOptionPane.YES_OPTION) {
            try {
                // Chama o controller para deletar a venda
                vendaController.deletarVenda(id);

                JOptionPane.showMessageDialog(this,
                        "Venda deletada com sucesso!\n\n" +
                                "Venda removida:\n" +
                                "• ID: " + id + "\n" +
                                "• Produto: " + produto + "\n" +
                                "• Quantidade: " + quantidade,
                        "Exclusão Realizada",
                        JOptionPane.INFORMATION_MESSAGE);

                // Recarregar a lista após a exclusão
                carregarVendas();

            } catch (SQLException ex) {
                String mensagemErro = "Erro ao deletar venda do banco de dados:\n" + ex.getMessage();
                if (ex.getMessage().contains("não encontrada")) {
                    mensagemErro = "A venda selecionada não existe mais no banco de dados.\n" +
                            "A lista será atualizada automaticamente.";
                    carregarVendas(); // Recarrega para mostrar estado atual
                }

                JOptionPane.showMessageDialog(this,
                        mensagemErro,
                        "Erro de Banco de Dados",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erro inesperado ao deletar venda:\n" + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}