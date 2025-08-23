package com.sellout.config;

import java.awt.Color;
import java.awt.Font;

public final class UIConstants {

    // Colors - FIAP + Asteria Palette
    public static final class Colors {
        public static final Color FIAP_PINK_VIBRANT = new Color(242, 48, 100);
        public static final Color FIAP_PINK_DARK = new Color(191, 59, 94);
        public static final Color FIAP_GRAY_MEDIUM = new Color(140, 140, 140);
        public static final Color FIAP_GRAY_DARK = new Color(64, 64, 64);
        public static final Color FIAP_BLACK_TECH = new Color(38, 38, 38);
        public static final Color ASTERIA_MIDNIGHT_BLUE = new Color(44, 62, 80);
        public static final Color ASTERIA_AMETHYST = new Color(142, 68, 173);
        public static final Color ASTERIA_OCEAN_BLUE = new Color(52, 152, 219);
        public static final Color SUCCESS_EMERALD = new Color(46, 204, 113);
        public static final Color WARNING_AMBER = new Color(243, 156, 18);
        public static final Color DANGER_CARDINAL = new Color(231, 76, 60);
        public static final Color PURE_WHITE = new Color(255, 255, 255);
        public static final Color LIGHT_GRAY = new Color(236, 240, 241);

        private Colors() {} // Utility class
    }

    // Fonts
    public static final class Fonts {
        public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
        public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
        public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 12);
        public static final Font TEXT_FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 12);
        public static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);

        private Fonts() {} // Utility class
    }

    // Dimensions
    public static final class Dimensions {
        public static final int DIALOG_WIDTH = 550;
        public static final int DIALOG_HEIGHT = 450;
        public static final int BUTTON_HEIGHT = 45;
        public static final int BUTTON_WIDTH = 160;
        public static final int TEXT_FIELD_HEIGHT = 35;

        private Dimensions() {} // Utility class
    }

    // Messages
    public static final class Messages {
        public static final String APP_TITLE = "SellOut EasyTrack";
        public static final String SUCCESS_SALE_CREATED = "Sale created successfully!";
        public static final String SUCCESS_SALE_UPDATED = "Sale updated successfully!";
        public static final String SUCCESS_SALE_DELETED = "Sale deleted successfully!";
        public static final String ERROR_INVALID_DATA = "Please check the entered data";
        public static final String ERROR_DATABASE = "Database error occurred";
        public static final String CONFIRM_DELETE = "Are you sure you want to delete this sale?";

        private Messages() {} // Utility class
    }

    private UIConstants() {} // Utility class
}