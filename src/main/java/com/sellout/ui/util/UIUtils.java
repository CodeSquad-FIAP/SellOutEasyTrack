package com.sellout.ui.util;

import com.sellout.config.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public final class UIUtils {

    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private UIUtils() {} // Utility class

    public static String formatCurrency(BigDecimal value) {
        return CURRENCY_FORMAT.format(value);
    }

    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    public static LocalDate parseDate(String dateString) throws DateTimeParseException {
        return LocalDate.parse(dateString, DATE_FORMATTER);
    }

    public static BigDecimal parseBigDecimal(String value) throws NumberFormatException {
        if (value == null || value.trim().isEmpty()) {
            throw new NumberFormatException("Value cannot be empty");
        }

        String cleanValue = value.trim().replace(",", ".");
        return new BigDecimal(cleanValue);
    }

    public static Integer parseInteger(String value) throws NumberFormatException {
        if (value == null || value.trim().isEmpty()) {
            throw new NumberFormatException("Value cannot be empty");
        }

        return Integer.valueOf(value.trim());
    }

    public static void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarculaLaf");

            // Customize UI properties
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("ProgressBar.arc", 8);
            UIManager.put("TextComponent.arc", 8);

            UIManager.put("Button.default.background", UIConstants.Colors.FIAP_PINK_VIBRANT);
            UIManager.put("Component.focusColor", UIConstants.Colors.ASTERIA_AMETHYST);
            UIManager.put("ProgressBar.foreground", UIConstants.Colors.SUCCESS_EMERALD);

        } catch (Exception e) {
            System.err.println("Failed to set look and feel: " + e.getMessage());
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Failed to set system look and feel: " + ex.getMessage());
            }
        }
    }

    public static JDialog createLoadingDialog(Component parent, String title, String message) {
        JDialog loadingDialog = new JDialog();
        loadingDialog.setTitle(title);
        loadingDialog.setModal(true);
        loadingDialog.setSize(350, 120);
        loadingDialog.setLocationRelativeTo(parent);
        loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel messageLabel = new JLabel(message, JLabel.CENTER);
        messageLabel.setFont(UIConstants.Fonts.SUBTITLE_FONT);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setString("Processing...");
        progressBar.setStringPainted(true);

        contentPanel.add(messageLabel, BorderLayout.CENTER);
        contentPanel.add(progressBar, BorderLayout.SOUTH);

        loadingDialog.add(contentPanel);
        return loadingDialog;
    }
}