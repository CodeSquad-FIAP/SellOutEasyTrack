// src/main/java/com/sellout/ui/view/MainDashboard.java
package com.sellout.ui.view;

import com.sellout.config.ApplicationConfig;
import com.sellout.controller.ChartController;
import com.sellout.controller.ImportController;
import com.sellout.controller.SalesController;
import com.sellout.model.ImportResult;
import com.sellout.model.Sale;
import com.sellout.model.SalesStatistics;
import com.sellout.service.ChartService;
import com.sellout.service.ImportService;
import com.sellout.service.impl.ChartServiceImpl;
import com.sellout.service.impl.ImportServiceImpl;
import com.sellout.ui.listener.SalesChangeListener;
import com.sellout.ui.util.UIUtils;
import com.sellout.config.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MainDashboard extends JFrame implements SalesChangeListener {
    private final SalesController salesController;
    private final ImportController importController;
    private final ChartController chartController;

    private JPanel chartPanel;
    private JPanel chartDisplayPanel;
    private JLabel totalSalesLabel;
    private JLabel totalRevenueLabel;
    private JLabel averageTicketLabel;

    public MainDashboard() {
        super(UIConstants.Messages.APP_TITLE + " - Dashboard");

        ApplicationConfig config = ApplicationConfig.getInstance();
        this.salesController = new SalesController(config.getSalesService());
        this.importController = new ImportController(new ImportServiceImpl(config.getSalesService()));
        this.chartController = new ChartController(new ChartServiceImpl());

        salesController.addSalesChangeListener(this);
        importController.addSalesChangeListener(this);

        setupMainFrame();
        createUI();
        updateDashboard();
        setVisible(true);
    }

    private void setupMainFrame() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void createUI() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createSidebarPanel(), BorderLayout.WEST);
        add(createContentPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.Colors.FIAP_BLACK_TECH);
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));
        headerPanel.setPreferredSize(new Dimension(0, 80));

        JLabel titleLabel = new JLabel(UIConstants.Messages.APP_TITLE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(UIConstants.Colors.PURE_WHITE);

        JLabel subtitleLabel = new JLabel("Sales Management System - Clean Architecture");
        subtitleLabel.setForeground(UIConstants.Colors.LIGHT_GRAY);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        return headerPanel;
    }

    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UIConstants.Colors.ASTERIA_MIDNIGHT_BLUE);
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setBorder(new EmptyBorder(20, 0, 20, 0));

        // Menu title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel menuTitle = new JLabel("MAIN MENU");
        menuTitle.setFont(UIConstants.Fonts.BUTTON_FONT);
        menuTitle.setForeground(UIConstants.Colors.LIGHT_GRAY);
        titlePanel.add(menuTitle);

        sidebar.add(titlePanel);
        sidebar.add(Box.createVerticalStrut(10));

        // Menu items
        sidebar.add(createMenuButton("SALES", "Manage sales operations", this::showSalesMenu));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createMenuButton("IMPORT", "Import data from multiple sources", this::showImportMenu));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createMenuButton("CHARTS", "Generate visual analytics", this::generateCharts));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createMenuButton("EXPORT", "Export data to CSV", this::exportData));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createMenuButton("REPORTS", "Generate detailed reports", this::generateReports));

        sidebar.add(Box.createVerticalGlue());

        // Statistics panel
        sidebar.add(createStatisticsPanel());
        sidebar.add(Box.createVerticalStrut(20));

        sidebar.add(createMenuButton("EXIT", "Close application", this::exitApplication));

        return sidebar;
    }

    private JPanel createMenuButton(String title, String description, Runnable action) {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        buttonPanel.setBorder(new EmptyBorder(8, 25, 8, 25));
        buttonPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConstants.Fonts.LABEL_FONT);
        titleLabel.setForeground(UIConstants.Colors.PURE_WHITE);

        JLabel descLabel = new JLabel(description);
        descLabel.setForeground(UIConstants.Colors.LIGHT_GRAY);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(descLabel, BorderLayout.SOUTH);

        buttonPanel.add(textPanel, BorderLayout.CENTER);

        addHoverEffect(buttonPanel, action);
        return buttonPanel;
    }

    private void addHoverEffect(JPanel buttonPanel, Runnable action) {
        buttonPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                buttonPanel.setOpaque(true);
                buttonPanel.setBackground(UIConstants.Colors.FIAP_PINK_DARK);
                buttonPanel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                buttonPanel.setOpaque(false);
                buttonPanel.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        action.run();
                    } catch (Exception ex) {
                        showErrorDialog("Operation failed: " + ex.getMessage());
                    }
                });
            }
        });
    }

    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("LIVE STATISTICS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        titleLabel.setForeground(UIConstants.Colors.PURE_WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        totalSalesLabel = new JLabel("Sales: 0");
        totalSalesLabel.setForeground(UIConstants.Colors.PURE_WHITE);
        totalSalesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        totalRevenueLabel = new JLabel("R$ 0,00");
        totalRevenueLabel.setFont(UIConstants.Fonts.BUTTON_FONT);
        totalRevenueLabel.setForeground(UIConstants.Colors.FIAP_PINK_VIBRANT);
        totalRevenueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        averageTicketLabel = new JLabel("Avg: R$ 0,00");
        averageTicketLabel.setForeground(UIConstants.Colors.LIGHT_GRAY);
        averageTicketLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(totalSalesLabel);
        panel.add(totalRevenueLabel);
        panel.add(averageTicketLabel);

        return panel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIConstants.Colors.LIGHT_GRAY);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        chartPanel = createChartPanel();
        contentPanel.add(chartPanel, BorderLayout.CENTER);

        return contentPanel;
    }

    private JPanel createChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.Colors.PURE_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.Colors.LIGHT_GRAY, 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel titleLabel = new JLabel("Sales Analytics");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(UIConstants.Colors.ASTERIA_MIDNIGHT_BLUE);

        JLabel subtitleLabel = new JLabel("Visual representation of sales data");
        subtitleLabel.setForeground(UIConstants.Colors.FIAP_BLACK_TECH);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Add refresh button to title panel
        JButton refreshButton = new JButton("Refresh Chart");
        refreshButton.setBackground(UIConstants.Colors.FIAP_PINK_VIBRANT);
        refreshButton.setForeground(UIConstants.Colors.PURE_WHITE);
        refreshButton.setFont(UIConstants.Fonts.BUTTON_FONT);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> generateCharts());
        titlePanel.add(refreshButton, BorderLayout.EAST);

        chartDisplayPanel = new JPanel(new BorderLayout());
        chartDisplayPanel.setBackground(UIConstants.Colors.PURE_WHITE);
        chartDisplayPanel.setBorder(BorderFactory.createLineBorder(UIConstants.Colors.LIGHT_GRAY, 1));
        chartDisplayPanel.setPreferredSize(new Dimension(0, 400));

        JLabel placeholderLabel = new JLabel("Charts will be displayed here", JLabel.CENTER);
        placeholderLabel.setForeground(UIConstants.Colors.FIAP_GRAY_MEDIUM);
        chartDisplayPanel.add(placeholderLabel, BorderLayout.CENTER);

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(chartDisplayPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(UIConstants.Colors.FIAP_GRAY_MEDIUM);
        footerPanel.setBorder(new EmptyBorder(12, 25, 12, 25));
        footerPanel.setPreferredSize(new Dimension(0, 45));

        JLabel footerText = new JLabel(UIConstants.Messages.APP_TITLE + " - Sales Management System v2.0");
        footerText.setForeground(UIConstants.Colors.PURE_WHITE);

        JLabel statusLabel = new JLabel("System Online | Clean Architecture");
        statusLabel.setForeground(UIConstants.Colors.LIGHT_GRAY);

        footerPanel.add(footerText, BorderLayout.WEST);
        footerPanel.add(statusLabel, BorderLayout.EAST);

        return footerPanel;
    }

    @Override
    public void onSalesChanged() {
        SwingUtilities.invokeLater(this::updateDashboard);
    }

    private void updateDashboard() {
        updateStatistics();
        generateCharts();
    }

    private void updateStatistics() {
        try {
            SalesStatistics stats = salesController.getStatistics();

            totalSalesLabel.setText(String.format("Sales: %d", stats.getTotalSalesCount()));
            totalRevenueLabel.setText(UIUtils.formatCurrency(stats.getTotalRevenue()));
            averageTicketLabel.setText("Avg: " + UIUtils.formatCurrency(stats.getAverageTicket()));
        } catch (Exception e) {
            System.err.println("Failed to update statistics: " + e.getMessage());
        }
    }

    private void showSalesMenu() {
        JDialog dialog = new JDialog(this, "Sales Operations", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(UIConstants.Colors.FIAP_PINK_VIBRANT);
        headerPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        JLabel titulo = new JLabel("SALES MENU", JLabel.CENTER);
        titulo.setFont(UIConstants.Fonts.TITLE_FONT);
        titulo.setForeground(UIConstants.Colors.PURE_WHITE);
        headerPanel.add(titulo);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(UIConstants.Colors.FIAP_GRAY_DARK);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton btnCreate = createDialogButton("Create Sale", "Register new sale in the system");
        JButton btnList = createDialogButton("List Sales", "View all registered sales");
        JButton btnUpdate = createDialogButton("Update Sale", "Edit existing sale information");
        JButton btnDelete = createDialogButton("Delete Sale", "Remove sale from system");

        btnCreate.addActionListener(e -> { dialog.dispose(); new CreateSaleDialog(this, salesController); });
        btnList.addActionListener(e -> { dialog.dispose(); new ListSalesDialog(this, salesController); });
        btnUpdate.addActionListener(e -> { dialog.dispose(); new UpdateSaleDialog(this, salesController); });
        btnDelete.addActionListener(e -> { dialog.dispose(); new DeleteSaleDialog(this, salesController); });

        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(btnCreate);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(btnList);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(btnUpdate);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(btnDelete);
        mainPanel.add(Box.createVerticalGlue());

        JPanel buttonPanel = createDialogButtonPanel(dialog);

        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showImportMenu() {
        JDialog dialog = new JDialog(this, "Import Data", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(UIConstants.Colors.FIAP_PINK_VIBRANT);
        headerPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        JLabel titulo = new JLabel("IMPORT DATA", JLabel.CENTER);
        titulo.setFont(UIConstants.Fonts.TITLE_FONT);
        titulo.setForeground(UIConstants.Colors.PURE_WHITE);
        headerPanel.add(titulo);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(UIConstants.Colors.FIAP_GRAY_DARK);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton btnCsv = createDialogButton("Import CSV", "Load structured CSV file");
        JButton btnText = createDialogButton("Import Text File", "Process TXT file with data");
        JButton btnWhatsApp = createDialogButton("Import WhatsApp", "Import WhatsApp messages");
        JButton btnTemplate = createDialogButton("Generate Template", "Create CSV template for import");

        btnCsv.addActionListener(e -> { dialog.dispose(); importFromCsv(); });
        btnText.addActionListener(e -> { dialog.dispose(); importFromText(); });
        btnWhatsApp.addActionListener(e -> { dialog.dispose(); importFromWhatsApp(); });
        btnTemplate.addActionListener(e -> { dialog.dispose(); generateImportTemplate(); });

        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(btnCsv);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(btnText);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(btnWhatsApp);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(btnTemplate);
        mainPanel.add(Box.createVerticalGlue());

        JPanel buttonPanel = createDialogButtonPanel(dialog);

        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private JButton createDialogButton(String title, String description) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setBackground(UIConstants.Colors.FIAP_BLACK_TECH);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(400, 60));
        button.setMaximumSize(new Dimension(400, 60));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.Colors.FIAP_GRAY_MEDIUM),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConstants.Fonts.LABEL_FONT);
        titleLabel.setForeground(UIConstants.Colors.PURE_WHITE);

        JLabel descLabel = new JLabel(description);
        descLabel.setForeground(UIConstants.Colors.LIGHT_GRAY);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(descLabel, BorderLayout.SOUTH);

        button.add(textPanel, BorderLayout.CENTER);

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(UIConstants.Colors.FIAP_PINK_DARK);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIConstants.Colors.FIAP_PINK_VIBRANT, 2),
                        BorderFactory.createEmptyBorder(9, 14, 9, 14)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(UIConstants.Colors.FIAP_BLACK_TECH);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIConstants.Colors.FIAP_GRAY_MEDIUM),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)));
            }
        });

        return button;
    }

    private JPanel createDialogButtonPanel(JDialog dialog) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(UIConstants.Colors.FIAP_GRAY_DARK);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(UIConstants.Colors.FIAP_GRAY_MEDIUM);
        cancelButton.setForeground(UIConstants.Colors.PURE_WHITE);
        cancelButton.setFont(UIConstants.Fonts.BUTTON_FONT);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelButton);
        return buttonPanel;
    }

    private void importFromCsv() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV files", "csv"));
        fileChooser.setDialogTitle("Select CSV file to import");

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            JDialog loadingDialog = UIUtils.createLoadingDialog(this, "Importing CSV", "Processing CSV file...");

            SwingWorker<ImportResult, Void> worker = new SwingWorker<ImportResult, Void>() {
                @Override
                protected ImportResult doInBackground() throws Exception {
                    return importController.importFromCsv(selectedFile);
                }

                @Override
                protected void done() {
                    loadingDialog.dispose();
                    try {
                        ImportResult result = get();
                        showImportResult(result);
                    } catch (Exception e) {
                        showErrorDialog("CSV import failed: " + e.getMessage());
                    }
                }
            };

            worker.execute();
            loadingDialog.setVisible(true);
        }
    }

    private void importFromText() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text files", "txt"));
        fileChooser.setDialogTitle("Select text file to import");

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            JDialog loadingDialog = UIUtils.createLoadingDialog(this, "Importing Text", "Processing text file...");

            SwingWorker<ImportResult, Void> worker = new SwingWorker<ImportResult, Void>() {
                @Override
                protected ImportResult doInBackground() throws Exception {
                    return importController.importFromText(selectedFile);
                }

                @Override
                protected void done() {
                    loadingDialog.dispose();
                    try {
                        ImportResult result = get();
                        showImportResult(result);
                    } catch (Exception e) {
                        showErrorDialog("Text import failed: " + e.getMessage());
                    }
                }
            };

            worker.execute();
            loadingDialog.setVisible(true);
        }
    }

    private void importFromWhatsApp() {
        JDialog dialog = new JDialog(this, "Import WhatsApp Messages", true);
        dialog.setSize(650, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(UIConstants.Colors.FIAP_GRAY_DARK);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(UIConstants.Colors.FIAP_PINK_VIBRANT);
        headerPanel.setBorder(new EmptyBorder(15, 0, 15, 0));

        JLabel titulo = new JLabel("IMPORT WHATSAPP", JLabel.CENTER);
        titulo.setFont(UIConstants.Fonts.TITLE_FONT);
        titulo.setForeground(UIConstants.Colors.PURE_WHITE);
        headerPanel.add(titulo);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel instruction = new JLabel("Paste WhatsApp messages with sales information here:");
        instruction.setFont(UIConstants.Fonts.LABEL_FONT);
        instruction.setForeground(UIConstants.Colors.PURE_WHITE);

        String exampleText = "Sold 5 Logitech Mouse for R$ 85.50 each\n" +
                "João: Bought 2 Mechanical Keyboard R$ 320.00\n" +
                "Maria sold 3 Dell Notebook - R$ 2500 each\n" +
                "10 USB-C Charger sold for 45.90 reais\n" +
                "Samsung Smartphone: 4 units x R$ 1200\n\n" +
                "TIP: The system automatically recognizes:\n" +
                "- Product names mentioned\n" +
                "- Quantities (numbers)\n" +
                "- Values (R$ or reais)\n" +
                "- Various text formats";

        JTextArea textArea = new JTextArea(12, 50);
        textArea.setFont(UIConstants.Fonts.TEXT_FIELD_FONT);
        textArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.Colors.FIAP_GRAY_MEDIUM),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        textArea.setBackground(UIConstants.Colors.FIAP_BLACK_TECH);
        textArea.setForeground(UIConstants.Colors.PURE_WHITE);
        textArea.setCaretColor(UIConstants.Colors.PURE_WHITE);
        textArea.setText(exampleText);
        textArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(null);

        contentPanel.add(instruction, BorderLayout.NORTH);
        contentPanel.add(Box.createVerticalStrut(10), BorderLayout.CENTER);
        contentPanel.add(scrollPane, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        buttonPanel.setOpaque(false);

        JButton clearButton = new JButton("Clear");
        clearButton.setBackground(UIConstants.Colors.WARNING_AMBER);
        clearButton.setForeground(UIConstants.Colors.PURE_WHITE);
        clearButton.setFont(UIConstants.Fonts.BUTTON_FONT);
        clearButton.setFocusPainted(false);
        clearButton.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        clearButton.addActionListener(e -> {
            textArea.setText("");
            textArea.requestFocus();
        });

        JButton importButton = new JButton("Import");
        importButton.setBackground(UIConstants.Colors.FIAP_PINK_VIBRANT);
        importButton.setForeground(UIConstants.Colors.PURE_WHITE);
        importButton.setFont(UIConstants.Fonts.BUTTON_FONT);
        importButton.setFocusPainted(false);
        importButton.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        importButton.addActionListener(e -> {
            String text = textArea.getText().trim();
            if (!text.isEmpty() && !text.equals(exampleText)) {
                dialog.dispose();

                JDialog loadingDialog = UIUtils.createLoadingDialog(this, "Importing WhatsApp", "Processing messages...");

                SwingWorker<ImportResult, Void> worker = new SwingWorker<ImportResult, Void>() {
                    @Override
                    protected ImportResult doInBackground() throws Exception {
                        return importController.importFromWhatsApp(text);
                    }

                    @Override
                    protected void done() {
                        loadingDialog.dispose();
                        try {
                            ImportResult result = get();
                            showImportResult(result);
                        } catch (Exception ex) {
                            showErrorDialog("WhatsApp import failed: " + ex.getMessage());
                        }
                    }
                };

                worker.execute();
                loadingDialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Please replace the example with your actual WhatsApp messages!",
                        "Input Required", JOptionPane.WARNING_MESSAGE);
                textArea.selectAll();
                textArea.requestFocus();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(UIConstants.Colors.FIAP_GRAY_MEDIUM);
        cancelButton.setForeground(UIConstants.Colors.PURE_WHITE);
        cancelButton.setFont(UIConstants.Fonts.BUTTON_FONT);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(clearButton);
        buttonPanel.add(importButton);
        buttonPanel.add(cancelButton);

        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);

        SwingUtilities.invokeLater(() -> {
            textArea.requestFocus();
            textArea.selectAll();
        });
    }

    private void generateImportTemplate() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Import Template");
        chooser.setSelectedFile(new File("sales_import_template.csv"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            String path = selectedFile.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".csv")) {
                path += ".csv";
                selectedFile = new File(path);
            }

            try (FileWriter writer = new FileWriter(selectedFile)) {
                writer.write("product_name,quantity,unit_price,sale_date\n");
                writer.write("\"Sample Product 1\",5,25.50,\"2024-01-15\"\n");
                writer.write("\"Sample Product 2\",2,150.00,\"2024-01-16\"\n");
                writer.write("\"Sample Product 3\",10,8.90,\"2024-01-17\"\n");
                writer.write("\n# INSTRUCTIONS:\n");
                writer.write("# 1. Fill in quantity and unit_price for products you want to import\n");
                writer.write("# 2. Keep the header (first line) and column format\n");
                writer.write("# 3. Date format: YYYY-MM-DD\n");
                writer.write("# 4. Use decimal point (.) for prices\n");

                JOptionPane.showMessageDialog(this,
                        "Import template created successfully!\n\nFile: " + selectedFile.getAbsolutePath(),
                        "Template Created",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                showErrorDialog("Failed to create template: " + e.getMessage());
            }
        }
    }

    private void showImportResult(ImportResult result) {
        StringBuilder message = new StringBuilder();
        message.append(result.getSummary()).append("\n\n");

        if (result.hasErrors()) {
            message.append("ERRORS:\n");
            for (String error : result.getErrors()) {
                message.append("• ").append(error).append("\n");
            }
            message.append("\n");
        }

        if (result.hasWarnings()) {
            message.append("WARNINGS:\n");
            for (String warning : result.getWarnings()) {
                message.append("• ").append(warning).append("\n");
            }
        }

        JTextArea textArea = new JTextArea(message.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBackground(UIConstants.Colors.PURE_WHITE);
        textArea.setBorder(new EmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Import Results",
                result.hasErrors() ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
    }

    private void generateCharts() {
        try {
            List<Sale> sales = salesController.getAllSales();
            if (sales.isEmpty()) {
                showEmptyChartsMessage();
                return;
            }

            showChartLoadingMessage();

            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    if (chartController.isChartGenerationAvailable()) {
                        return chartController.generateChart(sales);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        String chartPath = get();
                        if (chartPath != null && new File(chartPath).exists()) {
                            displayChart(chartPath);
                        } else {
                            showBasicChart(sales);
                        }
                    } catch (Exception e) {
                        showBasicChart(sales);
                    }
                }
            };

            worker.execute();
        } catch (Exception e) {
            showChartError("Error generating charts: " + e.getMessage());
        }
    }

    private void showEmptyChartsMessage() {
        updateChartDisplay("No sales data available for chart generation.\n\nCreate some sales to see visual analytics.");
    }

    private void showChartLoadingMessage() {
        updateChartDisplay("Generating charts...");
    }

    private void showChartError(String message) {
        updateChartDisplay("Chart Error: " + message);
    }

    private void displayChart(String chartPath) {
        try {
            ImageIcon chartIcon = new ImageIcon(chartPath);

            // Scale the image to fit the panel
            int panelWidth = chartDisplayPanel.getWidth() - 20;
            int panelHeight = chartDisplayPanel.getHeight() - 20;

            if (panelWidth > 100 && panelHeight > 100) {
                Image img = chartIcon.getImage();
                Image scaledImg = img.getScaledInstance(
                        Math.min(panelWidth, chartIcon.getIconWidth()),
                        Math.min(panelHeight, chartIcon.getIconHeight()),
                        Image.SCALE_SMOOTH
                );
                chartIcon = new ImageIcon(scaledImg);
            }

            JLabel chartLabel = new JLabel(chartIcon, JLabel.CENTER);

            chartDisplayPanel.removeAll();
            chartDisplayPanel.add(chartLabel, BorderLayout.CENTER);
            chartDisplayPanel.revalidate();
            chartDisplayPanel.repaint();

        } catch (Exception e) {
            showChartError("Failed to display chart: " + e.getMessage());
        }
    }

    private void showBasicChart(List<Sale> sales) {
        // Create a simple text-based chart when R is not available
        StringBuilder chartText = new StringBuilder();
        chartText.append("SALES SUMMARY\n");
        chartText.append("═".repeat(50)).append("\n\n");

        // Get top 5 products
        java.util.Map<String, Integer> productCounts = new java.util.HashMap<>();
        for (Sale sale : sales) {
            productCounts.merge(sale.getProductName(), sale.getQuantity(), Integer::sum);
        }

        productCounts.entrySet().stream()
                .sorted(java.util.Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> {
                    String bar = "█".repeat(Math.min(entry.getValue(), 20));
                    chartText.append(String.format("%-20s %s (%d)\n",
                            entry.getKey().substring(0, Math.min(entry.getKey().length(), 20)),
                            bar, entry.getValue()));
                });

        chartText.append("\n").append("═".repeat(50));
        chartText.append("\nTotal Sales: ").append(sales.size());

        JTextArea textChart = new JTextArea(chartText.toString());
        textChart.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textChart.setBackground(UIConstants.Colors.PURE_WHITE);
        textChart.setForeground(UIConstants.Colors.FIAP_BLACK_TECH);
        textChart.setEditable(false);
        textChart.setBorder(new EmptyBorder(20, 20, 20, 20));

        chartDisplayPanel.removeAll();
        chartDisplayPanel.add(new JScrollPane(textChart), BorderLayout.CENTER);
        chartDisplayPanel.revalidate();
        chartDisplayPanel.repaint();
    }

    private void updateChartDisplay(String message) {
        chartDisplayPanel.removeAll();
        JLabel messageLabel = new JLabel("<html><center>" + message.replace("\n", "<br>") + "</center></html>", JLabel.CENTER);
        messageLabel.setForeground(UIConstants.Colors.FIAP_GRAY_MEDIUM);
        messageLabel.setFont(UIConstants.Fonts.SUBTITLE_FONT);
        chartDisplayPanel.add(messageLabel, BorderLayout.CENTER);
        chartDisplayPanel.revalidate();
        chartDisplayPanel.repaint();
    }

    private void exportData() {
        try {
            List<Sale> sales = salesController.getAllSales();
            if (sales.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No sales data to export",
                        "Export",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Export Sales Data");
            fileChooser.setSelectedFile(new File("sales_export.csv"));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".csv")) {
                    filePath += ".csv";
                }

                try (FileWriter writer = new FileWriter(filePath)) {
                    writer.write("ID,Product Name,Quantity,Unit Price,Total Value,Sale Date\n");

                    for (Sale sale : sales) {
                        writer.write(String.format("%d,\"%s\",%d,%.2f,%.2f,%s\n",
                                sale.getId(),
                                sale.getProductName(),
                                sale.getQuantity(),
                                sale.getUnitPrice(),
                                sale.getTotalValue(),
                                UIUtils.formatDate(sale.getSaleDate())
                        ));
                    }
                }

                JOptionPane.showMessageDialog(this,
                        "Data exported successfully!\n\n" +
                                "File: " + filePath + "\n" +
                                "Total records: " + sales.size(),
                        "Export Completed",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            showErrorDialog("Export failed: " + e.getMessage());
        }
    }

    private void generateReports() {
        try {
            List<Sale> sales = salesController.getAllSales();
            SalesStatistics stats = salesController.getStatistics();

            if (sales.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No sales data available for report generation",
                        "Reports",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Create detailed report
            StringBuilder report = new StringBuilder();
            report.append("═══════════════════════════════════════════════════════════════\n");
            report.append("                    SELLOUT EASYTRACK REPORT\n");
            report.append("═══════════════════════════════════════════════════════════════\n");
            report.append("Generated: ").append(java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n\n");

            report.append("EXECUTIVE SUMMARY:\n");
            report.append("─".repeat(50)).append("\n");
            report.append("• Total sales registered: ").append(stats.getTotalSalesCount()).append("\n");
            report.append("• Total revenue: ").append(UIUtils.formatCurrency(stats.getTotalRevenue())).append("\n");
            report.append("• Average ticket: ").append(UIUtils.formatCurrency(stats.getAverageTicket())).append("\n\n");

            // Top products
            report.append("TOP PRODUCTS BY QUANTITY:\n");
            report.append("─".repeat(50)).append("\n");

            java.util.Map<String, Integer> topProducts = new java.util.HashMap<>();
            for (Sale sale : sales) {
                topProducts.merge(sale.getProductName(), sale.getQuantity(), Integer::sum);
            }

            topProducts.entrySet().stream()
                    .sorted(java.util.Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(10)
                    .forEach(entry -> {
                        java.math.BigDecimal totalValue = sales.stream()
                                .filter(s -> s.getProductName().equals(entry.getKey()))
                                .map(Sale::getTotalValue)
                                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

                        report.append(String.format("• %-30s: %4d units - %s\n",
                                entry.getKey(), entry.getValue(), UIUtils.formatCurrency(totalValue)));
                    });

            report.append("\n═══════════════════════════════════════════════════════════════\n");
            report.append("Report generated by SellOut EasyTrack v2.0\n");

            // Display report
            JTextArea reportArea = new JTextArea(report.toString());
            reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            reportArea.setEditable(false);
            reportArea.setBackground(UIConstants.Colors.PURE_WHITE);
            reportArea.setBorder(new EmptyBorder(20, 20, 20, 20));

            JScrollPane scrollPane = new JScrollPane(reportArea);
            scrollPane.setPreferredSize(new Dimension(700, 500));

            JPanel reportPanel = new JPanel(new BorderLayout());
            reportPanel.add(scrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton saveButton = new JButton("Save Report");
            saveButton.setBackground(UIConstants.Colors.FIAP_PINK_VIBRANT);
            saveButton.setForeground(UIConstants.Colors.PURE_WHITE);
            saveButton.setFont(UIConstants.Fonts.BUTTON_FONT);
            saveButton.setFocusPainted(false);
            saveButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

            saveButton.addActionListener(e -> saveReport(report.toString()));
            buttonPanel.add(saveButton);
            reportPanel.add(buttonPanel, BorderLayout.SOUTH);

            JOptionPane.showMessageDialog(this, reportPanel,
                    "Sales Report - SellOut EasyTrack",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            showErrorDialog("Failed to generate report: " + e.getMessage());
        }
    }

    private void saveReport(String reportContent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Report");
        chooser.setSelectedFile(new File("SalesReport_" +
                java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy")) + ".txt"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter writer = new FileWriter(chooser.getSelectedFile())) {
                writer.write(reportContent);
                JOptionPane.showMessageDialog(this,
                        "Report saved successfully!\n\nFile: " + chooser.getSelectedFile().getAbsolutePath(),
                        "Report Saved",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                showErrorDialog("Failed to save report: " + e.getMessage());
            }
        }
    }

    private void exitApplication() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit SellOut EasyTrack?",
                "Exit Application",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            // Cleanup resources if needed
            System.out.println("Closing SellOut EasyTrack...");
            System.exit(0);
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}