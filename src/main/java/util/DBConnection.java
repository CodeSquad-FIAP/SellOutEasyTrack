package util;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final Dotenv dotenv = Dotenv.load();

    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    /**
     * Obtém uma conexão com o banco de dados
     * @return Connection objeto de conexão
     * @throws SQLException se houver erro na conexão
     */
    public static Connection getConnection() throws SQLException {
        if (URL == null || USER == null || PASSWORD == null) {
            throw new SQLException("As variáveis de ambiente DB_URL, DB_USER, ou DB_PASSWORD não foram encontradas no arquivo .env");
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
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
                System.out.println("3. Usuário/senha estão corretos no .env?");
                System.out.println("4. Execute o script setup_database.sql");
            }

        } catch (Exception e) {
            System.out.println("❌ Erro durante o teste: " + e.getMessage());
            System.out.println("\nDicas de solução:");
            System.out.println("- Verifique se o MySQL está rodando");
            System.out.println("- Verifique as credenciais no seu arquivo .env");
            System.out.println("- Verifique a porta 3306");
        }

        System.out.println("\n=== FIM DO TESTE ===");
    }
}