import com.formdev.flatlaf.FlatLightLaf;
import view.SimplifiedDashboardView;
import util.DBConnection;

import javax.swing.*;
import java.awt.*;

public class Main {

    private static final String SYSTEM_NAME = "SellOut EasyTrack";
    private static final String VERSION = "v2.0 - Sistema Completo";

    public static void main(String[] args) {
        System.out.println("INICIANDO " + SYSTEM_NAME + " " + VERSION);
        System.out.println("FOCO: BI Inteligente + Múltiplas Fontes + Analytics");
        System.out.println("TODAS as funcionalidades SEMPRE ativas!");
        System.out.println("===============================================\n");

        configurarLookAndFeel();
        configurarPropriedadesRenderizacao();

        if (!verificarDependencias()) {
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> {
            try {
                new SimplifiedDashboardView();
                System.out.println("Aplicação iniciada com sucesso!");
                System.out.println("Todas as funcionalidades estão disponíveis!");
            } catch (Exception e) {
                mostrarErroInicializacao(e);
            }
        });
    }

    private static void configurarLookAndFeel() {
        try {
            FlatLightLaf.setup();

            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("ProgressBar.arc", 8);
            UIManager.put("TextComponent.arc", 8);

            Color primaryColor = new Color(41, 128, 185);
            Color accentColor = new Color(46, 204, 113);

            UIManager.put("Button.default.background", primaryColor);
            UIManager.put("Button.default.foreground", Color.WHITE);
            UIManager.put("Component.focusColor", primaryColor);
            UIManager.put("ProgressBar.foreground", accentColor);

            configurarFontes();

            System.out.println("Interface moderna configurada");

        } catch (Exception e) {
            System.err.println("Erro ao configurar interface: " + e.getMessage());
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                System.out.println("Usando interface padrão do sistema");
            } catch (Exception ex) {
                System.err.println("Erro crítico na interface: " + ex.getMessage());
            }
        }
    }

    private static void configurarFontes() {
        try {
            String[] fontCandidates = {
                    "Segoe UI", "SF Pro Display", "Ubuntu", "Liberation Sans", "Arial"
            };

            Font baseFont = null;
            for (String fontName : fontCandidates) {
                if (isFontAvailable(fontName)) {
                    baseFont = new Font(fontName, Font.PLAIN, 12);
                    break;
                }
            }

            if (baseFont == null) {
                baseFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
            }

            UIManager.put("defaultFont", baseFont);
            UIManager.put("Label.font", baseFont);
            UIManager.put("Button.font", new Font(baseFont.getName(), Font.BOLD, 12));
            UIManager.put("TextField.font", baseFont);

            System.out.println("Fonte configurada: " + baseFont.getName());

        } catch (Exception e) {
            System.err.println("Erro ao configurar fontes: " + e.getMessage());
        }
    }

    private static boolean isFontAvailable(String fontName) {
        Font[] availableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        for (Font font : availableFonts) {
            if (font.getName().equals(fontName)) {
                return true;
            }
        }
        return false;
    }

    private static void configurarPropriedadesRenderizacao() {
        try {
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
            System.setProperty("sun.java2d.opengl", "true");
            System.setProperty("sun.java2d.d3d", "false");

            System.out.println("Renderização otimizada");

        } catch (Exception e) {
            System.err.println("Algumas otimizações podem não estar disponíveis");
        }
    }

    private static boolean verificarDependencias() {
        System.out.println("Verificando dependências...");

        boolean todasOk = true;

        try {
            String javaVersion = System.getProperty("java.version");
            System.out.println("Java: " + javaVersion);

            String[] versionParts = javaVersion.split("\\.");
            int majorVersion = Integer.parseInt(versionParts[0]);
            if (majorVersion < 11) {
                System.err.println("Java 11+ necessário. Atual: " + javaVersion);
                todasOk = false;
            } else {
                System.out.println("Java compatível");
            }
        } catch (Exception e) {
            System.err.println("Erro ao verificar Java: " + e.getMessage());
            todasOk = false;
        }

        try {
            if (DBConnection.testarConexao()) {
                System.out.println("MySQL conectado");
            } else {
                System.err.println("Falha na conexão MySQL");
                todasOk = false;
            }
        } catch (Exception e) {
            System.err.println("Erro MySQL: " + e.getMessage());
            todasOk = false;
        }

        try {
            if (util.RGraphUtil.isRDisponivel()) {
                System.out.println("R disponível para gráficos avançados");
            } else {
                System.out.println("R não disponível - gráficos básicos serão usados");
            }
        } catch (Exception e) {
            System.out.println("R não verificado: " + e.getMessage());
        }

        try {
            long espacoMB = new java.io.File(".").getFreeSpace() / (1024 * 1024);
            System.out.println("Espaço livre: " + espacoMB + " MB");

            if (espacoMB < 50) {
                System.err.println("Pouco espaço em disco");
            } else {
                System.out.println("Espaço suficiente");
            }
        } catch (Exception e) {
            System.out.println("Não foi possível verificar espaço");
        }

        try {
            java.io.File testFile = new java.io.File("test_permission.tmp");
            if (testFile.createNewFile()) {
                testFile.delete();
                System.out.println("Permissões OK");
            } else {
                System.err.println("Sem permissões de escrita");
                todasOk = false;
            }
        } catch (Exception e) {
            System.err.println("Erro nas permissões: " + e.getMessage());
            todasOk = false;
        }

        if (todasOk) {
            System.out.println("Todas as dependências verificadas!");
        } else {
            System.err.println("Algumas dependências falharam");

            int resposta = JOptionPane.showConfirmDialog(
                    null,
                    "Algumas dependências não foram atendidas.\n" +
                            "O sistema pode não funcionar corretamente.\n\n" +
                            "Deseja continuar mesmo assim?",
                    "Dependências",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            return resposta == JOptionPane.YES_OPTION;
        }

        return true;
    }

    private static void mostrarErroInicializacao(Exception e) {
        System.err.println("ERRO CRÍTICO NA INICIALIZAÇÃO");
        System.err.println("==========================================");

        String mensagemErro = "Erro ao inicializar o " + SYSTEM_NAME + ":\n\n" +
                e.getClass().getSimpleName() + ": " + e.getMessage() + "\n\n" +
                "Verificações:\n" +
                "MySQL está executando?\n" +
                "Credenciais do banco corretas?\n" +
                "Java 11+ instalado?\n" +
                "Permissões de escrita?\n\n" +
                "Para suporte, envie os logs do console.";

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            JTextArea textArea = new JTextArea(mensagemErro);
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            textArea.setBackground(new Color(245, 245, 245));
            textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(scrollPane, BorderLayout.CENTER);

            JButton btnCopy = new JButton("Copiar Log");
            btnCopy.addActionListener(event -> {
                try {
                    java.awt.datatransfer.StringSelection selection =
                            new java.awt.datatransfer.StringSelection(mensagemErro + "\n\nStackTrace:\n" +
                                    java.util.Arrays.toString(e.getStackTrace()));
                    java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                            .setContents(selection, null);
                    JOptionPane.showMessageDialog(null, "Log copiado!");
                } catch (Exception ex) {
                    System.err.println("Erro ao copiar: " + ex.getMessage());
                }
            });

            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.add(btnCopy);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            JOptionPane.showMessageDialog(null, panel,
                    SYSTEM_NAME + " - Erro de Inicialização",
                    JOptionPane.ERROR_MESSAGE);

        } catch (Exception dialogError) {
            System.err.println("==========================================");
            System.err.println("ERRO - " + SYSTEM_NAME.toUpperCase());
            System.err.println("==========================================");
            System.err.println(mensagemErro);
            System.err.println("==========================================");
        }

        System.err.println("\nStackTrace:");
        e.printStackTrace();
        System.exit(1);
    }
}