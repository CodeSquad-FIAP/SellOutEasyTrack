package view;

import controller.VendaController;
import model.Venda;
import listener.VendaListener;
import util.ColorPalette;

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
        getContentPane().setBackground(new Color(64, 64, 64));
    }

    private void criarInterface() {
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(242, 48, 100));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        JLabel titulo = new JLabel("NOVA VENDA");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        headerPanel.add(titulo);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblProduto = new JLabel("Produto:");
        lblProduto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblProduto.setForeground(Color.WHITE);

        JLabel lblQuantidade = new JLabel("Quantidade:");
        lblQuantidade.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblQuantidade.setForeground(Color.WHITE);

        JLabel lblValor = new JLabel("Valor Unitário (R$):");
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblValor.setForeground(Color.WHITE);

        txtProduto = new JTextField(20);
        txtProduto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.FIAP_GRAY_MEDIUM),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        txtProduto.setBackground(new Color(38, 38, 38));
        txtProduto.setForeground(Color.WHITE);
        txtProduto.setCaretColor(Color.WHITE);

        txtQuantidade = new JTextField(20);
        txtQuantidade.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.FIAP_GRAY_MEDIUM),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        txtQuantidade.setBackground(new Color(38, 38, 38));
        txtQuantidade.setForeground(Color.WHITE);
        txtQuantidade.setCaretColor(Color.WHITE);

        txtValorUnitario = new JTextField(20);
        txtValorUnitario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.FIAP_GRAY_MEDIUM),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        txtValorUnitario.setBackground(new Color(38, 38, 38));
        txtValorUnitario.setForeground(Color.WHITE);
        txtValorUnitario.setCaretColor(Color.WHITE);

        gbc.gridx = 0; gbc.gridy = 0; mainPanel.add(lblProduto, gbc);
        gbc.gridx = 1; gbc.gridy = 0; mainPanel.add(txtProduto, gbc);
        gbc.gridx = 0; gbc.gridy = 1; mainPanel.add(lblQuantidade, gbc);
        gbc.gridx = 1; gbc.gridy = 1; mainPanel.add(txtQuantidade, gbc);
        gbc.gridx = 0; gbc.gridy = 2; mainPanel.add(lblValor, gbc);
        gbc.gridx = 1; gbc.gridy = 2; mainPanel.add(txtValorUnitario, gbc);

        Dimension buttonSize = new Dimension(160, 45);

        JButton btnSalvar = new JButton("Salvar Venda");
        btnSalvar.setBackground(new Color(242, 48, 100));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setPreferredSize(buttonSize);
        btnSalvar.setBorder(null);
        btnSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(140, 140, 140));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setPreferredSize(buttonSize);
        btnCancelar.setBorder(null);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSalvar.addActionListener(e -> salvarVenda());
        btnCancelar.addActionListener(e -> dispose());

        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBotoes.setOpaque(false);
        panelBotoes.add(btnSalvar);
        panelBotoes.add(btnCancelar);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 15, 15, 15);
        mainPanel.add(panelBotoes, gbc);

        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
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