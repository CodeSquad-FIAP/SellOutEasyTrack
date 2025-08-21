package view;

import controller.VendaController;
import model.Venda;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AtualizarVendaView extends JDialog {

    private VendaController vendaController = new VendaController();
    private JComboBox<VendaComboItem> comboVendas;
    private JTextField txtProduto;
    private JTextField txtQuantidade;
    private JTextField txtValorUnitario;
    private JButton btnAtualizar; // Referência direta ao botão
    private Venda vendaSelecionada;

    public AtualizarVendaView(JFrame parent) {
        super(parent, "Atualizar Venda", true);
        setSize(550, 500);
        setLocationRelativeTo(parent);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(245, 245, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel titulo = new JLabel("ATUALIZAR VENDA", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(230, 126, 34));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(titulo, gbc);

        // Reset gridwidth
        gbc.gridwidth = 1;

        // Seleção de venda
        JLabel lblSelecionar = new JLabel("Selecionar Venda:");
        lblSelecionar.setFont(new Font("Arial", Font.BOLD, 14));

        comboVendas = new JComboBox<>();
        comboVendas.setFont(new Font("Arial", Font.PLAIN, 12));
        comboVendas.addActionListener(e -> carregarDadosVenda());

        gbc.gridx = 0; gbc.gridy = 1;
        add(lblSelecionar, gbc);
        gbc.gridx = 1;
        add(comboVendas, gbc);

        // Separador visual
        JSeparator separador = new JSeparator();
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(separador, gbc);

        // Reset para campos de formulário
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campos do formulário
        JLabel lblProduto = new JLabel("Produto:");
        lblProduto.setFont(new Font("Arial", Font.BOLD, 14));
        txtProduto = new JTextField(20);
        txtProduto.setFont(new Font("Arial", Font.PLAIN, 14));
        txtProduto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        txtProduto.setEnabled(false); // Inicia desabilitado

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

        // Layout dos campos
        gbc.gridx = 0; gbc.gridy = 3;
        add(lblProduto, gbc);
        gbc.gridx = 1;
        add(txtProduto, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        add(lblQuantidade, gbc);
        gbc.gridx = 1;
        add(txtQuantidade, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        add(lblValor, gbc);
        gbc.gridx = 1;
        add(txtValorUnitario, gbc);

        // Botões
        btnAtualizar = new JButton("Atualizar Venda");
        btnAtualizar.setBackground(new Color(230, 126, 34));
        btnAtualizar.setForeground(Color.white);
        btnAtualizar.setFocusPainted(false);
        btnAtualizar.setFont(new Font("Arial", Font.BOLD, 16));
        btnAtualizar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btnAtualizar.setEnabled(false); // Inicia desabilitado

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(149, 165, 166));
        btnCancelar.setForeground(Color.white);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 16));
        btnCancelar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        // Event listeners
        btnAtualizar.addActionListener(e -> atualizarVenda());
        btnCancelar.addActionListener(e -> dispose());

        // Panel para botões
        JPanel panelBotoes = new JPanel(new FlowLayout());
        panelBotoes.setBackground(new Color(245, 245, 245));
        panelBotoes.add(btnAtualizar);
        panelBotoes.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        add(panelBotoes, gbc);

        // Carregar vendas
        carregarVendas();

        setVisible(true);
    }

    private void carregarVendas() {
        try {
            List<Venda> vendas = vendaController.obterTodasVendas();

            comboVendas.removeAllItems();
            comboVendas.addItem(new VendaComboItem(null, "-- Selecione uma venda --"));

            for (Venda venda : vendas) {
                comboVendas.addItem(new VendaComboItem(venda,
                        String.format("ID: %d - %s (Qtd: %d) - %s",
                                venda.getId(),
                                venda.getProduto(),
                                venda.getQuantidade(),
                                venda.getData())));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar vendas: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarDadosVenda() {
        VendaComboItem item = (VendaComboItem) comboVendas.getSelectedItem();

        if (item != null && item.getVenda() != null) {
            vendaSelecionada = item.getVenda();

            txtProduto.setText(vendaSelecionada.getProduto());
            txtQuantidade.setText(String.valueOf(vendaSelecionada.getQuantidade()));
            txtValorUnitario.setText(String.valueOf(vendaSelecionada.getValorUnitario()));

            // Habilitar campos e botão
            txtProduto.setEnabled(true);
            txtQuantidade.setEnabled(true);
            txtValorUnitario.setEnabled(true);
            btnAtualizar.setEnabled(true); // Usar referência direta

        } else {
            vendaSelecionada = null;

            txtProduto.setText("");
            txtQuantidade.setText("");
            txtValorUnitario.setText("");

            // Desabilitar campos e botão
            txtProduto.setEnabled(false);
            txtQuantidade.setEnabled(false);
            txtValorUnitario.setEnabled(false);
            btnAtualizar.setEnabled(false); // Usar referência direta
        }
    }

    private void atualizarVenda() {
        if (vendaSelecionada == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma venda para atualizar!",
                    "Nenhuma Venda Selecionada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Validação dos campos
            String produto = txtProduto.getText().trim();
            if (produto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, informe o nome do produto!",
                        "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
                txtProduto.requestFocus();
                return;
            }

            String quantidadeStr = txtQuantidade.getText().trim();
            if (quantidadeStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, informe a quantidade!",
                        "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
                txtQuantidade.requestFocus();
                return;
            }

            String valorStr = txtValorUnitario.getText().trim();
            if (valorStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, informe o valor unitário!",
                        "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
                txtValorUnitario.requestFocus();
                return;
            }

            // Conversão e validação de tipos
            int quantidade = Integer.parseInt(quantidadeStr);
            if (quantidade <= 0) {
                JOptionPane.showMessageDialog(this, "A quantidade deve ser um número positivo!",
                        "Valor Inválido", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double valor = Double.parseDouble(valorStr);
            if (valor <= 0) {
                JOptionPane.showMessageDialog(this, "O valor unitário deve ser um número positivo!",
                        "Valor Inválido", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Confirmação da atualização
            int resposta = JOptionPane.showConfirmDialog(this,
                    "Confirma a atualização desta venda?\n\n" +
                            "Dados atuais:\n" +
                            "Produto: " + vendaSelecionada.getProduto() + " → " + produto + "\n" +
                            "Quantidade: " + vendaSelecionada.getQuantidade() + " → " + quantidade + "\n" +
                            "Valor Unit.: R$ " + String.format("%.2f", vendaSelecionada.getValorUnitario()) +
                            " → R$ " + String.format("%.2f", valor),
                    "Confirmar Atualização",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (resposta == JOptionPane.YES_OPTION) {
                try {
                    // Cria nova instância da venda com dados atualizados
                    Venda vendaAtualizada = new Venda(
                            vendaSelecionada.getId(),
                            produto,
                            quantidade,
                            valor,
                            vendaSelecionada.getData() // Mantém a data original
                    );

                    // Chama o controller para atualizar
                    vendaController.atualizarVenda(vendaAtualizada);

                    JOptionPane.showMessageDialog(this,
                            "✅ Venda atualizada com sucesso!\n\n" +
                                    "Dados atualizados:\n" +
                                    "• ID: " + vendaSelecionada.getId() + "\n" +
                                    "• Produto: " + produto + "\n" +
                                    "• Quantidade: " + quantidade + "\n" +
                                    "• Valor Unit.: R$ " + String.format("%.2f", valor) + "\n" +
                                    "• Total: R$ " + String.format("%.2f", quantidade * valor),
                            "Atualização Realizada", JOptionPane.INFORMATION_MESSAGE);

                    dispose();

                } catch (SQLException ex) {
                    String mensagemErro = "Erro ao atualizar venda no banco de dados:\n" + ex.getMessage();
                    if (ex.getMessage().contains("não encontrada")) {
                        mensagemErro = "A venda selecionada não existe mais no banco de dados.";
                    }

                    JOptionPane.showMessageDialog(this,
                            mensagemErro,
                            "Erro de Banco de Dados",
                            JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Erro inesperado ao atualizar venda:\n" + ex.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, verifique se os valores numéricos estão corretos!\n" +
                            "Quantidade deve ser um número inteiro.\n" +
                            "Valor deve ser um número decimal.",
                    "Formato Inválido", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao atualizar venda: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Classe auxiliar para o ComboBox
    private static class VendaComboItem {
        private Venda venda;
        private String texto;

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