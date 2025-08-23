package com.sellout.ui.component;

import com.sellout.config.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class StyledTable extends JTable {

    public StyledTable(String[] columnNames) {
        super(new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        setupTableStyle();
    }

    private void setupTableStyle() {
        setRowHeight(30);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setBackground(UIConstants.Colors.FIAP_GRAY_DARK);
        setForeground(UIConstants.Colors.PURE_WHITE);
        setGridColor(UIConstants.Colors.FIAP_GRAY_MEDIUM);
        setSelectionBackground(UIConstants.Colors.FIAP_PINK_DARK);
        setSelectionForeground(UIConstants.Colors.PURE_WHITE);
        setFont(UIConstants.Fonts.TEXT_FIELD_FONT);

        JTableHeader header = getTableHeader();
        header.setBackground(UIConstants.Colors.FIAP_BLACK_TECH);
        header.setForeground(UIConstants.Colors.PURE_WHITE);
        header.setFont(UIConstants.Fonts.LABEL_FONT);
        header.setBorder(BorderFactory.createLineBorder(UIConstants.Colors.FIAP_GRAY_DARK));
    }

    public DefaultTableModel getTableModel() {
        return (DefaultTableModel) getModel();
    }

    public void clearData() {
        getTableModel().setRowCount(0);
    }

    public void addRow(Object[] rowData) {
        getTableModel().addRow(rowData);
    }
}