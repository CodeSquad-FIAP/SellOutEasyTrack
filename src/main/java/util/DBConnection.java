package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe para gerenciamento de conexão com o banco de dados MySQL
 * SellOut EasyTrack - Versão 2.0
 */
public class DBConnection {

    // ===============================================
    // CONFIGURAÇÕES DE CONEXÃO
    // ===============================================

    // URL com parâmetros otimizados para o projeto
    private static final String URL = "jdbc:mysql://localhost:3306/SellOutEasyTrack_SQL?" +
            "useSSL=false&" +
            "allowPublicKeyRetrieval=true&" +
            "serverTimezone=America/Sao_Paulo&" +
            "useUnicode=true&" +
            "characterEncoding=UTF-8";

    // OPÇÃO 1: Usuário específico criado (RECOMENDADO)
    private static final String USER = "root";
    private static final String PASSWORD = "SenhaForte123#";

    // OPÇÃO 2: Se preferir usar root (descomente as linhas abaixo e comente as de cima)
    // private static final String USER = "root";
    // private static final String PASSWORD = ""; // Sua senha do root aqui

    // ===============================================
    // CONFIGURAÇÕES DE POOL DE CONEXÕES
    // ===============================================

    private static final int MAX_CONNECTIONS = 10;
    private static final int CONNECTION_TIMEOUT = 30000; // 30 segundos

    // ===============================================
    // MÉTODOS DE CONEXÃO
    // ===============================================

    /**
     * Obtém uma conexão com o banco de dados
     * @return Connection objeto de conexão
     * @throws SQLException se houver erro na conexão
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Carrega o driver MySQL (necessário para versões antigas do Java)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Cria e retorna a conexão
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

            // Configurações otimizadas para a conexão
            connection.setAutoCommit(true);

            return connection;

        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL não encontrado: " + e.getMessage());
        } catch (SQLException e) {
            throw new SQLException("Erro ao conectar com o banco de dados: " + e.getMessage());
        }
    }

    /**
     * Testa a conexão com o banco de dados
     * @return true se a conexão for bem-sucedida
     */
    public static boolean testarConexao() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Erro ao testar conexão: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtém informações sobre a conexão atual
     * @return String com informações da conexão
     */
    public static String getConnectionInfo() {
        try (Connection conn = getConnection()) {
            return String.format(
                    "Conectado ao: %s\n" +
                            "Usuário: %s\n" +
                            "Driver: %s\n" +
                            "Versão do MySQL: %s",
                    conn.getMetaData().getURL(),
                    conn.getMetaData().getUserName(),
                    conn.getMetaData().getDriverName(),
                    conn.getMetaData().getDatabaseProductVersion()
            );
        } catch (SQLException e) {
            return "Erro ao obter informações da conexão: " + e.getMessage();
        }
    }

    /**
     * Fecha uma conexão de forma segura
     * @param connection conexão a ser fechada
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }

    // ===============================================
    // MÉTODO MAIN PARA TESTE
    // ===============================================

    /**
     * Método para testar a conexão
     */
    public static void main(String[] args) {
        System.out.println("=== TESTE DE CONEXÃO SellOut EasyTrack ===\n");

        try {
            System.out.println("Testando conexão...");

            if (testarConexao()) {
                System.out.println("✅ Conexão bem-sucedida!");
                System.out.println("\nInformações da conexão:");
                System.out.println(getConnectionInfo());

                // Teste adicional: executar query simples
                try (Connection conn = getConnection()) {
                    var stmt = conn.createStatement();
                    var rs = stmt.executeQuery("SELECT COUNT(*) as total FROM vendas");
                    if (rs.next()) {
                        System.out.println("\n📊 Total de vendas no banco: " + rs.getInt("total"));
                    }
                }

            } else {
                System.out.println("❌ Falha na conexão!");
                System.out.println("\nVerifique:");
                System.out.println("1. MySQL está rodando?");
                System.out.println("2. Banco SellOutEasyTrack_SQL existe?");
                System.out.println("3. Usuário/senha estão corretos?");
                System.out.println("4. Execute o script setup_database.sql");
            }

        } catch (Exception e) {
            System.out.println("❌ Erro durante o teste: " + e.getMessage());
            System.out.println("\nDicas de solução:");
            System.out.println("- Verifique se o MySQL está rodando");
            System.out.println("- Execute: mysql -u " + USER + " -p");
            System.out.println("- Verifique a porta 3306");
        }

        System.out.println("\n=== FIM DO TESTE ===");
    }
}