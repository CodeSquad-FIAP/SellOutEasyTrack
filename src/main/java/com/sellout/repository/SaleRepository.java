package com.sellout.repository;

import com.sellout.model.Sale;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SaleRepository {
    void save(Sale sale);
    void update(Sale sale);
    void deleteById(Long id);
    Optional<Sale> findById(Long id);
    List<Sale> findAll();
    List<Sale> findByProductName(String productName);
    int countAll();
    BigDecimal calculateTotalRevenue();
    Map<String, Integer> findTopProductsByQuantity();
    Map<String, Integer> findProductQuantities();
}