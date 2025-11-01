-- Cria o banco de dados (o nome é pego do .env, mas garantimos)
-- O docker-compose já cria o banco, mas podemos usar este comando por segurança.
-- CREATE DATABASE IF NOT EXISTS SellOutEasyTrack_SQL; 

-- Seleciona o banco de dados
USE SellOutEasyTrack_SQL;

-- 
-- !! ATENÇÃO !!
-- SUBSTITUA A ESTRUTURA DE TABELA ABAIXO PELA ESTRUTURA CORRETA DO SEU PROJETO
-- (Baseado em Venda.java e VendaDAO.java)
--
CREATE TABLE IF NOT EXISTS vendas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    produto VARCHAR(255) NOT NULL,
    valor DECIMAL(10, 2) NOT NULL,
    data_venda DATE NOT NULL,
    cliente VARCHAR(255),
    metodo_pagamento VARCHAR(50),
    status_venda VARCHAR(50),
    origem VARCHAR(100)
);

-- Você pode adicionar dados de exemplo aqui, se desejar
-- INSERT INTO vendas (produto, valor, data_venda, cliente, metodo_pagamento, status_venda, origem) 
-- VALUES 
-- ('Produto Exemplo 1', 99.90, '2025-01-10', 'Cliente A', 'Cartão de Crédito', 'Concluída', 'Loja Física'),
-- ('Produto Exemplo 2', 49.50, '2025-01-11', 'Cliente B', 'Pix', 'Pendente', 'Online');
