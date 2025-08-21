# Script para instalar dependências necessárias do R
# Execute este script antes de usar o sistema

# Função para verificar e instalar pacotes
install_if_missing <- function(package_name) {
  if (!require(package_name, character.only = TRUE)) {
    cat(paste("Instalando pacote:", package_name, "\n"))
    install.packages(package_name, dependencies = TRUE)
    library(package_name, character.only = TRUE)
  } else {
    cat(paste("Pacote", package_name, "já está instalado.\n"))
  }
}

# Lista de pacotes necessários
packages <- c("ggplot2", "dplyr", "scales")

cat("=== Instalação de Dependências do R para SellOut EasyTrack ===\n\n")

# Instalar cada pacote
for (package in packages) {
  install_if_missing(package)
}

cat("\n=== Instalação Concluída! ===\n")
cat("Todos os pacotes necessários foram instalados com sucesso.\n")
cat("O sistema Java agora pode gerar gráficos usando R.\n\n")

# Teste básico
cat("Realizando teste básico...\n")
test_data <- data.frame(
  produto = c("Produto A", "Produto B", "Produto C"),
  quantidade = c(10, 15, 8)
)

test_plot <- ggplot(test_data, aes(x = produto, y = quantidade)) +
  geom_bar(stat = "identity", fill = "steelblue") +
  labs(title = "Teste de Instalação", x = "Produto", y = "Quantidade") +
  theme_minimal()

ggsave("teste_instalacao.png", plot = test_plot, width = 8, height = 6, dpi = 300)

if (file.exists("teste_instalacao.png")) {
  cat("✅ Teste bem-sucedido! Arquivo 'teste_instalacao.png' foi criado.\n")
  file.remove("teste_instalacao.png")
  cat("Arquivo de teste removido.\n")
} else {
  cat("❌ Erro no teste. Verifique a instalação.\n")
}

cat("\n=== Pronto para uso! ===\n")