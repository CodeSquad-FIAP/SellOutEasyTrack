package com.sellout.service.impl;

import com.sellout.model.Sale;
import com.sellout.model.SalesStatistics;
import com.sellout.repository.SaleRepository;
import com.sellout.service.SalesService;
import com.sellout.service.validator.SaleValidator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public class SalesServiceImpl implements SalesService {
    private final SaleRepository saleRepository;
    private final SaleValidator saleValidator;

    public SalesServiceImpl(SaleRepository saleRepository, SaleValidator saleValidator) {
        this.saleRepository = saleRepository;
        this.saleValidator = saleValidator;
    }

    @Override
    public void createSale(Sale sale) {
        saleValidator.validateForCreation(sale);
        saleRepository.save(sale);
    }

    @Override
    public void updateSale(Sale sale) {
        saleValidator.validateForUpdate(sale);

        if (!saleRepository.findById(sale.getId()).isPresent()) {
            throw new IllegalArgumentException("Sale not found with ID: " + sale.getId());
        }

        saleRepository.update(sale);
    }

    @Override
    public void deleteSale(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID must be greater than zero");
        }

        if (!saleRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("Sale not found with ID: " + id);
        }

        saleRepository.deleteById(id);
    }

    @Override
    public Sale findSaleById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID must be greater than zero");
        }

        return saleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sale not found with ID: " + id));
    }

    @Override
    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    @Override
    public List<Sale> findSalesByProduct(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required for search");
        }

        return saleRepository.findByProductName(productName.trim());
    }

    @Override
    public SalesStatistics getStatistics() {
        int totalCount = saleRepository.countAll();
        BigDecimal totalRevenue = saleRepository.calculateTotalRevenue();

        BigDecimal averageTicket = BigDecimal.ZERO;
        if (totalCount > 0) {
            averageTicket = totalRevenue.divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP);
        }

        Map<String, Integer> topProducts = saleRepository.findTopProductsByQuantity();

        return new SalesStatistics(totalCount, totalRevenue, averageTicket, topProducts);
    }

    @Override
    public Map<String, Integer> getProductQuantitiesForChart() {
        return saleRepository.findProductQuantities();
    }
}
