package util;

import java.awt.Color;

/**
 * Paleta de cores unificada FIAP + Asteria para toda a aplicação SellOut EasyTrack.
 *
 * Combinação das cores oficiais da FIAP com tons complementares
 * inspirados na marca Asteria (Soluções Digitais Sob Medida).
 *
 * @author SellOut EasyTrack Team
 * @version 2.0
 */
public final class ColorPalette {

    // ===============================================
    // CORES PRIMÁRIAS FIAP
    // ===============================================

    /** Rosa vibrante FIAP - Cor principal da identidade */
    public static final Color FIAP_PINK_VIBRANT = new Color(242, 48, 100); // #F23064

    /** Rosa escuro FIAP - Variação mais profunda */
    public static final Color FIAP_PINK_DARK = new Color(191, 59, 94); // #BF3B5E

    /** Cinza médio FIAP - Cor neutra para textos e fundos */
    public static final Color FIAP_GRAY_MEDIUM = new Color(140, 140, 140); // #8C8C8C

    /** Cinza escuro FIAP - Para textos principais */
    public static final Color FIAP_GRAY_DARK = new Color(64, 64, 64); // #404040

    /** Preto tecnológico FIAP - Para contrastes máximos */
    public static final Color FIAP_BLACK_TECH = new Color(38, 38, 38); // #262626

    // ===============================================
    // CORES ASTERIA (COMPLEMENTARES)
    // ===============================================

    /** Azul meia-noite Asteria - Profundidade e confiabilidade */
    public static final Color ASTERIA_MIDNIGHT_BLUE = new Color(44, 62, 80); // #2C3E50

    /** Ametista Asteria - Inovação e criatividade */
    public static final Color ASTERIA_AMETHYST = new Color(142, 68, 173); // #8E44AD

    /** Azul oceano Asteria - Estabilidade e tecnologia */
    public static final Color ASTERIA_OCEAN_BLUE = new Color(52, 152, 219); // #3498DB

    /** Prata Asteria - Modernidade e sofisticação */
    public static final Color ASTERIA_SILVER = new Color(189, 195, 199); // #BDC3C7

    // ===============================================
    // CORES FUNCIONAIS (SEMÁFORO)
    // ===============================================

    /** Verde esmeralda - Sucesso e aprovação */
    public static final Color SUCCESS_EMERALD = new Color(46, 204, 113); // #2ECC71

    /** Amarelo âmbar - Atenção e avisos */
    public static final Color WARNING_AMBER = new Color(243, 156, 18); // #F39C12

    /** Vermelho cardinal - Erros e perigos */
    public static final Color DANGER_CARDINAL = new Color(231, 76, 60); // #E74C3C

    /** Azul informativo - Informações e dicas */
    public static final Color INFO_AZURE = new Color(52, 152, 219); // #3498DB

    // ===============================================
    // CORES NEUTRAS (FUNDOS E TEXTO)
    // ===============================================

    /** Branco puro - Fundos principais */
    public static final Color PURE_WHITE = new Color(255, 255, 255); // #FFFFFF

    /** Cinza claro - Fundos secundários */
    public static final Color LIGHT_GRAY = new Color(236, 240, 241); // #ECF0F1

    /** Cinza pérola - Separadores e bordas */
    public static final Color PEARL_GRAY = new Color(220, 221, 225); // #DCDDE1

    /** Carvão suave - Textos principais */
    public static final Color SOFT_CHARCOAL = new Color(44, 62, 80); // #2C3E50

    // ===============================================
    // GRADIENTES PARA ELEMENTOS MODERNOS
    // ===============================================

    /** Gradiente principal: FIAP Pink → Asteria Amethyst */
    public static final Color[] PRIMARY_GRADIENT = {FIAP_PINK_VIBRANT, ASTERIA_AMETHYST};

    /** Gradiente secundário: Asteria Ocean → Asteria Midnight */
    public static final Color[] SECONDARY_GRADIENT = {ASTERIA_OCEAN_BLUE, ASTERIA_MIDNIGHT_BLUE};

    /** Gradiente de sucesso: Verde claro → Verde escuro */
    public static final Color[] SUCCESS_GRADIENT = {new Color(46, 204, 113), new Color(39, 174, 96)};

    // ===============================================
    // PALETAS ESPECÍFICAS POR CONTEXTO
    // ===============================================

    /**
     * Paleta para Dashboard - Foco em legibilidade e profissionalismo
     */
    public static class Dashboard {
        public static final Color BACKGROUND = LIGHT_GRAY;
        public static final Color CARD_BACKGROUND = PURE_WHITE;
        public static final Color PRIMARY_TEXT = SOFT_CHARCOAL;
        public static final Color SECONDARY_TEXT = FIAP_GRAY_MEDIUM;
        public static final Color ACCENT = FIAP_PINK_VIBRANT;
        public static final Color BORDER = PEARL_GRAY;
        public static final Color HEADER = ASTERIA_MIDNIGHT_BLUE;
    }

    /**
     * Paleta para Gráficos - Máximo contraste e diferenciação
     */
    public static class Charts {
        public static final Color[] DATA_SERIES = {
                FIAP_PINK_VIBRANT,     // Série 1
                ASTERIA_OCEAN_BLUE,    // Série 2
                ASTERIA_AMETHYST,      // Série 3
                SUCCESS_EMERALD,       // Série 4
                WARNING_AMBER,         // Série 5
                FIAP_PINK_DARK,        // Série 6
                ASTERIA_MIDNIGHT_BLUE, // Série 7
                FIAP_GRAY_DARK         // Série 8
        };

        public static final Color GRID_LINES = PEARL_GRAY;
        public static final Color LABELS = SOFT_CHARCOAL;
        public static final Color BACKGROUND = PURE_WHITE;
    }

    /**
     * Paleta para Botões - Estados e interações
     */
    public static class Buttons {
        // Botão Primário (Ações principais)
        public static final Color PRIMARY_NORMAL = FIAP_PINK_VIBRANT;
        public static final Color PRIMARY_HOVER = FIAP_PINK_DARK;
        public static final Color PRIMARY_PRESSED = new Color(171, 53, 84); // Mais escuro
        public static final Color PRIMARY_TEXT = PURE_WHITE;

        // Botão Secundário (Ações secundárias)
        public static final Color SECONDARY_NORMAL = ASTERIA_OCEAN_BLUE;
        public static final Color SECONDARY_HOVER = new Color(41, 128, 185); // Mais escuro
        public static final Color SECONDARY_PRESSED = new Color(40, 116, 166); // Ainda mais escuro
        public static final Color SECONDARY_TEXT = PURE_WHITE;

        // Botão Sucesso (Confirmações)
        public static final Color SUCCESS_NORMAL = SUCCESS_EMERALD;
        public static final Color SUCCESS_HOVER = new Color(39, 174, 96); // Mais escuro
        public static final Color SUCCESS_PRESSED = new Color(34, 153, 84); // Ainda mais escuro
        public static final Color SUCCESS_TEXT = PURE_WHITE;

        // Botão Perigo (Exclusões)
        public static final Color DANGER_NORMAL = DANGER_CARDINAL;
        public static final Color DANGER_HOVER = new Color(192, 57, 43); // Mais escuro
        public static final Color DANGER_PRESSED = new Color(169, 50, 38); // Ainda mais escuro
        public static final Color DANGER_TEXT = PURE_WHITE;

        // Botão Neutro (Cancelar, etc.)
        public static final Color NEUTRAL_NORMAL = FIAP_GRAY_MEDIUM;
        public static final Color NEUTRAL_HOVER = FIAP_GRAY_DARK;
        public static final Color NEUTRAL_PRESSED = new Color(52, 52, 52); // Mais escuro
        public static final Color NEUTRAL_TEXT = PURE_WHITE;
    }

    /**
     * Paleta para Estados e Feedbacks
     */
    public static class Status {
        // Insights/Analytics
        public static final Color INSIGHT_CRITICAL = DANGER_CARDINAL;
        public static final Color INSIGHT_WARNING = WARNING_AMBER;
        public static final Color INSIGHT_INFO = INFO_AZURE;
        public static final Color INSIGHT_SUCCESS = SUCCESS_EMERALD;
        public static final Color INSIGHT_OPPORTUNITY = ASTERIA_AMETHYST;

        // Bordas dos cards de insight
        public static final Color CRITICAL_BORDER = DANGER_CARDINAL;
        public static final Color WARNING_BORDER = WARNING_AMBER;
        public static final Color INFO_BORDER = INFO_AZURE;
        public static final Color SUCCESS_BORDER = SUCCESS_EMERALD;
        public static final Color OPPORTUNITY_BORDER = ASTERIA_AMETHYST;
    }

    // ===============================================
    // MÉTODOS UTILITÁRIOS
    // ===============================================

    /**
     * Retorna uma versão mais clara da cor (adiciona transparência)
     */
    public static Color lighter(Color color, float alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(),
                (int)(255 * alpha));
    }

    /**
     * Retorna uma versão mais escura da cor
     */
    public static Color darker(Color color, float factor) {
        return new Color((int)(color.getRed() * factor),
                (int)(color.getGreen() * factor),
                (int)(color.getBlue() * factor));
    }

    /**
     * Converte Color para hex string
     */
    public static String toHex(Color color) {
        return String.format("#%02X%02X%02X",
                color.getRed(),
                color.getGreen(),
                color.getBlue());
    }

    /**
     * Construtor privado para impedir a instanciação da classe.
     */
    private ColorPalette() {
        throw new UnsupportedOperationException("Classe utilitária não pode ser instanciada");
    }

    // ===============================================
    // PALETA PARA R (GRÁFICOS AVANÇADOS)
    // ===============================================

    /**
     * Retorna array de cores em formato hex para uso em scripts R
     */
    public static String[] getRColorPalette() {
        return new String[] {
                toHex(FIAP_PINK_VIBRANT),     // #F23064
                toHex(ASTERIA_OCEAN_BLUE),    // #3498DB
                toHex(ASTERIA_AMETHYST),      // #8E44AD
                toHex(SUCCESS_EMERALD),       // #2ECC71
                toHex(WARNING_AMBER),         // #F39C12
                toHex(FIAP_PINK_DARK),        // #BF3B5E
                toHex(ASTERIA_MIDNIGHT_BLUE), // #2C3E50
                toHex(FIAP_GRAY_DARK)         // #404040
        };
    }

    /**
     * Informações sobre a paleta para documentação
     */
    public static void printPaletteInfo() {
        System.out.println("=== PALETA DE CORES SELLOUT EASYTRACK ===");
        System.out.println("FIAP + Asteria - Soluções Digitais Sob Medida");
        System.out.println();
        System.out.println("CORES PRIMÁRIAS:");
        System.out.println("• FIAP Pink Vibrant: " + toHex(FIAP_PINK_VIBRANT));
        System.out.println("• FIAP Pink Dark: " + toHex(FIAP_PINK_DARK));
        System.out.println("• Asteria Midnight Blue: " + toHex(ASTERIA_MIDNIGHT_BLUE));
        System.out.println("• Asteria Amethyst: " + toHex(ASTERIA_AMETHYST));
        System.out.println();
        System.out.println("CORES FUNCIONAIS:");
        System.out.println("• Sucesso: " + toHex(SUCCESS_EMERALD));
        System.out.println("• Aviso: " + toHex(WARNING_AMBER));
        System.out.println("• Perigo: " + toHex(DANGER_CARDINAL));
        System.out.println("• Info: " + toHex(INFO_AZURE));
        System.out.println();
        System.out.println("CORES NEUTRAS:");
        System.out.println("• Fundo principal: " + toHex(PURE_WHITE));
        System.out.println("• Fundo secundário: " + toHex(LIGHT_GRAY));
        System.out.println("• Texto principal: " + toHex(SOFT_CHARCOAL));
        System.out.println("==========================================");
    }
}