import com.formdev.flatlaf.FlatLightLaf;
import view.DashboardView;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        configurarLookAndFeel();
        configurarPropriedadesRenderizacao();

        SwingUtilities.invokeLater(() -> {
            try {
                new DashboardView();
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

            UIManager.put("Button.background", new Color(52, 152, 219));
            UIManager.put("Button.foreground", Color.WHITE);

            try {
                Font font = new Font("Segoe UI", Font.PLAIN, 12);
                UIManager.put("defaultFont", font);

                UIManager.put("Label.font", font);
                UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 12));
                UIManager.put("TextField.font", font);
                UIManager.put("TextArea.font", font);

            } catch (Exception e) {
                System.out.println("Segoe UI não disponível, usando fonte padrão");
            }

            System.out.println("✅ FlatLaf configurado com sucesso");

        } catch (Exception e) {
            System.err.println("❌ Erro ao configurar FlatLaf: " + e.getMessage());

            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                System.out.println("⚠️ Usando Look and Feel padrão como fallback");
            } catch (Exception ex) {
                System.err.println("❌ Erro crítico no Look and Feel: " + ex.getMessage());
            }
        }
    }

    private static void configurarPropriedadesRenderizacao() {
        try {
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");

            System.setProperty("sun.java2d.d3d", "false");
            System.setProperty("sun.java2d.opengl", "true");

            System.setProperty("sun.java2d.uiScale", "1.0");

            System.out.println("✅ Propriedades de renderização configuradas");

        } catch (Exception e) {
            System.err.println("⚠️ Aviso: Algumas propriedades de renderização podem não estar disponíveis");
        }
    }

    private static void mostrarErroInicializacao(Exception e) {
        String mensagem = "Erro ao inicializar o SellOut EasyTrack:\n\n" +
                e.getMessage() + "\n\n" +
                "Verificações necessárias:\n" +
                "• MySQL está rodando?\n" +
                "• Credenciais do banco estão corretas?\n" +
                "• R está instalado (para gráficos)?\n" +
                "• Todas as dependências foram baixadas?";

        try {
            JOptionPane.showMessageDialog(null,
                    mensagem,
                    "SellOut EasyTrack - Erro de Inicialização",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception dialogError) {
            System.err.println("==========================================");
            System.err.println("ERRO DE INICIALIZAÇÃO - SELLOUT EASYTRACK");
            System.err.println("==========================================");
            System.err.println(mensagem);
            System.err.println("==========================================");
        }

        System.err.println("\nDetalhes técnicos do erro:");
        e.printStackTrace();

        System.exit(1);
    }
}