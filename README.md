# 🚀 SellOut EasyTrack

Sistema de controle de vendas com interface moderna em Java, integração com MySQL e geração de relatórios gráficos interativos.

![Java](https://img.shields.io/badge/Java-11+-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Swing](https://img.shields.io/badge/Swing-UI-green?style=for-the-badge)
![MySQL](https://img.shields.io/badge/MySQL-00758F?style=for-the-badge&logo=mysql&logoColor=white)
![FlatLaf](https://img.shields.io/badge/FlatLaf-3.6-blue?style=for-the-badge)
![JFreeChart](https://img.shields.io/badge/JFreeChart-1.5.3-purple?style=for-the-badge)
![Maven](https://img.shields.io/badge/Maven-Automated-red?style=for-the-badge)

---

## 📋 Descrição

O **SellOut EasyTrack** é uma aplicação desktop corporativa desenvolvida em **Java Swing com FlatLaf**, utilizando arquitetura **MVC + DAO**, integração real com banco de dados **MySQL** via JDBC, e geração de relatórios com gráficos profissionais usando **JFreeChart**.

O sistema permite:

- Registro de vendas reais.
- Visualização de relatórios e gráficos de desempenho.
- Exportação de relatórios em **CSV**.
- Interface moderna, amigável e responsiva.

---

## 💻 Tecnologias Utilizadas

| Tecnologia        | Descrição                                         |
|-------------------|--------------------------------------------------|
| Java 11+          | Linguagem principal do projeto                   |
| Swing + FlatLaf   | Interface gráfica moderna e refinada                       |
| MySQL 8+          | Banco de dados relacional                        |
| JDBC              | Conexão Java com MySQL                         |
| JFreeChart        | Geração de gráficos dinâmicos e profissionais |
| Maven             | Gerenciamento automatizado de dependências      |

---

## 🎯 Funcionalidades

- ✅ Registro de vendas
- ✅ Dashboard moderno com cards e gráficos integrados
- ✅ Geração de relatórios gráficos em tela
- ✅ Exportação de dados para **CSV**  
- ✅ Conexão real com banco de dados MySQL
- ✅ Interface moderna com FlatLaf  

---

## 📁 Estrutura de Pastas

```
SellOutEasy/
├── src/
│   └── main/
│       ├── java/
│       │   ├── controller/
│       │   ├── dao/
│       │   ├── model/
│       │   ├── util/
│       │   └── view/
│       └── resources/
├── pom.xml
└── README.md
```

---

## 🛠️ Requisitos

- Java JDK 11 ou superior  
- MySQL ou MariaDB  
- Maven instalado  

---

## 🗃️ Configuração do Banco de Dados

### Script SQL

```sql
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
(\'Monitor 24"\', 1, 899.99, \'2024-01-18\'),
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

---

### 💡 Configuração da Conexão

No arquivo:

```
src/main/java/util/DBConnection.java
```

Edite as credenciais:

```java
private static final String URL = "jdbc:mysql://localhost:3306/SellOutEasyTrack_SQL?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo";
private static final String USER = "sellout_user";
private static final String PASSWORD = "SellOut123!";
```

---

## ▶️ Como Executar

1. Clone o repositório:

```bash
git clone https://github.com/kimurinhakikii/SellOutEasy.git
cd SellOutEasy
```

2. Compile o projeto com Maven:

```bash
mvn clean compile
```

3. Execute a aplicação:

```bash
mvn exec:java -Dexec.mainClass="Main"
```

---

## 📈 Relatórios e Gráficos

- **Gráfico em tela**: Exibe gráfico de barras com desempenho mensal.
- **Exportação CSV**: Exporta vendas registradas para arquivo ```.csv```

---

## 📚 Padrões de Projeto Utilizados

- **MVC** (Model-View-Controller)
- **DAO** (Data Access Object)
- **Singleton** (para conexão com o banco)
- **POO completa** (encapsulamento, herança, polimorfismo, coleções, tratamento de exceções)

---

## 📦 Bibliotecas (Gerenciadas via Maven)

| Biblioteca        | Versão |
|-------------------|--------|
| FlatLaf           | 3.2    |
| JFreeChart        | 1.5.3  |
| MySQL Connector/J | 8.0.33 |

---

## 📑 Licença

Projeto desenvolvido para fins acadêmicos e demonstração de conceitos **POO completa e arquitetura em camadas** com foco em arquitetura Java robusta, integração com banco de dados, e interface amigável com recursos modernos de visualização.

---

## 🤝 Autores

| Nome             | LinkedIn |
|------------------|----------|
| Yasmin Kimura    | [LinkedIn](https://www.linkedin.com/in/yasmin-kimura-b374b72b7/) |
| André Flores     | [LinkedIn](https://www.linkedin.com/in/andréflores/) |
| Roger Alencar    | [LinkedIn](https://www.linkedin.com/in/roger-alencar-it/) |
| **Kevin Benevides** | [LinkedIn](https://www.linkedin.com/in/kevinbenevidesdasilva/) |
| Arthur Corrêa | [LinkedIn](https://www.linkedin.com/in/arthurceicorrea/) | 
