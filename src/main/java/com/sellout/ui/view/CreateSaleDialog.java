package com.sellout.ui.view;

import com.sellout.controller.SalesController;
import com.sellout.model.Sale;
import com.sellout.ui.component.BaseDialog;
import com.sellout.ui.util.UIUtils;
import com.sellout.config.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateSaleDialog extends BaseDialog {
    private final SalesController salesController;
    private JTextField productNameField;
    private JTextField quantityField;
    private JTextField unitPriceField;

    public CreateSaleDialog(Frame parent, SalesController salesController) {
        super(parent, "Create New Sale");
        this.salesController = salesController;
        setVisible(true);
    }

    @Override
    protected void setupUI() {
        setLayout(new BorderLayout());

        add(createHeaderPanel("CREATE NEW SALE"), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Product name
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(createStyledLabel("Product Name:"), gbc);
        gbc.gridx = 1;
        productNameField = createStyledTextField();
        mainPanel.add(productNameField, gbc);

        // Quantity
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(createStyledLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        quantityField = createStyledTextField();
        mainPanel.add(quantityField, gbc);

        // Unit price
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(createStyledLabel("Unit Price (R$):"), gbc);
        gbc.gridx = 1;
        unitPriceField = createStyledTextField();
        mainPanel.add(unitPriceField, gbc);

        return mainPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setOpaque(false);

        JButton saveButton = createPrimaryButton("Save Sale");
        JButton cancelButton = createSecondaryButton("Cancel");

        saveButton.addActionListener(e -> handleSaveSale());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    private void handleSaveSale() {
        try {
            Sale sale = createSaleFromInput();
            salesController.createSale(sale);
            showSuccessMessage(UIConstants.Messages.SUCCESS_SALE_CREATED);
            dispose();
        } catch (IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        } catch (Exception e) {
            showErrorMessage("Failed to create sale: " + e.getMessage());
        }
    }

    private Sale createSaleFromInput() {
        String productName = getValidatedProductName();
        Integer quantity = getValidatedQuantity();
        BigDecimal unitPrice = getValidatedUnitPrice();

        return new Sale(productName, quantity, unitPrice, LocalDate.now());
    }

    private String getValidatedProductName() {
        String productName = productNameField.getText().trim();
        if (productName.isEmpty()) {
            productNameField.requestFocus();
            throw new IllegalArgumentException("Product name is required");
        }
        return productName;
    }

    private Integer getValidatedQuantity() {
        try {
            Integer quantity = UIUtils.parseInteger(quantityField.getText());
            if (quantity <= 0) {
                quantityField.requestFocus();
                throw new IllegalArgumentException("Quantity must be greater than zero");
            }
            return quantity;
        } catch (NumberFormatException e) {
            quantityField.requestFocus();
            throw new IllegalArgumentException("Invalid quantity format");
        }
    }

    private BigDecimal getValidatedUnitPrice() {
        try {
            BigDecimal unitPrice = UIUtils.parseBigDecimal(unitPriceField.getText());
            if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
                unitPriceField.requestFocus();
                throw new IllegalArgumentException("Unit price must be greater than zero");
            }
            return unitPrice;
        } catch (NumberFormatException e) {
            unitPriceField.requestFocus();
            throw new IllegalArgumentException("Invalid unit price format");
        }
    }
}