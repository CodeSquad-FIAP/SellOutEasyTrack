package view;

import controller.VendaController;
import model.Venda;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.time.LocalDate;

public class CriarVendaView extends JDialog {

    private JTextField txtProduto;
    private JTextField txtQuantidade;
    private JTextField txtValorUnitario;
    private VendaController vendaController = new VendaController();

    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color DARK_COLOR = new Color(44, 62, 80);
    private static final Color LIGHT_GRAY = new Color(236, 240, 241);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(52, 73, 94);

    public CriarVendaView(JFrame parent) {
        super(parent, "Nova Venda", true);
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
        titulo.setForeground(PRIMARY_COLOR);
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

        gbc.gridx = 0; gbc.gridy = 1;
        add(lblProduto, gbc);
        gbc.gridx = 1;
        add(txtProduto, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(lblQuantidade, gbc);
        gbc.gridx = 1;
        add(txtQuantidade, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        add(lblValor, gbc);
        gbc.gridx = 1;
        add(txtValorUnitario, gbc);

        JButton btnSalvar = new JButton("Salvar Venda");
        btnSalvar.setBackground(ACCENT_COLOR);
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFocusPainted(false);
        btnSalvar.setFont(new Font("Arial", Font.BOLD, 16));
        btnSalvar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(149, 165, 166));
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

            String valorStr = txtValorUnitario.getText().trim();
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

            double valor = Double.parseDouble(valorStr.replace(",", "."));
            if (valor <= 0) {
                mostrarErro("O valor unitário deve ser um número positivo!");
                txtValorUnitario.requestFocus();
                return;
            }

            Venda venda = new Venda(produto, quantidade, valor, Date.valueOf(LocalDate.now()));
            vendaController.salvarVenda(venda);

            mostrarSucesso(
                    "Venda registrada com sucesso!\n\n" +
                            "Produto: " + produto + "\n" +
                            "Quantidade: " + quantidade + "\n" +
                            "Valor Unitário: R$ " + String.format("%.2f", valor) + "\n" +
                            "Total: R$ " + String.format("%.2f", quantidade * valor)
            );

            txtProduto.setText("");
            txtQuantidade.setText("");
            txtValorUnitario.setText("");
            txtProduto.requestFocus();

        } catch (NumberFormatException ex) {
            mostrarErro("Por favor, verifique se os valores numéricos estão corretos!\n" +
                    "Quantidade deve ser um número inteiro.\n" +
                    "Valor deve ser um número decimal (use ponto ou vírgula).");
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