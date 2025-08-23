package com.sellout.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Sale {
    private Long id;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private LocalDate saleDate;

    public Sale() {}

    public Sale(String productName, Integer quantity, BigDecimal unitPrice, LocalDate saleDate) {
        this.productName = validateProductName(productName);
        this.quantity = validateQuantity(quantity);
        this.unitPrice = validateUnitPrice(unitPrice);
        this.saleDate = validateSaleDate(saleDate);
    }

    public Sale(Long id, String productName, Integer quantity, BigDecimal unitPrice, LocalDate saleDate) {
        this(productName, quantity, unitPrice, saleDate);
        this.id = id;
    }

    public BigDecimal getTotalValue() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public boolean isValidForPersistence() {
        return productName != null && !productName.trim().isEmpty()
                && quantity != null && quantity > 0
                && unitPrice != null && unitPrice.compareTo(BigDecimal.ZERO) > 0
                && saleDate != null;
    }

    private String validateProductName(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        return productName.trim();
    }

    private Integer validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        return quantity;
    }

    private BigDecimal validateUnitPrice(BigDecimal unitPrice) {
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Unit price must be greater than zero");
        }
        return unitPrice;
    }

    private LocalDate validateSaleDate(LocalDate saleDate) {
        if (saleDate == null) {
            throw new IllegalArgumentException("Sale date cannot be null");
        }
        return saleDate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = validateProductName(productName); }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = validateQuantity(quantity); }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = validateUnitPrice(unitPrice); }

    public LocalDate getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDate saleDate) { this.saleDate = validateSaleDate(saleDate); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sale sale = (Sale) o;
        return Objects.equals(id, sale.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Sale{id=%d, product='%s', quantity=%d, unitPrice=%s, date=%s}",
                id, productName, quantity, unitPrice, saleDate);
    }
}