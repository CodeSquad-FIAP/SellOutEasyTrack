package com.sellout.ui.view;

import com.sellout.controller.SalesController;
import com.sellout.model.Sale;
import com.sellout.ui.component.BaseDialog;
import com.sellout.ui.component.StyledTable;
import com.sellout.ui.util.UIUtils;
import com.sellout.config.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class ListSalesDialog extends BaseDialog {
    private final SalesController salesController;
    private StyledTable salesTable;
    private JLabel totalSalesLabel;
    private JLabel totalValueLabel;

    public ListSalesDialog(Frame parent, SalesController salesController) {
        super(parent, "Sales List");
        this.salesController = salesController;
        setSize(800, 600);
        loadSalesData();
        setVisible(true);
    }

    @Override
    protected void setupUI() {
        setLayout(new BorderLayout());

        add(createHeaderPanel("SALES LIST"), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        String[] columnNames = {"ID", "Product", "Quantity", "Unit Price (R$)", "Total (R$)", "Date"};
        salesTable = new StyledTable(columnNames);

        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UIConstants.Colors.FIAP_GRAY_DARK);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        JPanel statisticsPanel = createStatisticsPanel();
        JPanel buttonPanel = createButtonPanel();

        bottomPanel.add(statisticsPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        return bottomPanel;
    }

    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIConstants.Colors.FIAP_GRAY_MEDIUM),
                "Statistics",
                0, 0, UIConstants.Fonts.LABEL_FONT, UIConstants.Colors.PURE_WHITE));

        totalSalesLabel = createStyledLabel("Total Sales: 0");
        totalValueLabel = createStyledLabel("Total Value: R$ 0,00");

        totalSalesLabel.setForeground(UIConstants.Colors.FIAP_PINK_VIBRANT);
        totalValueLabel.setForeground(UIConstants.Colors.FIAP_PINK_VIBRANT);

        panel.add(totalSalesLabel);
        panel.add(new JLabel("|") {{ setForeground(UIConstants.Colors.FIAP_GRAY_MEDIUM); }});
        panel.add(totalValueLabel);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        buttonPanel.setOpaque(false);

        JButton refreshButton = createPrimaryButton("Refresh");
        JButton closeButton = createSecondaryButton("Close");

        refreshButton.addActionListener(e -> loadSalesData());
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        return buttonPanel;
    }

    private void loadSalesData() {
        try {
            List<Sale> sales = salesController.getAllSales();
            displaySalesData(sales);
            updateStatistics(sales);
        } catch (Exception e) {
            showErrorMessage("Failed to load sales: " + e.getMessage());
        }
    }

    private void displaySalesData(List<Sale> sales) {
        salesTable.clearData();

        if (sales.isEmpty()) {
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

    private void updateStatistics(List<Sale> sales) {
        int totalCount = sales.size();
        BigDecimal totalValue = sales.stream()
                .map(Sale::getTotalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalSalesLabel.setText("Total Sales: " + totalCount);
        totalValueLabel.setText("Total Value: " + UIUtils.formatCurrency(totalValue));
    }
}