package util;

import model.Venda;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReportUtil {
    public static void exportarCSV(List<Venda> vendas, String path) throws IOException {
        FileWriter writer = new FileWriter(path);
        writer.write("Produto,Quantidade,Valor Unitário,Data\n");
        for (Venda v : vendas) {
            writer.write(v.getProduto() + "," + v.getQuantidade() + "," + v.getValorUnitario() + "," + v.getData() + "\n");
        }
        writer.close();
    }
}
