package model;

import java.sql.Date;

public class Venda {
    private int id;
    private String produto;
    private int quantidade;
    private double valorUnitario;
    private Date data;

    public Venda(String produto, int quantidade, double valorUnitario, Date data) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
        this.data = data;
    }

    public Venda(int id, String produto, int quantidade, double valorUnitario, Date data) {
        this(produto, quantidade, valorUnitario, data);
        this.id = id;
    }

    public int getId() { return id; }
    public String getProduto() { return produto; }
    public int getQuantidade() { return quantidade; }
    public double getValorUnitario() { return valorUnitario; }
    public Date getData() { return data; }
}
