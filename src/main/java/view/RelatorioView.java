package view;

import controller.VendaController;
import model.Venda;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class RelatorioView extends JDialog {

    private final VendaController vendaController = new VendaController();
    private JTextArea txtRelatorio;

    public RelatorioView(JFrame parent) {
        super(parent, "Relatório de Vendas", true);
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        txtRelatorio = new JTextArea();
        txtRelatorio.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtRelatorio.setEditable(false);

        JButton btnGerar = new JButton("Gerar Relatório");
        btnGerar.setBackground(new Color(52, 152, 219));
        btnGerar.setForeground(Color.WHITE);
        btnGerar.setFocusPainted(false);

        btnGerar.addActionListener(e -> gerarRelatorio());

        add(new JScrollPane(txtRelatorio), BorderLayout.CENTER);
        add(btnGerar, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void gerarRelatorio() {
        try {
            List<Venda> vendas = vendaController.obterTodasVendas();

            if (vendas.isEmpty()) {
                txtRelatorio.setText("Nenhuma venda registrada.");
                return;
            }

            int totalVendas = vendas.size();
            double somaTotal = vendas.stream().mapToDouble(v -> v.getQuantidade() * v.getValorUnitario()).sum();
            double ticketMedio = somaTotal / totalVendas;

            Map<String, Integer> produtoMaisVendido = vendaController.obterProdutoMaisVendido();

            StringBuilder sb = new StringBuilder();
            sb.append("======= Relatório SellOut EasyTrack =======\n\n");
            sb.append("Total de vendas registradas: ").append(totalVendas).append("\n");
            sb.append(String.format("Total geral em vendas: R$ %.2f\n", somaTotal));
            sb.append(String.format("Ticket Médio: R$ %.2f\n\n", ticketMedio));

            sb.append("Produto(s) mais vendido(s):\n");
            for (Map.Entry<String, Integer> entry : produtoMaisVendido.entrySet()) {
                sb.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" unidades\n");
            }

            txtRelatorio.setText(sb.toString());

        } catch (SQLException e) {
            txtRelatorio.setText("Erro ao gerar relatório: " + e.getMessage());
        }
    }
}