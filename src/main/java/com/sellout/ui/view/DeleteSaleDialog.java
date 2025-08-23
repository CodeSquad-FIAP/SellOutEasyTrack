package com.sellout.ui.view;

import com.sellout.controller.SalesController;
import com.sellout.model.Sale;
import com.sellout.ui.component.BaseDialog;
import com.sellout.ui.component.StyledTable;
import com.sellout.ui.util.UIUtils;
import com.sellout.config.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DeleteSaleDialog extends BaseDialog {
    private final SalesController salesController;
    private StyledTable salesTable;
    private JButton deleteButton;

    public DeleteSaleDialog(Frame parent, SalesController salesController) {
        super(parent, "Delete Sale");
        this.salesController = salesController;
        setSize(850, 600);
        loadSalesData();
        setVisible(true);
    }

    @Override
    protected void setupUI() {
        setLayout(new BorderLayout());

        add(createHeaderPanel("DELETE SALE"), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);

        JPanel instructionPanel = createInstructionPanel();
        JPanel tablePanel = createTablePanel();

        mainPanel.add(instructionPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createInstructionPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(100, 100, 100));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.Colors.WARNING_AMBER, 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        JLabel instruction = new JLabel(
                "<html><b style='color:white;'>WARNING:</b><font color='white'> Select a sale from the table below and click 'Delete Selected' to permanently remove it.</font></html>");
        panel.add(instruction);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        String[] columnNames = {"ID", "Product", "Quantity", "Unit Price (R$)", "Total (R$)", "Date"};
        salesTable = new StyledTable(columnNames);
        salesTable.setSelectionBackground(UIConstants.Colors.DANGER_CARDINAL.brighter());

        salesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                deleteButton.setEnabled(salesTable.getSelectedRow() != -1);
            }
        });

        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UIConstants.Colors.FIAP_GRAY_DARK);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        buttonPanel.setOpaque(false);

        deleteButton = createDangerButton("Delete Selected");
        JButton refreshButton = createPrimaryButton("Refresh List");
        JButton closeButton = createSecondaryButton("Close");

        deleteButton.setEnabled(false);

        deleteButton.addActionListener(e -> handleDeleteSelectedSale());
        refreshButton.addActionListener(e -> loadSalesData());
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        return buttonPanel;
    }

    private void loadSalesData() {
        try {
            List<Sale> sales = salesController.getAllSales();
            displaySalesData(sales);
        } catch (Exception e) {
            showErrorMessage("Failed to load sales: " + e.getMessage());
        }
    }

    private void displaySalesData(List<Sale> sales) {
        salesTable.clearData();
        deleteButton.setEnabled(false);

        if (sales.isEmpty()) {
            Object[] noDataRow = {"—", "No sales found", "—", "—", "—", "—"};
            salesTable.addRow(noDataRow);
            return;
        }

        for (Sale sale : sales) {
            Object[] rowData = {
                    sale.getId(),
                    sale.getProductName(),
                    sale.getQuantity(),
                    UIUtils.formatCurrency(sale.getUnitPrice()),
                    UIUtils.formatCurrency(sale.getTotalValue()),
                    UIUtils.formatDate(sale.getSaleDate())
            };
            salesTable.addRow(rowData);
        }
    }

    private void handleDeleteSelectedSale() {
        int selectedRow = salesTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Please select a sale to delete");
            return;
        }

        Object idObj = salesTable.getValueAt(selectedRow, 0);
        if (!(idObj instanceof Long)) {
            showWarningMessage("Cannot delete: no valid sale selected");
            return;
        }

        Long saleId = (Long) idObj;
        String productName = (String) salesTable.getValueAt(selectedRow, 1);

        boolean confirmed = showConfirmDialog(
                "Are you sure you want to permanently delete the sale for product '" + productName + "'?");

        if (confirmed) {
            try {
                salesController.deleteSale(saleId);
                showSuccessMessage(UIConstants.Messages.SUCCESS_SALE_DELETED);
                loadSalesData();
            } catch (Exception e) {
                showErrorMessage("Failed to delete sale: " + e.getMessage());
                loadSalesData(); // Refresh in case the sale was already deleted
            }
        }
    }
}