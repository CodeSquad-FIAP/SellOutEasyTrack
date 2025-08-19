package dao;

import model.Venda;
import util.DBConnection;

import java.sql.*;
import java.util.*;

public class VendaDAO {

    public void inserirVenda(Venda venda) throws SQLException {
        String sql = "INSERT INTO vendas (produto, quantidade, valor_unitario, data_venda) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, venda.getProduto());
            stmt.setInt(2, venda.getQuantidade());
            stmt.setDouble(3, venda.getValorUnitario());
            stmt.setDate(4, venda.getData());
            stmt.executeUpdate();
        }
    }

    public List<Venda> listarVendas() throws SQLException {
        List<Venda> lista = new ArrayList<>();
        String sql = "SELECT * FROM vendas ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Venda venda = new Venda(
                        rs.getInt("id"),
                        rs.getString("produto"),
                        rs.getInt("quantidade"),
                        rs.getDouble("valor_unitario"),
                        rs.getDate("data_venda")
                );
                lista.add(venda);
            }
        }
        return lista;
    }

    public Venda buscarVendaPorId(int id) throws SQLException {
        String sql = "SELECT * FROM vendas WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Venda(
                            rs.getInt("id"),
                            rs.getString("produto"),
                            rs.getInt("quantidade"),
                            rs.getDouble("valor_unitario"),
                            rs.getDate("data_venda")
                    );
                }
            }
        }
        return null;
    }

    public void atualizarVenda(Venda venda) throws SQLException {
        String sql = "UPDATE vendas SET produto = ?, quantidade = ?, valor_unitario = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, venda.getProduto());
            stmt.setInt(2, venda.getQuantidade());
            stmt.setDouble(3, venda.getValorUnitario());
            stmt.setInt(4, venda.getId());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("Nenhuma venda encontrada com o ID: " + venda.getId());
            }
        }
    }

    public void deletarVenda(int id) throws SQLException {
        String sql = "DELETE FROM vendas WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("Nenhuma venda encontrada com o ID: " + id);
            }
        }
    }

    public Map<String, Integer> produtoMaisVendido() throws SQLException {
        Map<String, Integer> resultado = new HashMap<>();
        String sql = "SELECT produto, SUM(quantidade) as total FROM vendas GROUP BY produto ORDER BY total DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                resultado.put(rs.getString("produto"), rs.getInt("total"));
            }
        }
        return resultado;
    }

    public Map<String, Integer> obterDadosGrafico() throws SQLException {
        Map<String, Integer> dados = new HashMap<>();
        String sql = "SELECT produto, SUM(quantidade) as total FROM vendas GROUP BY produto ORDER BY total DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                dados.put(rs.getString("produto"), rs.getInt("total"));
            }
        }
        return dados;
    }

    public int contarVendas() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM vendas";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    public double calcularValorTotalVendas() throws SQLException {
        String sql = "SELECT SUM(quantidade * valor_unitario) as total FROM vendas";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0.0;
    }
}