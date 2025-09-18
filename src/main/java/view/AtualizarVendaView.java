package view;

import controller.VendaController;
import model.Venda;
import listener.VendaListener;
import util.ColorPalette;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AtualizarVendaView extends JDialog {

    private final VendaController vendaController = new VendaController();
    private JComboBox<VendaComboItem> comboVendas;
    private JTextField txtProduto;
    private JTextField txtQuantidade;
    private JTextField txtValorUnitario;
    private JButton btnAtualizar;
    private Venda vendaSelecionada;
    private final VendaListener vendaListener;

    public AtualizarVendaView(JFrame parent, VendaListener listener) {
        super(parent, "Atualizar Venda", true);
        this.vendaListener = listener;
        setSize(550, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(64, 64, 64));

        criarInterface();
        carregarVendas();
        setVisible(true);
    }

    private void criarInterface() {
        JPanel painelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelTitulo.setBackground(new Color(242, 48, 100));
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        JLabel titulo = new JLabel("ATUALIZAR VENDA");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        painelTitulo.add(titulo);

        JPanel painelPrincipal = new JPanel(new GridBagLayout());
        painelPrincipal.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblSelecionar = new JLabel("Selecionar Venda:");
        lblSelecionar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSelecionar.setForeground(Color.WHITE);

        comboVendas = new JComboBox<>();

        comboVendas.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setForeground(Color.WHITE);
                if (isSelected) {
                    setBackground(ColorPalette.FIAP_PINK_DARK);
                } else {
                    setBackground(new Color(64, 64, 64));
                }
                return this;
            }
        });

        comboVendas.addActionListener(e -> carregarDadosVenda());

        gbc.gridx = 0; gbc.gridy = 1; painelPrincipal.add(lblSelecionar, gbc);
        gbc.gridx = 1; painelPrincipal.add(comboVendas, gbc);

        JLabel lblProduto = new JLabel("Produto:");
        lblProduto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblProduto.setForeground(Color.WHITE);

        txtProduto = new JTextField(20);
        txtProduto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.FIAP_GRAY_MEDIUM),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        txtProduto.setBackground(new Color(38, 38, 38));
        txtProduto.setForeground(Color.WHITE);
        txtProduto.setCaretColor(Color.WHITE);

        JLabel lblQuantidade = new JLabel("Quantidade:");
        lblQuantidade.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblQuantidade.setForeground(Color.WHITE);

        txtQuantidade = new JTextField(20);
        txtQuantidade.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.FIAP_GRAY_MEDIUM),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        txtQuantidade.setBackground(new Color(38, 38, 38));
        txtQuantidade.setForeground(Color.WHITE);
        txtQuantidade.setCaretColor(Color.WHITE);

        JLabel lblValor = new JLabel("Valor Unitário (R$):");
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblValor.setForeground(Color.WHITE);

        txtValorUnitario = new JTextField(20);
        txtValorUnitario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.FIAP_GRAY_MEDIUM),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        txtValorUnitario.setBackground(new Color(38, 38, 38));
        txtValorUnitario.setForeground(Color.WHITE);
        txtValorUnitario.setCaretColor(Color.WHITE);

        gbc.gridx = 0; gbc.gridy = 3; painelPrincipal.add(lblProduto, gbc);
        gbc.gridx = 1; painelPrincipal.add(txtProduto, gbc);
        gbc.gridx = 0; gbc.gridy = 4; painelPrincipal.add(lblQuantidade, gbc);
        gbc.gridx = 1; painelPrincipal.add(txtQuantidade, gbc);
        gbc.gridx = 0; gbc.gridy = 5; painelPrincipal.add(lblValor, gbc);
        gbc.gridx = 1; painelPrincipal.add(txtValorUnitario, gbc);

        btnAtualizar = new JButton("Atualizar Venda");
        btnAtualizar.setBackground(new Color(242, 48, 100));
        btnAtualizar.setForeground(Color.WHITE);
        btnAtualizar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btnAtualizar.setEnabled(false);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(140, 140, 140));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        btnAtualizar.addActionListener(e -> atualizarVenda());
        btnCancelar.addActionListener(e -> dispose());

        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panelBotoes.setOpaque(false);

        JPanel wrapperBotoes = new JPanel(new GridLayout(1, 0, 10, 0));
        wrapperBotoes.setOpaque(false);
        wrapperBotoes.add(btnAtualizar);
        wrapperBotoes.add(btnCancelar);
        panelBotoes.add(wrapperBotoes);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 15, 15, 15);
        painelPrincipal.add(panelBotoes, gbc);

        add(painelTitulo, BorderLayout.NORTH);
        add(painelPrincipal, BorderLayout.CENTER);
    }

    private void carregarVendas() {
        try {
            List<Venda> vendas = vendaController.obterTodasVendas();
            comboVendas.removeAllItems();
            comboVendas.addItem(new VendaComboItem(null, "-- Selecione uma venda --"));
            for (Venda venda : vendas) {
                comboVendas.addItem(new VendaComboItem(venda,
                        String.format("ID: %d - %s (Qtd: %d)", venda.getId(), venda.getProduto(), venda.getQuantidade())));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar vendas: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarDadosVenda() {
        VendaComboItem item = (VendaComboItem) comboVendas.getSelectedItem();
        if (item != null && item.getVenda() != null) {
            vendaSelecionada = item.getVenda();
            txtProduto.setText(vendaSelecionada.getProduto());
            txtQuantidade.setText(String.valueOf(vendaSelecionada.getQuantidade()));
            txtValorUnitario.setText(String.valueOf(vendaSelecionada.getValorUnitario()));
            txtProduto.setEnabled(true);
            txtQuantidade.setEnabled(true);
            txtValorUnitario.setEnabled(true);
            btnAtualizar.setEnabled(true);
        } else {
            vendaSelecionada = null;
            txtProduto.setText("");
            txtQuantidade.setText("");
            txtValorUnitario.setText("");
            txtProduto.setEnabled(false);
            txtQuantidade.setEnabled(false);
            txtValorUnitario.setEnabled(false);
            btnAtualizar.setEnabled(false);
        }
    }

    private void atualizarVenda() {
        if (vendaSelecionada == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma venda para atualizar!", "Nenhuma Venda Selecionada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String produto = txtProduto.getText().trim();
            if (produto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, informe o nome do produto!", "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
                txtProduto.requestFocus();
                return;
            }

            String quantidadeStr = txtQuantidade.getText().trim();
            if (quantidadeStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, informe a quantidade!", "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
                txtQuantidade.requestFocus();
                return;
            }

            String valorStr = txtValorUnitario.getText().trim().replace(",", ".");
            if (valorStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, informe o valor unitário!", "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
                txtValorUnitario.requestFocus();
                return;
            }

            int quantidade = Integer.parseInt(quantidadeStr);
            double valor = Double.parseDouble(valorStr);

            int resposta = JOptionPane.showConfirmDialog(this, "Confirma a atualização desta venda?", "Confirmar Atualização", JOptionPane.YES_NO_OPTION);

            if (resposta == JOptionPane.YES_OPTION) {
                Venda vendaAtualizada = new Venda(vendaSelecionada.getId(), produto, quantidade, valor, vendaSelecionada.getData());
                vendaController.atualizarVenda(vendaAtualizada);

                if (vendaListener != null) {
                    vendaListener.onVendasChanged();
                }

                JOptionPane.showMessageDialog(this, "Venda atualizada com sucesso!", "Atualização Realizada", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valores de quantidade e/ou valor unitário são inválidos.", "Formato Inválido", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar venda: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class VendaComboItem {
        private final Venda venda;
        private final String texto;

        public VendaComboItem(Venda venda, String texto) {
            this.venda = venda;
            this.texto = texto;
        }

        public Venda getVenda() {
            return venda;
        }

        @Override
        public String toString() {
            return texto;
        }
    }
}