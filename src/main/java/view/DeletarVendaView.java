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

    // Cores padronizadas
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color SECONDARY_COLOR = new Color(149, 165, 166);

    public DeletarVendaView(JFrame parent) {
        super(parent, "Deletar Venda", true);
        setSize(850, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        criarInterface();
        carregarVendas();
        setVisible(true);
    }

    private void criarInterface() {
        // Painel do título
        JPanel painelTitulo = new JPanel();
        painelTitulo.setBackground(DANGER_COLOR);
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel titulo = new JLabel("DELETAR VENDA", JLabel.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        painelTitulo.add(titulo);

        // Painel de instruções
        JPanel painelInstrucoes = new JPanel();
        painelInstrucoes.setBackground(new Color(255, 248, 220));
        painelInstrucoes.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 193, 7), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        JLabel lblInstrucoes = new JLabel(
                "<html><b>⚠️ ATENÇÃO:</b> Selecione uma venda na tabela abaixo e clique em 'Deletar Selecionada' para remover permanentemente.</html>");
        lblInstrucoes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInstrucoes.setForeground(new Color(133, 100, 4));
        painelInstrucoes.add(lblInstrucoes);

        // Configurar tabela
        String[] colunas = {"ID", "Produto", "Quantidade", "Valor Unit. (R$)", "Total (R$)", "Data"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaVendas = new JTable(modeloTabela);
        tabelaVendas.setRowHeight(30);
        tabelaVendas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabelaVendas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaVendas.setGridColor(new Color(230, 230, 230));
        tabelaVendas.setSelectionBackground(new Color(255, 235, 235));
        tabelaVendas.setSelectionForeground(Color.BLACK);

        JTableHeader header = tabelaVendas.getTableHeader();
        header.setBackground(new Color(240, 240, 240));
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createRaisedBevelBorder());

        // Ajustar larguras das colunas
        tabelaVendas.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabelaVendas.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabelaVendas.getColumnModel().getColumn(2).setPreferredWidth(80);
        tabelaVendas.getColumnModel().getColumn(3).setPreferredWidth(100);
        tabelaVendas.getColumnModel().getColumn(4).setPreferredWidth(100);
        tabelaVendas.getColumnModel().getColumn(5).setPreferredWidth(120);

        tabelaVendas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnDeletar.setEnabled(tabelaVendas.getSelectedRow() != -1);
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabelaVendas);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        // CORRIGIDO: Painel de botões padronizados
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        painelBotoes.setBackground(Color.WHITE);

        // Botão Deletar - Destaque vermelho
        btnDeletar = new JButton("Deletar Selecionada");
        btnDeletar.setBackground(DANGER_COLOR);
        btnDeletar.setForeground(Color.WHITE);
        btnDeletar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDeletar.setFocusPainted(false);
        btnDeletar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btnDeletar.setPreferredSize(new Dimension(160, 45));
        btnDeletar.setEnabled(false);
        btnDeletar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Botão Atualizar - Azul
        JButton btnAtualizar = new JButton("Atualizar Lista");
        btnAtualizar.setBackground(PRIMARY_COLOR);
        btnAtualizar.setForeground(Color.WHITE);
        btnAtualizar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAtualizar.setFocusPainted(false);
        btnAtualizar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btnAtualizar.setPreferredSize(new Dimension(160, 45));
        btnAtualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Botão Fechar - Cinza
        JButton btnFechar = new JButton("Fechar");
        btnFechar.setBackground(SECONDARY_COLOR);
        btnFechar.setForeground(Color.WHITE);
        btnFechar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnFechar.setFocusPainted(false);
        btnFechar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btnFechar.setPreferredSize(new Dimension(160, 45));
        btnFechar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Adicionar efeitos hover aos botões
        adicionarEfeitoHover(btnDeletar, DANGER_COLOR);
        adicionarEfeitoHover(btnAtualizar, PRIMARY_COLOR);
        adicionarEfeitoHover(btnFechar, SECONDARY_COLOR);

        // Ações dos botões
        btnDeletar.addActionListener(e -> deletarVendaSelecionada());
        btnAtualizar.addActionListener(e -> carregarVendas());
        btnFechar.addActionListener(e -> dispose());

        painelBotoes.add(btnDeletar);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnFechar);

        // Montar layout
        JPanel painelCentral = new JPanel(new BorderLayout());
        painelCentral.add(painelInstrucoes, BorderLayout.NORTH);
        painelCentral.add(scrollPane, BorderLayout.CENTER);

        add(painelTitulo, BorderLayout.NORTH);
        add(painelCentral, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void adicionarEfeitoHover(JButton botao, Color corOriginal) {
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
                    "❌ Erro ao carregar vendas:\n\n" + e.getMessage(),
                    "Erro de Banco de Dados",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "❌ Erro inesperado:\n\n" + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletarVendaSelecionada() {
        int linhaSelecionada = tabelaVendas.getSelectedRow();

        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ Por favor, selecione uma venda para deletar!",
                    "Nenhuma Seleção",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object idObj = modeloTabela.getValueAt(linhaSelecionada, 0);
        if (idObj.equals("—")) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ Não é possível deletar: nenhuma venda válida selecionada!",
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

        // Dialog de confirmação melhorado
        JPanel confirmPanel = new JPanel(new BorderLayout());
        confirmPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel iconLabel = new JLabel("⚠️", JLabel.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        iconLabel.setForeground(DANGER_COLOR);

        JLabel mensagemLabel = new JLabel("<html><center><b>ATENÇÃO: Esta ação não pode ser desfeita!</b><br><br>" +
                "Confirma a exclusão permanente desta venda?</center></html>");
        mensagemLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mensagemLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel detalhesPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        detalhesPanel.setBorder(BorderFactory.createTitledBorder("Detalhes da venda:"));
        detalhesPanel.setBackground(new Color(248, 249, 250));

        detalhesPanel.add(new JLabel("ID:"));
        detalhesPanel.add(new JLabel(String.valueOf(id)));
        detalhesPanel.add(new JLabel("Produto:"));
        detalhesPanel.add(new JLabel(produto));
        detalhesPanel.add(new JLabel("Quantidade:"));
        detalhesPanel.add(new JLabel(String.valueOf(quantidade)));
        detalhesPanel.add(new JLabel("Valor Unitário:"));
        detalhesPanel.add(new JLabel(valorStr));
        detalhesPanel.add(new JLabel("Total:"));
        detalhesPanel.add(new JLabel(totalStr));

        confirmPanel.add(iconLabel, BorderLayout.NORTH);
        confirmPanel.add(mensagemLabel, BorderLayout.CENTER);
        confirmPanel.add(detalhesPanel, BorderLayout.SOUTH);

        String[] opcoes = {"🗑️ Deletar", "❌ Cancelar"};
        int resposta = JOptionPane.showOptionDialog(this,
                confirmPanel,
                "Confirmar Exclusão Permanente",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                opcoes,
                opcoes[1]);

        if (resposta == 0) { // Deletar
            try {
                vendaController.deletarVenda(id);

                JOptionPane.showMessageDialog(this,
                        "✅ Venda deletada com sucesso!\n\n" +
                                "Venda removida:\n" +
                                "• ID: " + id + "\n" +
                                "• Produto: " + produto + "\n" +
                                "• Quantidade: " + quantidade + "\n" +
                                "• Total: " + totalStr,
                        "Exclusão Realizada",
                        JOptionPane.INFORMATION_MESSAGE);

                carregarVendas();

            } catch (SQLException ex) {
                String mensagemErro = "❌ Erro ao deletar venda do banco de dados:\n\n" + ex.getMessage();
                if (ex.getMessage().contains("não encontrada")) {
                    mensagemErro = "⚠️ A venda selecionada não existe mais no banco de dados.\n" +
                            "A lista será atualizada automaticamente.";
                    carregarVendas();
                }

                JOptionPane.showMessageDialog(this,
                        mensagemErro,
                        "Erro de Banco de Dados",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "❌ Erro inesperado ao deletar venda:\n\n" + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}