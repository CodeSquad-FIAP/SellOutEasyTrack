# SellOut EasyTrack




## 🚀 Sobre o Projeto

**SellOut EasyTrack** é uma aplicação desktop robusta desenvolvida em Java, projetada para otimizar a gestão de vendas. Com uma interface intuitiva e moderna, construída com FlatLaf, o sistema permite o controle completo do ciclo de vida das vendas, desde o registro até a análise detalhada. Ele se integra a um banco de dados MySQL para persistência de dados e utiliza a linguagem R para gerar visualizações gráficas dinâmicas, oferecendo insights valiosos sobre o desempenho de vendas.

Este projeto é ideal para pequenas e médias empresas que buscam uma solução eficiente para monitorar e analisar suas operações de vendas.




## ✨ Funcionalidades Principais

- **Gestão Completa de Vendas:** Crie, visualize, atualize e exclua registros de vendas de forma eficiente.
- **Dashboard Interativo:** Obtenha uma visão geral do desempenho de vendas com gráficos e métricas gerados dinamicamente através da integração com R.
- **Relatórios Detalhados:** Gere relatórios abrangentes para análises aprofundadas e tomada de decisões estratégicas.
- **Persistência de Dados:** Todos os dados são armazenados de forma segura em um banco de dados MySQL.
- **Interface de Usuário Moderna:** Desfrute de uma experiência de usuário agradável e intuitiva, graças à biblioteca FlatLaf.




## 🛠️ Tecnologias e Ferramentas

O projeto SellOut EasyTrack é construído com um conjunto robusto de tecnologias para garantir performance, escalabilidade e uma ótima experiência de usuário:

-   **Linguagem de Programação:** Java 11
-   **Gerenciamento de Dependências:** Apache Maven
-   **Banco de Dados:** MySQL (com `mysql-connector-j` para conexão)
-   **Interface Gráfica (UI):** FlatLaf (para um design moderno e plano)
-   **Testes:** JUnit 5
-   **Análise de Dados e Gráficos:** Integração com a linguagem R para visualizações de dados.




## ⚙️ Como Rodar o Projeto

Para colocar o SellOut EasyTrack em funcionamento em sua máquina, siga os passos abaixo:

### Pré-requisitos

Certifique-se de ter os seguintes softwares instalados e configurados:

-   **Java Development Kit (JDK) 11 ou superior:** Essencial para compilar e executar a aplicação Java.
-   **Apache Maven:** Utilizado para gerenciar as dependências do projeto e o processo de build.
-   **Servidor MySQL:** O banco de dados onde as informações de vendas serão armazenadas. Você pode usar o MySQL Community Server, XAMPP, WAMP, MAMP ou Docker.
-   **R (Linguagem de Programação Estatística):** Necessário para a geração dos gráficos e relatórios. Baixe em [https://cran.r-project.org/](https://cran.r-project.org/).
-   **RStudio (Opcional, mas recomendado):** Facilita a instalação de pacotes R e a execução de scripts. Baixe em [https://posit.co/downloads/](https://posit.co/downloads/).

### Configuração do Banco de Dados

1.  **Execute o Script SQL:**
    O projeto inclui um script SQL completo para configurar o banco de dados. Abra seu cliente MySQL (MySQL Workbench, DBeaver, linha de comando, etc.) e execute o conteúdo do arquivo `SellOutEasyTrack_SQL.sql` (que foi fornecido como `pasted_content.txt`):
    ```sql
    -- Conteúdo do arquivo pasted_content.txt (renomeie para SellOutEasyTrack_SQL.sql)
    -- ===============================================
    -- SCRIPT COMPLETO DE CONFIGURAÇÃO DO MYSQL
    -- SellOut EasyTrack - Versão 2.0
    -- ===============================================

    -- Remove database se já existir (cuidado em produção!)
    DROP DATABASE IF EXISTS SellOutEasyTrack_SQL;

    -- Cria o database com configurações otimizadas
    CREATE DATABASE SellOutEasyTrack_SQL 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

    -- Usar o database
    USE SellOutEasyTrack_SQL;

    -- ===============================================
    -- CRIAÇÃO DA TABELA VENDAS (OTIMIZADA)
    -- ===============================================

    CREATE TABLE vendas (
        id INT AUTO_INCREMENT PRIMARY KEY,
        produto VARCHAR(100) NOT NULL,
        quantidade INT NOT NULL CHECK (quantidade > 0),
        valor_unitario DECIMAL(10,2) NOT NULL CHECK (valor_unitario > 0),
        data_venda DATE NOT NULL DEFAULT (CURRENT_DATE),
        data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        
        -- Índices para melhor performance
        INDEX idx_produto (produto),
        INDEX idx_data_venda (data_venda),
        INDEX idx_produto_data (produto, data_venda)
    ) ENGINE=InnoDB 
      CHARACTER SET utf8mb4 
      COLLATE utf8mb4_unicode_ci
      COMMENT=\'Tabela de vendas do sistema SellOut EasyTrack\';

    -- ===============================================
    -- CRIAÇÃO DE USUÁRIO ESPECÍFICO (RECOMENDADO)
    -- ===============================================

    -- Remove usuário se já existir
    DROP USER IF EXISTS \'sellout_user\'@\'localhost\';

    -- Cria usuário específico para a aplicação
    CREATE USER \'sellout_user\'@\'localhost\' IDENTIFIED BY \'SellOut123!\';

    -- Concede privilégios específicos
    GRANT SELECT, INSERT, UPDATE, DELETE ON SellOutEasyTrack_SQL.* TO \'sellout_user\'@\'localhost\';

    -- Aplica as mudanças
    FLUSH PRIVILEGES;

    -- ===============================================
    -- DADOS DE EXEMPLO PARA TESTE
    -- ===============================================

    INSERT INTO vendas (produto, quantidade, valor_unitario, data_venda) VALUES
    (\'Notebook Dell\', 2, 2500.00, \'2024-01-15\'),
    (\'Mouse Logitech\', 5, 85.50, \'2024-01-16\'),
    (\'Teclado Mecânico\', 3, 320.00, \'2024-01-17\'),
    (\'Monitor 24\"\', 1, 899.99, \'2024-01-18\'),
    (\'Smartphone Samsung\', 4, 1200.00, \'2024-01-19\'),
    (\'Tablet Apple\', 2, 2800.00, \'2024-01-20\'),
    (\'Fone Bluetooth\', 8, 150.00, \'2024-01-21\'),
    (\'Carregador USB-C\', 10, 45.90, \'2024-01-22\'),
    (\'SSD 1TB\', 3, 480.00, \'2024-01-23\'),
    (\'Webcam HD\', 6, 220.00, \'2024-01-24\');

    -- ===============================================
    -- VIEWS ÚTEIS PARA RELATÓRIOS
    -- ===============================================

    -- View para produtos mais vendidos
    CREATE VIEW vw_produtos_mais_vendidos AS
    SELECT 
        produto,
        SUM(quantidade) as total_quantidade,
        SUM(quantidade * valor_unitario) as total_vendas,
        AVG(valor_unitario) as preco_medio,
        COUNT(*) as numero_vendas
    FROM vendas 
    GROUP BY produto 
    ORDER BY total_quantidade DESC;

    -- View para vendas mensais
    CREATE VIEW vw_vendas_mensais AS
    SELECT 
        YEAR(data_venda) as ano,
        MONTH(data_venda) as mes,
        MONTHNAME(data_venda) as nome_mes,
        COUNT(*) as total_vendas,
        SUM(quantidade) as total_produtos,
        SUM(quantidade * valor_unitario) as faturamento
    FROM vendas 
    GROUP BY YEAR(data_venda), MONTH(data_venda)
    ORDER BY ano DESC, mes DESC;

    -- ===============================================
    -- PROCEDURES ÚTEIS
    -- ===============================================

    DELIMITER //

    -- Procedure para calcular estatísticas gerais
    CREATE PROCEDURE sp_estatisticas_gerais()
    BEGIN
        SELECT 
            COUNT(*) as total_vendas,
            SUM(quantidade) as total_produtos_vendidos,
            SUM(quantidade * valor_unitario) as faturamento_total,
            AVG(quantidade * valor_unitario) as ticket_medio,
            MIN(data_venda) as primeira_venda,
            MAX(data_venda) as ultima_venda
        FROM vendas;
    END //

    -- Procedure para limpeza de dados antigos (se necessário)
    CREATE PROCEDURE sp_limpar_vendas_antigas(IN dias_antigos INT)
    BEGIN
        DELETE FROM vendas 
        WHERE data_venda < DATE_SUB(CURDATE(), INTERVAL dias_antigos DAY);
        
        SELECT ROW_COUNT() as registros_removidos;
    END //

    DELIMITER ;

    -- ===============================================
    -- VERIFICAÇÕES E TESTES
    -- ===============================================

    -- Verificar se a tabela foi criada corretamente
    DESCRIBE vendas;

    -- Verificar dados inseridos
    SELECT COUNT(*) as total_registros FROM vendas;

    -- Testar view de produtos mais vendidos
    SELECT * FROM vw_produtos_mais_vendidos LIMIT 5;

    -- Testar procedure de estatísticas
    CALL sp_estatisticas_gerais();

    -- ===============================================
    -- INFORMAÇÕES DE CONEXÃO PARA O JAVA
    -- ===============================================

    /*
    CONFIGURAÇÃO PARA DBConnection.java:

    private static final String URL = "jdbc:mysql://localhost:3306/SellOutEasyTrack_SQL?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo";
    private static final String USER = "sellout_user";
    private static final String PASSWORD = "SellOut123!";

    ALTERNATIVA (usando root):
    private static final String USER = "root";
    private static final String PASSWORD = "sua_senha_root";
    */

    -- ===============================================
    -- COMANDOS DE VERIFICAÇÃO PARA TROUBLESHOOTING
    -- ===============================================

    -- Verificar usuários criados
    SELECT User, Host FROM mysql.user WHERE User IN (\'sellout_user\', \'root\');

    -- Verificar privilégios do usuário
    SHOW GRANTS FOR \'sellout_user\'@\'localhost\';

    -- Verificar status da conexão
    SHOW STATUS LIKE \'Connections\';

    -- Verificar configurações do servidor
    SHOW VARIABLES LIKE \'port\';
    SHOW VARIABLES LIKE \'socket\';

    -- ===============================================
    -- SCRIPT CONCLUÍDO COM SUCESSO!
    -- ===============================================

    SELECT \'DATABASE CONFIGURADO COM SUCESSO!\' as status,
           \'sellout_user criado\' as usuario,
           \'Dados de exemplo inseridos\' as dados,
           \'Views e procedures criadas\' as recursos;
    ```
    Este script irá criar o banco de dados `SellOutEasyTrack_SQL`, a tabela `vendas`, um usuário `sellout_user` com a senha `SellOut123!` e conceder as permissões necessárias. Ele também insere dados de exemplo e cria views e procedures úteis.

2.  **Configure as Credenciais do Banco de Dados no Java:**
    Edite o arquivo `src/main/java/util/DBConnection.java` para refletir as credenciais do banco de dados configuradas pelo script SQL. Utilize as informações fornecidas no final do script SQL:
    ```java
    private static final String URL = "jdbc:mysql://localhost:3306/SellOutEasyTrack_SQL?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo";
    private static final String USER = "sellout_user";
    private static final String PASSWORD = "SellOut123!";
    ```
    **Importante:** Se você optou por usar o usuário `root` (não recomendado para produção), ajuste `USER` e `PASSWORD` conforme sua configuração.

### Configuração do R

1.  **Instale os Pacotes R Necessários:**
    O projeto utiliza a linguagem R para gerar gráficos. É crucial que os pacotes R necessários estejam instalados em seu ambiente R. Abra o R ou RStudio e execute o seguinte comando para instalar os pacotes essenciais:
    ```R
    install.packages("ggplot2")
    install.packages("dplyr")
    install.packages("jsonlite")
    # Verifique os scripts R (temp_graph_script.R, teste_grafico.R) para quaisquer outros pacotes específicos.
    ```
    Você também pode executar o script `install-r-script.r` (se presente no projeto) para garantir que todas as dependências R sejam instaladas automaticamente.

### Compilação e Execução

1.  **Navegue até o Diretório do Projeto:**
    Abra seu terminal ou prompt de comando e navegue até o diretório raiz do projeto `Asteria` (onde o arquivo `pom.xml` está localizado):
    ```bash
    cd /caminho/para/o/seu/projeto/Asteria
    ```

2.  **Compile o Projeto com Maven:**
    Execute o seguinte comando para compilar o projeto e baixar todas as dependências Java:
    ```bash
    mvn clean install
    ```

3.  **Execute a Aplicação:**
    Após a compilação bem-sucedida, você pode iniciar a aplicação principal:
    ```bash
    mvn exec:java -Dexec.mainClass="Main"
    ```
    Alternativamente, para criar um arquivo JAR executável e rodá-lo (útil para distribuição):
    ```bash
    mvn package
    java -jar target/SellOutEasy-1.0-SNAPSHOT-jar-with-dependencies.jar
    ```
    (O nome exato do arquivo JAR pode variar ligeiramente dependendo da versão e do `artifactId` configurado no `pom.xml`)

Com esses passos, a aplicação Asteria estará pronta para ser utilizada em seu ambiente local.




## 🤝 Autores

| Nome             | LinkedIn |
|------------------|----------|
| Yasmin Kimura    | [LinkedIn](https://www.linkedin.com/in/yasmin-kimura-b374b72b7/) |
| André Flores     | [LinkedIn](https://www.linkedin.com/in/andréflores/) |
| Roger Alencar    | [LinkedIn](https://www.linkedin.com/in/roger-alencar-it/) |
| **Kevin Benevides** | [LinkedIn](https://www.linkedin.com/in/kevinbenevidesdasilva/) |
| Arthur Corrêa | [LinkedIn](https://www.linkedin.com/in/arthurceicorrea/) |


