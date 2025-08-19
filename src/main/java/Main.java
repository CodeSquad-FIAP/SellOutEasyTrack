import com.formdev.flatlaf.FlatLightLaf;
import view.DashboardView;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Configurar look and feel moderno
        configurarLookAndFeel();

        // Configurar propriedades do sistema para melhor renderização
        configurarPropriedadesRenderizacao();

        // Iniciar aplicação
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
            // Usar FlatLaf Light (moderno e confiável)
            FlatLightLaf.setup();

            // Configurações de aparência moderna
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("ProgressBar.arc", 8);
            UIManager.put("TextComponent.arc", 8);

            // Configurar cores personalizadas
            UIManager.put("Button.background", new Color(52, 152, 219));
            UIManager.put("Button.foreground", Color.WHITE);

            // Configurar fonte padrão
            try {
                Font font = new Font("Segoe UI", Font.PLAIN, 12);
                UIManager.put("defaultFont", font);

                // Aplicar fonte em todos os componentes
                UIManager.put("Label.font", font);
                UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 12));
                UIManager.put("TextField.font", font);
                UIManager.put("TextArea.font", font);

            } catch (Exception e) {
                // Se Segoe UI não estiver disponível, usar fonte padrão
                System.out.println("Segoe UI não disponível, usando fonte padrão");
            }

            System.out.println("✅ FlatLaf configurado com sucesso");

        } catch (Exception e) {
            System.err.println("❌ Erro ao configurar FlatLaf: " + e.getMessage());

            // Fallback para look and feel padrão do Swing
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
            // Ativar anti-aliasing para texto (melhora legibilidade)
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");

            // Melhorar renderização gráfica
            System.setProperty("sun.java2d.d3d", "false");
            System.setProperty("sun.java2d.opengl", "true");

            // Configurar para telas de alta resolução
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

        // Tentar mostrar dialog de erro
        try {
            JOptionPane.showMessageDialog(null,
                    mensagem,
                    "SellOut EasyTrack - Erro de Inicialização",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception dialogError) {
            // Se não conseguir mostrar dialog, imprimir no console
            System.err.println("==========================================");
            System.err.println("ERRO DE INICIALIZAÇÃO - SELLOUT EASYTRACK");
            System.err.println("==========================================");
            System.err.println(mensagem);
            System.err.println("==========================================");
        }

        // Imprimir stacktrace detalhado
        System.err.println("\nDetalhes técnicos do erro:");
        e.printStackTrace();

        // Sair da aplicação
        System.exit(1);
    }
}