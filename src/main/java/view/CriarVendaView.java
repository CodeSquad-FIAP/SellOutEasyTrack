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
        getContentPane().setBackground(ColorPalette.Dashboard.BACKGROUND);
    }

    private void criarInterface() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel titulo = new JLabel("NOVA VENDA", JLabel.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(ColorPalette.Dashboard.PRIMARY_TEXT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(titulo, gbc);

        gbc.gridwidth = 1;

        // Campo Produto
        JLabel lblProduto = new JLabel("Produto:");
        lblProduto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblProduto.setForeground(ColorPalette.Dashboard.PRIMARY_TEXT);

        txtProduto = new JTextField(20);
        txtProduto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtProduto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.Dashboard.BORDER),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        txtProduto.setBackground(ColorPalette.PURE_WHITE);
        txtProduto.setForeground(ColorPalette.Dashboard.PRIMARY_TEXT);

        // Campo Quantidade
        JLabel lblQuantidade = new JLabel("Quantidade:");
        lblQuantidade.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblQuantidade.setForeground(ColorPalette.Dashboard.PRIMARY_TEXT);

        txtQuantidade = new JTextField(20);
        txtQuantidade.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtQuantidade.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.Dashboard.BORDER),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        txtQuantidade.setBackground(ColorPalette.PURE_WHITE);
        txtQuantidade.setForeground(ColorPalette.Dashboard.PRIMARY_TEXT);

        // Campo Valor
        JLabel lblValor = new JLabel("Valor Unitário (R$):");
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblValor.setForeground(ColorPalette.Dashboard.PRIMARY_TEXT);

        txtValorUnitario = new JTextField(20);
        txtValorUnitario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtValorUnitario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.Dashboard.BORDER),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        txtValorUnitario.setBackground(ColorPalette.PURE_WHITE);
        txtValorUnitario.setForeground(ColorPalette.Dashboard.PRIMARY_TEXT);

        // Posicionamento dos campos
        gbc.gridx = 0; gbc.gridy = 1; add(lblProduto, gbc);
        gbc.gridx = 1; add(txtProduto, gbc);
        gbc.gridx = 0; gbc.gridy = 2; add(lblQuantidade, gbc);
        gbc.gridx = 1; add(txtQuantidade, gbc);
        gbc.gridx = 0; gbc.gridy = 3; add(lblValor, gbc);
        gbc.gridx = 1; add(txtValorUnitario, gbc);

        // Botões
        JButton btnSalvar = new JButton("Salvar Venda");
        btnSalvar.setBackground(ColorPalette.Buttons.SUCCESS_NORMAL);
        btnSalvar.setForeground(ColorPalette.Buttons.SUCCESS_TEXT);
        btnSalvar.setFocusPainted(false);
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSalvar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btnSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(ColorPalette.Buttons.NEUTRAL_NORMAL);
        btnCancelar.setForeground(ColorPalette.Buttons.NEUTRAL_TEXT);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnCancelar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efeitos hover para botões
        adicionarEfeitoHover(btnSalvar, ColorPalette.Buttons.SUCCESS_NORMAL, ColorPalette.Buttons.SUCCESS_HOVER);
        adicionarEfeitoHover(btnCancelar, ColorPalette.Buttons.NEUTRAL_NORMAL, ColorPalette.Buttons.NEUTRAL_HOVER);

        btnSalvar.addActionListener(e -> salvarVenda());
        btnCancelar.addActionListener(e -> dispose());

        JPanel panelBotoes = new JPanel(new FlowLayout());
        panelBotoes.setBackground(ColorPalette.Dashboard.BACKGROUND);
        panelBotoes.add(btnSalvar);
        panelBotoes.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        add(panelBotoes, gbc);
    }

    private void adicionarEfeitoHover(JButton botao, Color corNormal, Color corHover) {
        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                botao.setBackground(corHover);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                botao.setBackground(corNormal);
            }
        });
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
        // Customizar o JOptionPane com as cores da paleta
        UIManager.put("OptionPane.background", ColorPalette.PURE_WHITE);
        UIManager.put("Panel.background", ColorPalette.PURE_WHITE);
        UIManager.put("OptionPane.messageForeground", ColorPalette.DANGER_CARDINAL);

        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);

        // Restaurar cores padrão
        UIManager.put("OptionPane.background", null);
        UIManager.put("Panel.background", null);
        UIManager.put("OptionPane.messageForeground", null);
    }

    private void mostrarSucesso(String mensagem) {
        // Customizar o JOptionPane com as cores da paleta
        UIManager.put("OptionPane.background", ColorPalette.PURE_WHITE);
        UIManager.put("Panel.background", ColorPalette.PURE_WHITE);
        UIManager.put("OptionPane.messageForeground", ColorPalette.SUCCESS_EMERALD);

        JOptionPane.showMessageDialog(this, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);

        // Restaurar cores padrão
        UIManager.put("OptionPane.background", null);
        UIManager.put("Panel.background", null);
        UIManager.put("OptionPane.messageForeground", null);
    }
}