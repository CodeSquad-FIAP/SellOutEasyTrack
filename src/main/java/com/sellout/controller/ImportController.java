package com.sellout.controller;

import com.sellout.model.ImportResult;
import com.sellout.service.ImportService;
import com.sellout.ui.listener.SalesChangeListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImportController {
    private final ImportService importService;
    private final List<SalesChangeListener> listeners;

    public ImportController(ImportService importService) {
        this.importService = importService;
        this.listeners = new ArrayList<>();
    }

    public ImportResult importFromCsv(File csvFile) {
        try {
            ImportResult result = importService.importFromCsv(csvFile);
            if (result.isSuccess() && result.getSuccessCount() > 0) {
                notifyListeners();
            }
            return result;
        } catch (Exception e) {
            throw new ControllerException("CSV import failed: " + e.getMessage(), e);
        }
    }

    public ImportResult importFromText(File textFile) {
        try {
            ImportResult result = importService.importFromText(textFile);
            if (result.isSuccess() && result.getSuccessCount() > 0) {
                notifyListeners();
            }
            return result;
        } catch (Exception e) {
            throw new ControllerException("Text import failed: " + e.getMessage(), e);
        }
    }

    public ImportResult importFromWhatsApp(String whatsappText) {
        try {
            ImportResult result = importService.importFromWhatsApp(whatsappText);
            if (result.isSuccess() && result.getSuccessCount() > 0) {
                notifyListeners();
            }
            return result;
        } catch (Exception e) {
            throw new ControllerException("WhatsApp import failed: " + e.getMessage(), e);
        }
    }

    public void addSalesChangeListener(SalesChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    private void notifyListeners() {
        listeners.forEach(SalesChangeListener::onSalesChanged);
    }

    public static class ControllerException extends RuntimeException {
        public ControllerException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}