package com.sellout.ui.component;

import com.sellout.config.UIConstants;

import javax.swing.*;
import java.awt.*;

public abstract class BaseDialog extends JDialog {

    protected BaseDialog(Frame parent, String title) {
        super(parent, title, true);
        initializeDialog();
        setupUI();
    }

    private void initializeDialog() {
        setSize(UIConstants.Dimensions.DIALOG_WIDTH, UIConstants.Dimensions.DIALOG_HEIGHT);
        setLocationRelativeTo(getParent());
        setResizable(false);
        getContentPane().setBackground(UIConstants.Colors.FIAP_GRAY_DARK);
    }

    protected abstract void setupUI();

    protected JPanel createHeaderPanel(String title) {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(UIConstants.Colors.FIAP_PINK_VIBRANT);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConstants.Fonts.TITLE_FONT);
        titleLabel.setForeground(UIConstants.Colors.PURE_WHITE);
        headerPanel.add(titleLabel);

        return headerPanel;
    }

    protected JTextField createStyledTextField() {
        JTextField textField = new JTextField(20);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.Colors.FIAP_GRAY_MEDIUM),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        textField.setBackground(UIConstants.Colors.FIAP_BLACK_TECH);
        textField.setForeground(UIConstants.Colors.PURE_WHITE);
        textField.setCaretColor(UIConstants.Colors.PURE_WHITE);
        textField.setFont(UIConstants.Fonts.TEXT_FIELD_FONT);
        return textField;
    }

    protected JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.Fonts.LABEL_FONT);
        label.setForeground(UIConstants.Colors.PURE_WHITE);
        return label;
    }

    protected JButton createPrimaryButton(String text) {
        return createStyledButton(text, UIConstants.Colors.FIAP_PINK_VIBRANT);
    }

    protected JButton createSecondaryButton(String text) {
        return createStyledButton(text, UIConstants.Colors.FIAP_GRAY_MEDIUM);
    }

    protected JButton createDangerButton(String text) {
        return createStyledButton(text, UIConstants.Colors.DANGER_CARDINAL);
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(UIConstants.Colors.PURE_WHITE);
        button.setFont(UIConstants.Fonts.BUTTON_FONT);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        Color originalColor = backgroundColor;
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });

        return button;
    }

    protected void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    protected void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    protected void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    protected boolean showConfirmDialog(String message) {
        int result = JOptionPane.showConfirmDialog(
                this, message, "Confirm",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }
}