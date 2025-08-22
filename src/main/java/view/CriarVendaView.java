package view;

import controller.VendaController;
import model.Venda;
import listener.VendaListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;

public class CriarVendaView extends JDialog {

    private JTextField txtProduto;
    private JTextField txtQuantidade;
    private JTextField txtValorUnitario;
    private final VendaController vendaController = new VendaController();
    private final VendaListener vendaListener;

    public CriarVendaView(JFrame parent, VendaListener listener) {
        super(parent, "Nova Venda", true);
        this.vendaListener = listener;
        configurarJanela();
        criarInterface();
        setVisible(true);
    }

    private void configurarJanela() {
        setSize(550, 450);
        setLocationRelativeTo(getParent());
        setResizable(false);
        getContentPane().setBackground(new Color(245, 245, 245));
    }

    private void criarInterface() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("NOVA VENDA", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(Color.BLACK);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(titulo, gbc);

        gbc.gridwidth = 1;

        JLabel lblProduto = new JLabel("Produto:");
        lblProduto.setFont(new Font("Arial", Font.BOLD, 14));
        txtProduto = new JTextField(20);
        txtProduto.setFont(new Font("Arial", Font.PLAIN, 14));
        txtProduto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        JLabel lblQuantidade = new JLabel("Quantidade:");
        lblQuantidade.setFont(new Font("Arial", Font.BOLD, 14));
        txtQuantidade = new JTextField(20);
        txtQuantidade.setFont(new Font("Arial", Font.PLAIN, 14));
        txtQuantidade.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        JLabel lblValor = new JLabel("Valor Unitário (R$):");
        lblValor.setFont(new Font("Arial", Font.BOLD, 14));
        txtValorUnitario = new JTextField(20);
        txtValorUnitario.setFont(new Font("Arial", Font.PLAIN, 14));
        txtValorUnitario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        gbc.gridx = 0; gbc.gridy = 1; add(lblProduto, gbc);
        gbc.gridx = 1; add(txtProduto, gbc);
        gbc.gridx = 0; gbc.gridy = 2; add(lblQuantidade, gbc);
        gbc.gridx = 1; add(txtQuantidade, gbc);
        gbc.gridx = 0; gbc.gridy = 3; add(lblValor, gbc);
        gbc.gridx = 1; add(txtValorUnitario, gbc);

        JButton btnSalvar = new JButton("Salvar Venda");
        btnSalvar.setBackground(new Color(46, 204, 113));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFocusPainted(false);
        btnSalvar.setFont(new Font("Arial", Font.BOLD, 16));
        btnSalvar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(Color.GRAY);
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 16));
        btnCancelar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        btnSalvar.addActionListener(e -> salvarVenda());
        btnCancelar.addActionListener(e -> dispose());

        JPanel panelBotoes = new JPanel(new FlowLayout());
        panelBotoes.setBackground(new Color(245, 245, 245));
        panelBotoes.add(btnSalvar);
        panelBotoes.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        add(panelBotoes, gbc);
    }

    private void salvarVenda() {
        try {
            String produto = txtProduto.getText().trim();
            if (produto.isEmpty()) {
                mostrarErro("Por favor, digite o nome do produto!");
                txtProduto.requestFocus();
                return;
            }

            String quantidadeStr = txtQuantidade.getText().trim();
            if (quantidadeStr.isEmpty()) {
                mostrarErro("Por favor, informe a quantidade!");
                txtQuantidade.requestFocus();
                return;
            }

            String valorStr = txtValorUnitario.getText().trim().replace(",", ".");
            if (valorStr.isEmpty()) {
                mostrarErro("Por favor, informe o valor unitário!");
                txtValorUnitario.requestFocus();
                return;
            }

            int quantidade = Integer.parseInt(quantidadeStr);
            if (quantidade <= 0) {
                mostrarErro("A quantidade deve ser um número positivo!");
                txtQuantidade.requestFocus();
                return;
            }

            double valor = Double.parseDouble(valorStr);
            if (valor <= 0) {
                mostrarErro("O valor unitário deve ser um número positivo!");
                txtValorUnitario.requestFocus();
                return;
            }

            Venda venda = new Venda(produto, quantidade, valor, Date.valueOf(LocalDate.now()));
            vendaController.salvarVenda(venda);

            if (vendaListener != null) {
                vendaListener.onVendasChanged();
            }

            mostrarSucesso("Venda registrada com sucesso!");
            dispose();

        } catch (NumberFormatException ex) {
            mostrarErro("Valores numéricos inválidos.");
        } catch (Exception ex) {
            mostrarErro("Erro ao salvar venda: " + ex.getMessage());
        }
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarSucesso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }
}