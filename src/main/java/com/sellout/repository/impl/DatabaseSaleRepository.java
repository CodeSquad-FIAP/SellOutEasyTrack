// src/main/java/com/sellout/repository/SaleRepository.java
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

// src/main/java/com/sellout/repository/impl/DatabaseSaleRepository.java
package com.sellout.repository.impl;

import com.sellout.config.DatabaseConnection;
import com.sellout.model.Sale;
import com.sellout.repository.SaleRepository;

import java.math.BigDecimal;
import java.sql.*;
        import java.time.LocalDate;
import java.util.*;

public class DatabaseSaleRepository implements SaleRepository {
    private final DatabaseConnection databaseConnection;

    public DatabaseSaleRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public void save(Sale sale) {
        final String sql = "INSERT INTO vendas (produto, quantidade, valor_unitario, data_venda) VALUES (?, ?, ?, ?)";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setStatementParameters(stmt, sale);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save sale", e);
        }
    }

    @Override
    public void update(Sale sale) {
        final String sql = "UPDATE vendas SET produto = ?, quantidade = ?, valor_unitario = ? WHERE id = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setStatementParameters(stmt, sale);
            stmt.setLong(4, sale.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Sale not found with ID: " + sale.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update sale", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        final String sql = "DELETE FROM vendas WHERE id = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Sale not found with ID: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete sale", e);
        }
    }

    @Override
    public Optional<Sale> findById(Long id) {
        final String sql = "SELECT * FROM vendas WHERE id = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSale(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find sale by ID", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Sale> findAll() {
        final String sql = "SELECT * FROM vendas ORDER BY id DESC";
        final List<Sale> sales = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                sales.add(mapResultSetToSale(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all sales", e);
        }

        return sales;
    }

    @Override
    public List<Sale> findByProductName(String productName) {
        final String sql = "SELECT * FROM vendas WHERE LOWER(produto) LIKE LOWER(?) ORDER BY id DESC";
        final List<Sale> sales = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + productName + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sales.add(mapResultSetToSale(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find sales by product name", e);
        }

        return sales;
    }

    @Override
    public int countAll() {
        final String sql = "SELECT COUNT(*) as total FROM vendas";

        try (Connection conn = databaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count sales", e);
        }

        return 0;
    }

    @Override
    public BigDecimal calculateTotalRevenue() {
        final String sql = "SELECT SUM(quantidade * valor_unitario) as total FROM vendas";

        try (Connection conn = databaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                BigDecimal result = rs.getBigDecimal("total");
                return result != null ? result : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to calculate total revenue", e);
        }

        return BigDecimal.ZERO;
    }

    @Override
    public Map<String, Integer> findTopProductsByQuantity() {
        final String sql = "SELECT produto, SUM(quantidade) as total FROM vendas GROUP BY produto ORDER BY total DESC LIMIT 1";
        final Map<String, Integer> result = new HashMap<>();

        try (Connection conn = databaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.put(rs.getString("produto"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find top products", e);
        }

        return result;
    }

    @Override
    public Map<String, Integer> findProductQuantities() {
        final String sql = "SELECT produto, SUM(quantidade) as total FROM vendas GROUP BY produto ORDER BY total DESC";
        final Map<String, Integer> result = new LinkedHashMap<>();

        try (Connection conn = databaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.put(rs.getString("produto"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find product quantities", e);
        }

        return result;
    }

    private void setStatementParameters(PreparedStatement stmt, Sale sale) throws SQLException {
        stmt.setString(1, sale.getProductName());
        stmt.setInt(2, sale.getQuantity());
        stmt.setBigDecimal(3, sale.getUnitPrice());
        stmt.setDate(4, java.sql.Date.valueOf(sale.getSaleDate()));
    }

    private Sale mapResultSetToSale(ResultSet rs) throws SQLException {
        return new Sale(
                rs.getLong("id"),
                rs.getString("produto"),
                rs.getInt("quantidade"),
                rs.getBigDecimal("valor_unitario"),
                rs.getDate("data_venda").toLocalDate()
        );
    }
}