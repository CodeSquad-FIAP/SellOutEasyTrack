package view;

import controller.VendaController;
import model.Venda;
import listener.VendaListener;

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
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(245, 245, 245));

        criarInterface();
        carregarVendas();
        setVisible(true);
    }

    private void criarInterface() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("ATUALIZAR VENDA", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(Color.BLACK);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(titulo, gbc);

        gbc.gridwidth = 1;

        JLabel lblSelecionar = new JLabel("Selecionar Venda:");
        lblSelecionar.setFont(new Font("Arial", Font.BOLD, 14));

        comboVendas = new JComboBox<>();
        comboVendas.setFont(new Font("Arial", Font.PLAIN, 12));
        comboVendas.addActionListener(e -> carregarDadosVenda());

        gbc.gridx = 0; gbc.gridy = 1; add(lblSelecionar, gbc);
        gbc.gridx = 1; add(comboVendas, gbc);

        JSeparator separador = new JSeparator();
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(separador, gbc);

        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblProduto = new JLabel("Produto:");
        lblProduto.setFont(new Font("Arial", Font.BOLD, 14));
        txtProduto = new JTextField(20);
        txtProduto.setFont(new Font("Arial", Font.PLAIN, 14));
        txtProduto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        txtProduto.setEnabled(false);

        JLabel lblQuantidade = new JLabel("Quantidade:");
        lblQuantidade.setFont(new Font("Arial", Font.BOLD, 14));
        txtQuantidade = new JTextField(20);
        txtQuantidade.setFont(new Font("Arial", Font.PLAIN, 14));
        txtQuantidade.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        txtQuantidade.setEnabled(false);

        JLabel lblValor = new JLabel("Valor Unitário (R$):");
        lblValor.setFont(new Font("Arial", Font.BOLD, 14));
        txtValorUnitario = new JTextField(20);
        txtValorUnitario.setFont(new Font("Arial", Font.PLAIN, 14));
        txtValorUnitario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        txtValorUnitario.setEnabled(false);

        gbc.gridx = 0; gbc.gridy = 3; add(lblProduto, gbc);
        gbc.gridx = 1; add(txtProduto, gbc);
        gbc.gridx = 0; gbc.gridy = 4; add(lblQuantidade, gbc);
        gbc.gridx = 1; add(txtQuantidade, gbc);
        gbc.gridx = 0; gbc.gridy = 5; add(lblValor, gbc);
        gbc.gridx = 1; add(txtValorUnitario, gbc);

        btnAtualizar = new JButton("Atualizar Venda");
        btnAtualizar.setBackground(new Color(220, 20, 60));
        btnAtualizar.setForeground(Color.WHITE);
        btnAtualizar.setFocusPainted(false);
        btnAtualizar.setFont(new Font("Arial", Font.BOLD, 16));
        btnAtualizar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btnAtualizar.setEnabled(false);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(Color.GRAY);
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 16));
        btnCancelar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        btnAtualizar.addActionListener(e -> atualizarVenda());
        btnCancelar.addActionListener(e -> dispose());

        JPanel panelBotoes = new JPanel(new FlowLayout());
        panelBotoes.setBackground(new Color(245, 245, 245));
        panelBotoes.add(btnAtualizar);
        panelBotoes.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        add(panelBotoes, gbc);
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