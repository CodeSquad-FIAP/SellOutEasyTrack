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
        String sql = "SELECT * FROM vendas";
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
}
