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

    // Cores do tema
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
        setSize(600, 400);
        setLocationRelativeTo(getParent());
        setResizable(false);
        getContentPane().setBackground(LIGHT_GRAY);
    }

    private void criarInterface() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = criarHeader();
        add(headerPanel, BorderLayout.NORTH);

        // Formulário
        JPanel formPanel = criarFormulario();
        add(formPanel, BorderLayout.CENTER);

        // Botões
        JPanel buttonPanel = criarBotoes();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel criarHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel titulo = new JLabel("NOVA VENDA");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(WHITE);

        JLabel subtitulo = new JLabel("Registrar uma nova venda no sistema");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(new Color(189, 195, 199));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(titulo, BorderLayout.NORTH);
        textPanel.add(subtitulo, BorderLayout.SOUTH);

        header.add(textPanel, BorderLayout.WEST);

        return header;
    }

    private JPanel criarFormulario() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(WHITE);
        form.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // Campo Produto (TextField simples)
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        form.add(criarLabel("Produto:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtProduto = criarTextField();
        txtProduto.setToolTipText("Digite o nome do produto");
        form.add(txtProduto, gbc);

        // Campo Quantidade
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        form.add(criarLabel("Quantidade:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtQuantidade = criarTextField();
        txtQuantidade.setToolTipText("Digite a quantidade vendida");
        form.add(txtQuantidade, gbc);

        // Campo Valor Unitário
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        form.add(criarLabel("Valor Unitário (R$):"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtValorUnitario = criarTextField();
        txtValorUnitario.setToolTipText("Digite o preço por unidade (ex: 150.50)");
        form.add(txtValorUnitario, gbc);

        return form;
    }

    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(DARK_COLOR);
        label.setPreferredSize(new Dimension(180, 35));
        label.setHorizontalAlignment(SwingConstants.LEFT);
        return label;
    }

    private JComboBox<String> criarComboBoxEditavel(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);

        // TORNAR EDITÁVEL - isso permite digitar livremente
        combo.setEditable(true);

        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        combo.setPreferredSize(new Dimension(300, 35));
        combo.setBackground(WHITE);

        // Configurar editor do ComboBox para parecer com TextField
        JTextField editor = (JTextField) combo.getEditor().getEditorComponent();
        editor.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        editor.setBorder(null);
        editor.setBackground(WHITE);

        // Placeholder text
        editor.setToolTipText("Digite o nome do produto ou selecione uma sugestão");

        // Efeito focus no ComboBox
        combo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                combo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                        new EmptyBorder(7, 11, 7, 11)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                combo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                        new EmptyBorder(8, 12, 8, 12)
                ));
            }
        });

        // Efeito focus no editor interno também
        editor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                combo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                        new EmptyBorder(7, 11, 7, 11)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                combo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                        new EmptyBorder(8, 12, 8, 12)
                ));
            }
        });

        return combo;
    }

    private JPanel criarCampoForm(String titulo, String descricao) {
        JPanel campo = new JPanel(new BorderLayout());
        campo.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(DARK_COLOR);

        JLabel lblDesc = new JLabel(descricao);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(TEXT_COLOR);

        campo.add(lblTitulo, BorderLayout.NORTH);
        campo.add(lblDesc, BorderLayout.SOUTH);

        return campo;
    }

    private JTextField criarTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        field.setPreferredSize(new Dimension(300, 35));

        // Efeito focus
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                        new EmptyBorder(7, 11, 7, 11)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                        new EmptyBorder(8, 12, 8, 12)
                ));
            }
        });

        return field;
    }

    private JPanel criarBotoes() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(LIGHT_GRAY);
        buttonPanel.setBorder(new EmptyBorder(20, 40, 25, 40));

        JButton btnSalvar = criarBotaoModerno("SALVAR VENDA", ACCENT_COLOR);
        btnSalvar.addActionListener(e -> salvarVenda());

        JButton btnCancelar = criarBotaoModerno("CANCELAR", DANGER_COLOR);
        btnCancelar.addActionListener(e -> dispose());

        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);

        return buttonPanel;
    }

    private JButton criarBotaoModerno(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(WHITE);
        btn.setBackground(cor);
        btn.setBorder(new EmptyBorder(12, 25, 12, 25));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efeito hover
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(cor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(cor);
            }
        });

        return btn;
    }

    private void salvarVenda() {
        try {
            // Validações - usar TextField simples
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

            // Conversões
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

            // Salvar venda
            Venda venda = new Venda(produto, quantidade, valor, Date.valueOf(LocalDate.now()));
            vendaController.salvarVenda(venda);

            mostrarSucesso(
                    "Venda registrada com sucesso!\n\n" +
                            "Produto: " + produto + "\n" +
                            "Quantidade: " + quantidade + "\n" +
                            "Valor Unitário: R$ " + String.format("%.2f", valor) + "\n" +
                            "Total: R$ " + String.format("%.2f", quantidade * valor)
            );

            // Limpar campos após salvar
            txtProduto.setText("");
            txtQuantidade.setText("");
            txtValorUnitario.setText("");

            // Focar no campo produto para próxima venda
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