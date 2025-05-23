package controller;

import dao.VendaDAO;
import model.Venda;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class VendaController {

    private VendaDAO vendaDAO = new VendaDAO();

    public void salvarVenda(Venda venda) throws SQLException {
        vendaDAO.inserirVenda(venda);
    }

    public List<Venda> obterTodasVendas() throws SQLException {
        return vendaDAO.listarVendas();
    }

    public Map<String, Integer> obterProdutoMaisVendido() throws SQLException {
        return vendaDAO.produtoMaisVendido();
    }
}