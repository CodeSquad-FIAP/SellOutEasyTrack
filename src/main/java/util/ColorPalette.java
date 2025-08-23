package util;

import java.awt.Color;

public final class ColorPalette {

    public static final Color FIAP_PINK_VIBRANT = new Color(242, 48, 100);
    public static final Color FIAP_PINK_DARK = new Color(191, 59, 94);
    public static final Color FIAP_GRAY_MEDIUM = new Color(140, 140, 140);
    public static final Color FIAP_GRAY_DARK = new Color(64, 64, 64);
    public static final Color FIAP_BLACK_TECH = new Color(38, 38, 38);
    public static final Color ASTERIA_MIDNIGHT_BLUE = new Color(44, 62, 80);
    public static final Color ASTERIA_AMETHYST = new Color(142, 68, 173);
    public static final Color ASTERIA_OCEAN_BLUE = new Color(52, 152, 219);
    public static final Color ASTERIA_SILVER = new Color(189, 195, 199);
    public static final Color SUCCESS_EMERALD = new Color(46, 204, 113);
    public static final Color WARNING_AMBER = new Color(243, 156, 18);
    public static final Color DANGER_CARDINAL = new Color(231, 76, 60);
    public static final Color INFO_AZURE = new Color(52, 152, 219);
    public static final Color PURE_WHITE = new Color(255, 255, 255);
    public static final Color LIGHT_GRAY = new Color(236, 240, 241);
    public static final Color PEARL_GRAY = new Color(220, 221, 225);
    public static final Color SOFT_CHARCOAL = new Color(44, 62, 80);
    public static final Color[] PRIMARY_GRADIENT = {FIAP_PINK_VIBRANT, ASTERIA_AMETHYST};
    public static final Color[] SECONDARY_GRADIENT = {ASTERIA_OCEAN_BLUE, ASTERIA_MIDNIGHT_BLUE};
    public static final Color[] SUCCESS_GRADIENT = {new Color(46, 204, 113), new Color(39, 174, 96)};

    public static class Dashboard {
        public static final Color BACKGROUND = LIGHT_GRAY;
        public static final Color CARD_BACKGROUND = PURE_WHITE;
        public static final Color PRIMARY_TEXT = SOFT_CHARCOAL;
        public static final Color SECONDARY_TEXT = FIAP_GRAY_MEDIUM;
        public static final Color ACCENT = FIAP_PINK_VIBRANT;
        public static final Color BORDER = PEARL_GRAY;
        public static final Color HEADER = ASTERIA_MIDNIGHT_BLUE;
    }

    public static class Charts {
        public static final Color[] DATA_SERIES = {
                FIAP_PINK_VIBRANT,
                ASTERIA_OCEAN_BLUE,
                ASTERIA_AMETHYST,
                SUCCESS_EMERALD,
                WARNING_AMBER,
                FIAP_PINK_DARK,
                ASTERIA_MIDNIGHT_BLUE,
                FIAP_GRAY_DARK
        };
        public static final Color GRID_LINES = PEARL_GRAY;
        public static final Color LABELS = SOFT_CHARCOAL;
        public static final Color BACKGROUND = PURE_WHITE;
    }

    public static class Buttons {
        public static final Color PRIMARY_NORMAL = FIAP_PINK_VIBRANT;
        public static final Color PRIMARY_HOVER = FIAP_PINK_DARK;
        public static final Color PRIMARY_PRESSED = new Color(171, 53, 84);
        public static final Color PRIMARY_TEXT = PURE_WHITE;
        public static final Color SECONDARY_NORMAL = ASTERIA_OCEAN_BLUE;
        public static final Color SECONDARY_HOVER = new Color(41, 128, 185);
        public static final Color SECONDARY_PRESSED = new Color(40, 116, 166);
        public static final Color SECONDARY_TEXT = PURE_WHITE;
        public static final Color SUCCESS_NORMAL = SUCCESS_EMERALD;
        public static final Color SUCCESS_HOVER = new Color(39, 174, 96);
        public static final Color SUCCESS_PRESSED = new Color(34, 153, 84);
        public static final Color SUCCESS_TEXT = PURE_WHITE;
        public static final Color DANGER_NORMAL = DANGER_CARDINAL;
        public static final Color DANGER_HOVER = new Color(192, 57, 43);
        public static final Color DANGER_PRESSED = new Color(169, 50, 38);
        public static final Color DANGER_TEXT = PURE_WHITE;
        public static final Color NEUTRAL_NORMAL = FIAP_GRAY_MEDIUM;
        public static final Color NEUTRAL_HOVER = FIAP_GRAY_DARK;
        public static final Color NEUTRAL_PRESSED = new Color(52, 52, 52);
        public static final Color NEUTRAL_TEXT = PURE_WHITE;
    }

    public static class Status {
        public static final Color INSIGHT_CRITICAL = DANGER_CARDINAL;
        public static final Color INSIGHT_WARNING = WARNING_AMBER;
        public static final Color INSIGHT_INFO = INFO_AZURE;
        public static final Color INSIGHT_SUCCESS = SUCCESS_EMERALD;
        public static final Color INSIGHT_OPPORTUNITY = ASTERIA_AMETHYST;
        public static final Color CRITICAL_BORDER = DANGER_CARDINAL;
        public static final Color WARNING_BORDER = WARNING_AMBER;
        public static final Color INFO_BORDER = INFO_AZURE;
        public static final Color SUCCESS_BORDER = SUCCESS_EMERALD;
        public static final Color OPPORTUNITY_BORDER = ASTERIA_AMETHYST;
    }

    public static Color lighter(Color color, float alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(),
                (int)(255 * alpha));
    }

    public static Color darker(Color color, float factor) {
        return new Color((int)(color.getRed() * factor),
                (int)(color.getGreen() * factor),
                (int)(color.getBlue() * factor));
    }

    public static String toHex(Color color) {
        return String.format("#%02X%02X%02X",
                color.getRed(),
                color.getGreen(),
                color.getBlue());
    }

    private ColorPalette() {
        throw new UnsupportedOperationException("Classe utilitária não pode ser instanciada");
    }

    public static String[] getRColorPalette() {
        return new String[] {
                toHex(FIAP_PINK_VIBRANT),
                toHex(ASTERIA_OCEAN_BLUE),
                toHex(ASTERIA_AMETHYST),
                toHex(SUCCESS_EMERALD),
                toHex(WARNING_AMBER),
                toHex(FIAP_PINK_DARK),
                toHex(ASTERIA_MIDNIGHT_BLUE),
                toHex(FIAP_GRAY_DARK)
        };
    }

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