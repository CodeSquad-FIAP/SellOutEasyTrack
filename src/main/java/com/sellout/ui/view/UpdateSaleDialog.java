package com.sellout.ui.view;

import com.sellout.controller.SalesController;
import com.sellout.model.Sale;
import com.sellout.ui.component.BaseDialog;
import com.sellout.ui.util.UIUtils;
import com.sellout.config.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class UpdateSaleDialog extends BaseDialog {
    private final SalesController salesController;
    private JComboBox<SaleComboItem> salesComboBox;
    private JTextField productNameField;
    private JTextField quantityField;
    private JTextField unitPriceField;
    private JButton updateButton;
    private Sale selectedSale;

    public UpdateSaleDialog(Frame parent, SalesController salesController) {
        super(parent, "Update Sale");
        this.salesController = salesController;
        loadSalesIntoComboBox();
        setVisible(true);
    }

    @Override
    protected void setupUI() {
        setLayout(new BorderLayout());

        add(createHeaderPanel("UPDATE SALE"), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Sale selection
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(createStyledLabel("Select Sale:"), gbc);
        gbc.gridx = 1;
        salesComboBox = createSalesComboBox();
        mainPanel.add(salesComboBox, gbc);

        // Product name
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(createStyledLabel("Product Name:"), gbc);
        gbc.gridx = 1;
        productNameField = createStyledTextField();
        productNameField.setEnabled(false);
        mainPanel.add(productNameField, gbc);

        // Quantity
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(createStyledLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        quantityField = createStyledTextField();
        quantityField.setEnabled(false);
        mainPanel.add(quantityField, gbc);

        // Unit price
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(createStyledLabel("Unit Price (R$):"), gbc);
        gbc.gridx = 1;
        unitPriceField = createStyledTextField();
        unitPriceField.setEnabled(false);
        mainPanel.add(unitPriceField, gbc);

        return mainPanel;
    }

    private JComboBox<SaleComboItem> createSalesComboBox() {
        JComboBox<SaleComboItem> comboBox = new JComboBox<>();
        comboBox.addActionListener(e -> handleSaleSelection());
        return comboBox;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setOpaque(false);

        updateButton = createPrimaryButton("Update Sale");
        JButton cancelButton = createSecondaryButton("Cancel");

        updateButton.setEnabled(false);
        updateButton.addActionListener(e -> handleUpdateSale());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    private void loadSalesIntoComboBox() {
        try {
            List<Sale> sales = salesController.getAllSales();
            salesComboBox.removeAllItems();
            salesComboBox.addItem(new SaleComboItem(null, "-- Select a sale --"));

            for (Sale sale : sales) {
                String displayText = String.format("ID: %d - %s (Qty: %d)",
                        sale.getId(), sale.getProductName(), sale.getQuantity());
                salesComboBox.addItem(new SaleComboItem(sale, displayText));
            }
        } catch (Exception e) {
            showErrorMessage("Failed to load sales: " + e.getMessage());
        }
    }

    private void handleSaleSelection() {
        SaleComboItem selectedItem = (SaleComboItem) salesComboBox.getSelectedItem();
        if (selectedItem != null && selectedItem.getSale() != null) {
            selectedSale = selectedItem.getSale();
            populateFields();
            enableFields();
        } else {
            selectedSale = null;
            clearFields();
            disableFields();
        }
    }

    private void populateFields() {
        if (selectedSale != null) {
            productNameField.setText(selectedSale.getProductName());
            quantityField.setText(selectedSale.getQuantity().toString());
            unitPriceField.setText(selectedSale.getUnitPrice().toString());
        }
    }

    private void clearFields() {
        productNameField.setText("");
        quantityField.setText("");
        unitPriceField.setText("");
    }

    private void enableFields() {
        productNameField.setEnabled(true);
        quantityField.setEnabled(true);
        unitPriceField.setEnabled(true);
        updateButton.setEnabled(true);
    }

    private void disableFields() {
        productNameField.setEnabled(false);
        quantityField.setEnabled(false);
        unitPriceField.setEnabled(false);
        updateButton.setEnabled(false);
    }

    private void handleUpdateSale() {
        if (selectedSale == null) {
            showWarningMessage("Please select a sale to update");
            return;
        }

        try {
            Sale updatedSale = createUpdatedSaleFromInput();
            salesController.updateSale(updatedSale);
            showSuccessMessage(UIConstants.Messages.SUCCESS_SALE_UPDATED);
            dispose();
        } catch (IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        } catch (Exception e) {
            showErrorMessage("Failed to update sale: " + e.getMessage());
        }
    }

    private Sale createUpdatedSaleFromInput() {
        String productName = getValidatedProductName();
        Integer quantity = getValidatedQuantity();
        BigDecimal unitPrice = getValidatedUnitPrice();

        return new Sale(selectedSale.getId(), productName, quantity, unitPrice, selectedSale.getSaleDate());
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

    private static class SaleComboItem {
        private final Sale sale;
        private final String displayText;

        public SaleComboItem(Sale sale, String displayText) {
            this.sale = sale;
            this.displayText = displayText;
        }

        public Sale getSale() {
            return sale;
        }

        @Override
        public String toString() {
            return displayText;
        }
    }
}