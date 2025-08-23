package com.sellout.controller;

import com.sellout.model.Sale;
import com.sellout.model.SalesStatistics;
import com.sellout.service.SalesService;
import com.sellout.ui.listener.SalesChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SalesController {
    private final SalesService salesService;
    private final List<SalesChangeListener> listeners;

    public SalesController(SalesService salesService) {
        this.salesService = salesService;
        this.listeners = new ArrayList<>();
    }

    public void createSale(Sale sale) {
        try {
            salesService.createSale(sale);
            notifyListeners();
        } catch (Exception e) {
            throw new ControllerException("Failed to create sale: " + e.getMessage(), e);
        }
    }

    public void updateSale(Sale sale) {
        try {
            salesService.updateSale(sale);
            notifyListeners();
        } catch (Exception e) {
            throw new ControllerException("Failed to update sale: " + e.getMessage(), e);
        }
    }

    public void deleteSale(Long id) {
        try {
            salesService.deleteSale(id);
            notifyListeners();
        } catch (Exception e) {
            throw new ControllerException("Failed to delete sale: " + e.getMessage(), e);
        }
    }

    public Sale findSaleById(Long id) {
        try {
            return salesService.findSaleById(id);
        } catch (Exception e) {
            throw new ControllerException("Failed to find sale: " + e.getMessage(), e);
        }
    }

    public List<Sale> getAllSales() {
        try {
            return salesService.getAllSales();
        } catch (Exception e) {
            throw new ControllerException("Failed to retrieve sales: " + e.getMessage(), e);
        }
    }

    public List<Sale> findSalesByProduct(String productName) {
        try {
            return salesService.findSalesByProduct(productName);
        } catch (Exception e) {
            throw new ControllerException("Failed to search sales: " + e.getMessage(), e);
        }
    }

    public SalesStatistics getStatistics() {
        try {
            return salesService.getStatistics();
        } catch (Exception e) {
            throw new ControllerException("Failed to get statistics: " + e.getMessage(), e);
        }
    }

    public Map<String, Integer> getChartData() {
        try {
            return salesService.getProductQuantitiesForChart();
        } catch (Exception e) {
            throw new ControllerException("Failed to get chart data: " + e.getMessage(), e);
        }
    }

    public void addSalesChangeListener(SalesChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removeSalesChangeListener(SalesChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        listeners.forEach(SalesChangeListener::onSalesChanged);
    }

    // Exception class for controller layer
    public static class ControllerException extends RuntimeException {
        public ControllerException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}