# Script R simplificado para gerar gráfico de vendas
cat('Iniciando script R...\n')

library(ggplot2)

# Ler dados
dados <- read.csv('temp_vendas_data.csv', stringsAsFactors = FALSE)
cat(paste('Dados lidos:', nrow(dados), 'linhas\n'))

# Ordenar dados
dados <- dados[order(-dados$Quantidade), ]
if(nrow(dados) > 10) dados <- dados[1:10, ]

# Criar gráfico vertical em 1080p
cat('Criando gráfico 1080p...\n')
grafico <- ggplot(dados, aes(x = reorder(Produto, Quantidade), y = Quantidade)) +
  geom_col(fill = '#3498db', color = '#2980b9', linewidth = 1.0) +
  geom_text(aes(label = Quantidade), vjust = -0.5, size = 5, fontface = 'bold', color = '#2c3e50') +
  labs(
    title = 'Produtos Mais Vendidos',
    subtitle = 'SellOut EasyTrack - Sistema de Vendas',
    x = 'Produtos',
    y = 'Quantidade Vendida',
    caption = 'Gerado automaticamente'
  ) +
  scale_y_continuous(expand = c(0, 0, 0.1, 0)) +
  theme_minimal(base_size = 14) +
  theme(
    plot.title = element_text(size = 20, face = 'bold', hjust = 0.5, color = '#2c3e50', margin = margin(b = 8)),
    plot.subtitle = element_text(size = 14, hjust = 0.5, color = '#7f8c8d', margin = margin(b = 15)),
    plot.caption = element_text(size = 10, color = '#95a5a6', hjust = 1),
    axis.title.x = element_text(size = 16, face = 'bold', color = '#34495e', margin = margin(t = 10)),
    axis.title.y = element_text(size = 16, face = 'bold', color = '#34495e', margin = margin(r = 10)),
    axis.text.x = element_text(size = 12, angle = 45, hjust = 1, color = '#2c3e50'),
    axis.text.y = element_text(size = 12, color = '#2c3e50'),
    panel.grid.minor = element_blank(),
    panel.grid.major.x = element_blank(),
    panel.grid.major.y = element_line(color = '#ecf0f1', linewidth = 0.6),
    plot.background = element_rect(fill = 'white', color = NA),
    panel.background = element_rect(fill = 'white', color = NA),
    plot.margin = margin(20, 20, 20, 20)
  )

# Salvar gráfico em 1080p Full HD
cat('Salvando gráfico em 1080p...\n')
ggsave(
  filename = 'vendas_grafico.png',
  plot = grafico,
  width = 12,
  height = 9,
  dpi = 400,
  units = 'in',
  device = 'png',
  type = 'cairo-png'
)

# Verificar resultado
if(file.exists('vendas_grafico.png')) {
  cat('Grafico salvo com sucesso!\n')
} else {
  stop('Erro ao salvar grafico')
}

cat('Script concluido!\n')
