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
CREATE DATABASE SellOutEasyTrack_SQL;

USE SellOutEasyTrack_SQL;

CREATE TABLE vendas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    produto VARCHAR(100) NOT NULL,
    quantidade INT NOT NULL,
    valor_unitario DECIMAL(10,2) NOT NULL,
    data_venda DATE NOT NULL
);
```

---

### 💡 Configuração da Conexão

No arquivo:

```
src/main/java/util/DBConnection.java
```

Edite as credenciais:

```java
private static final String URL = "jdbc:mysql://localhost:3306/SellOutEasyTrack_SQL";
private static final String USER = "seu_usuario";
private static final String PASSWORD = "sua_senha";
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
