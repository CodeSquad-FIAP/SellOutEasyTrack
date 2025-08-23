import com.formdev.flatlaf.FlatDarculaLaf;
import view.SimplifiedDashboardView;
import util.DBConnection;
import util.RGraphUtil;

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
            FlatDarculaLaf.setup();

            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("ProgressBar.arc", 8);
            UIManager.put("TextComponent.arc", 8);

            UIManager.put("Button.default.background", new Color(225, 80, 96));
            UIManager.put("Component.focusColor", new Color(142, 68, 173));
            UIManager.put("ProgressBar.foreground", new Color(46, 204, 113));

            UIManager.put("TitlePane.foreground", Color.WHITE);
            UIManager.put("TitlePane.inactiveForeground", Color.WHITE);

            configurarFontes();

            System.out.println("Interface moderna configurada com o tema FlatDarculaLaf");

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
                    "Roboto", "Poppins", "Segoe UI", "Liberation Sans", "Arial"
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
            UIManager.put("TextArea.font", baseFont);
            UIManager.put("Table.font", baseFont);

            System.out.println("Fonte configurada: " + baseFont.getName());

        } catch (Exception e) {
            System.err.println("Erro ao configurar fontes: " + e.getMessage());
        }
    }

    private static boolean isFontAvailable(String fontName) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] allFonts = ge.getAllFonts();
        for (Font font : allFonts) {
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
            if (RGraphUtil.isRDisponivel()) {
                System.out.println("R disponível para gráficos avançados");
            } else {
                System.out.println("R não disponível - gráficos básicos serão usados");
            }
        } catch (Exception e) {
            System.out.println("R não verificado: " + e.getMessage());
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
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, e.getMessage(), "Erro Crítico", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
}