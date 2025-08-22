package view;

import controller.VendaController;
import model.Venda;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;

public class RegistroVendaView extends JDialog {

    private JTextField txtProduto;
    private JTextField txtQuantidade;
    private JTextField txtValorUnitario;
    private final VendaController vendaController = new VendaController();

    public RegistroVendaView(JFrame parent) {
        super(parent, "Registrar Venda", true);
        setSize(400, 350);
        setLocationRelativeTo(parent);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(236, 240, 241));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblProduto = new JLabel("Produto:");
        txtProduto = new JTextField();

        JLabel lblQuantidade = new JLabel("Quantidade:");
        txtQuantidade = new JTextField();

        JLabel lblValor = new JLabel("Valor Unitário:");
        txtValorUnitario = new JTextField();

        JButton btnSalvar = new JButton("Salvar Venda");
        btnSalvar.setBackground(new Color(46, 204, 113));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFocusPainted(false);
        btnSalvar.setFont(new Font("Arial", Font.BOLD, 16));

        btnSalvar.addActionListener(e -> salvarVenda());

        gbc.gridx = 0; gbc.gridy = 0;
        add(lblProduto, gbc);
        gbc.gridx = 1;
        add(txtProduto, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(lblQuantidade, gbc);
        gbc.gridx = 1;
        add(txtQuantidade, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(lblValor, gbc);
        gbc.gridx = 1;
        add(txtValorUnitario, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        add(btnSalvar, gbc);

        setVisible(true);
    }

    private void salvarVenda() {
        try {
            String produto = txtProduto.getText().trim();
            int quantidade = Integer.parseInt(txtQuantidade.getText().trim());
            double valor = Double.parseDouble(txtValorUnitario.getText().trim());

            Venda venda = new Venda(produto, quantidade, valor, Date.valueOf(LocalDate.now()));
            vendaController.salvarVenda(venda);

            JOptionPane.showMessageDialog(this, "Venda registrada com sucesso!");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }
}