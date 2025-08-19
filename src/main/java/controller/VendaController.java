package controller;

import dao.VendaDAO;
import model.Venda;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class VendaController {

    private VendaDAO vendaDAO = new VendaDAO();

    // CREATE - Salvar nova venda
    public void salvarVenda(Venda venda) throws SQLException {
        if (venda == null) {
            throw new IllegalArgumentException("Venda não pode ser nula");
        }

        // Validações de negócio
        if (venda.getProduto() == null || venda.getProduto().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório");
        }

        if (venda.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        if (venda.getValorUnitario() <= 0) {
            throw new IllegalArgumentException("Valor unitário deve ser maior que zero");
        }

        vendaDAO.inserirVenda(venda);
    }

    // READ - Obter todas as vendas
    public List<Venda> obterTodasVendas() throws SQLException {
        return vendaDAO.listarVendas();
    }

    // READ - Buscar venda por ID
    public Venda buscarVendaPorId(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID deve ser maior que zero");
        }
        return vendaDAO.buscarVendaPorId(id);
    }

    // UPDATE - Atualizar venda existente
    public void atualizarVenda(Venda venda) throws SQLException {
        if (venda == null) {
            throw new IllegalArgumentException("Venda não pode ser nula");
        }

        if (venda.getId() <= 0) {
            throw new IllegalArgumentException("ID da venda deve ser maior que zero");
        }

        // Validações de negócio (mesmas do CREATE)
        if (venda.getProduto() == null || venda.getProduto().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório");
        }

        if (venda.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        if (venda.getValorUnitario() <= 0) {
            throw new IllegalArgumentException("Valor unitário deve ser maior que zero");
        }

        // Verificar se a venda existe antes de atualizar
        Venda vendaExistente = vendaDAO.buscarVendaPorId(venda.getId());
        if (vendaExistente == null) {
            throw new SQLException("Venda com ID " + venda.getId() + " não encontrada");
        }

        vendaDAO.atualizarVenda(venda);
    }

    // DELETE - Deletar venda por ID
    public void deletarVenda(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID deve ser maior que zero");
        }

        // Verificar se a venda existe antes de deletar
        Venda vendaExistente = vendaDAO.buscarVendaPorId(id);
        if (vendaExistente == null) {
            throw new SQLException("Venda com ID " + id + " não encontrada");
        }

        vendaDAO.deletarVenda(id);
    }

    // RELATÓRIOS E ESTATÍSTICAS
    public Map<String, Integer> obterProdutoMaisVendido() throws SQLException {
        return vendaDAO.produtoMaisVendido();
    }

    public Map<String, Integer> obterDadosParaGrafico() throws SQLException {
        return vendaDAO.obterDadosGrafico();
    }

    public int contarTotalVendas() throws SQLException {
        return vendaDAO.contarVendas();
    }

    public double calcularValorTotalVendas() throws SQLException {
        return vendaDAO.calcularValorTotalVendas();
    }

    public double calcularTicketMedio() throws SQLException {
        int totalVendas = contarTotalVendas();
        if (totalVendas == 0) {
            return 0.0;
        }
        return calcularValorTotalVendas() / totalVendas;
    }

    // Validação de dados de venda
    public void validarVenda(Venda venda) throws IllegalArgumentException {
        if (venda == null) {
            throw new IllegalArgumentException("Venda não pode ser nula");
        }

        if (venda.getProduto() == null || venda.getProduto().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório");
        }

        if (venda.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        if (venda.getValorUnitario() <= 0) {
            throw new IllegalArgumentException("Valor unitário deve ser maior que zero");
        }

        if (venda.getData() == null) {
            throw new IllegalArgumentException("Data da venda é obrigatória");
        }
    }

    // Método auxiliar para buscar vendas por produto
    public List<Venda> buscarVendasPorProduto(String produto) throws SQLException {
        if (produto == null || produto.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório para busca");
        }

        return vendaDAO.listarVendas().stream()
                .filter(v -> v.getProduto().toLowerCase().contains(produto.toLowerCase()))
                .collect(java.util.stream.Collectors.toList());
    }
}