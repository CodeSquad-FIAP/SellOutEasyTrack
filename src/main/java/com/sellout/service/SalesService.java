package com.sellout.service;

import com.sellout.model.Sale;
import com.sellout.model.SalesStatistics;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface SalesService {
    void createSale(Sale sale);
    void updateSale(Sale sale);
    void deleteSale(Long id);
    Sale findSaleById(Long id);
    List<Sale> getAllSales();
    List<Sale> findSalesByProduct(String productName);
    SalesStatistics getStatistics();
    Map<String, Integer> getProductQuantitiesForChart();
}