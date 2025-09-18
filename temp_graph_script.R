cat('Iniciando script R com paleta FIAP + Asteria...\n')

library(ggplot2)

# ===== PALETA DE CORES FIAP + ASTERIA =====
cores_fiap_asteria <- c(
  fiap_pink_vibrant = '#F23064',
  fiap_pink_dark = '#BF3B5E',
  asteria_midnight_blue = '#2C3E50',
  asteria_amethyst = '#8E44AD',
  asteria_ocean_blue = '#3498DB',
  success_emerald = '#2ECC71',
  warning_amber = '#F39C12',
  pure_white = '#FFFFFF',
  light_gray = '#ECF0F1',
  soft_charcoal = '#2C3E50'
)

# Paleta para séries de dados
cores_series <- c(
  '#F23064',
  '#3498DB',
  '#8E44AD',
  '#2ECC71',
  '#F39C12',
  '#BF3B5E',
  '#2C3E50',
  '#404040'
)

dados <- read.csv('temp_vendas_data.csv', stringsAsFactors = FALSE)
cat(paste('Dados lidos:', nrow(dados), 'linhas\n'))

dados <- dados[order(-dados$Quantidade), ]
if(nrow(dados) > 10) dados <- dados[1:10, ]

# Atribuir cores dinamicamente baseado no número de produtos
num_produtos <- nrow(dados)
cores_usadas <- cores_series[1:min(num_produtos, length(cores_series))]
if(num_produtos > length(cores_series)) {
  cores_extras <- rep(cores_series, ceiling(num_produtos / length(cores_series)))
  cores_usadas <- cores_extras[1:num_produtos]
}

cat('Criando gráfico 4K com paleta FIAP + Asteria...\n')
grafico <- ggplot(dados, aes(x = reorder(Produto, Quantidade), y = Quantidade)) +
  geom_col(
    fill = cores_fiap_asteria[['fiap_pink_vibrant']],
    color = cores_fiap_asteria[['pure_white']],
    linewidth = 1.0,
    alpha = 0.9
  ) +
  geom_text(
    aes(label = paste(Quantidade, 'un')),
    vjust = -0.5,
    size = 5,
    fontface = 'bold',
    color = cores_fiap_asteria[['soft_charcoal']]
  ) +
  labs(
    title = 'Produtos Mais Vendidos',
    subtitle = 'SellOut EasyTrack - Análise de Vendas',
    x = 'Produtos',
    y = 'Quantidade Vendida',
    caption = 'SellOut EasyTrack - Análise de Vendas'
  ) +
  scale_y_continuous(expand = c(0, 0, 0.1, 0)) +
  theme_minimal(base_size = 16) +
  theme(
    plot.title = element_text(
      size = 24,
      face = 'bold',
      hjust = 0.5,
      color = cores_fiap_asteria[['asteria_midnight_blue']],
      margin = margin(b = 10)
    ),
    plot.subtitle = element_text(
      size = 16,
      hjust = 0.5,
      color = cores_fiap_asteria[['fiap_pink_dark']],
      margin = margin(b = 20)
    ),
    plot.caption = element_text(
      size = 12,
      color = '#8C8C8C',
      hjust = 1
    ),
    axis.title.x = element_text(
      size = 18,
      face = 'bold',
      color = cores_fiap_asteria[['soft_charcoal']],
      margin = margin(t = 15)
    ),
    axis.title.y = element_text(
      size = 18,
      face = 'bold',
      color = cores_fiap_asteria[['soft_charcoal']],
      margin = margin(r = 15)
    ),
    axis.text.x = element_text(
      size = 14,
      angle = 45,
      hjust = 1,
      color = cores_fiap_asteria[['asteria_midnight_blue']]
    ),
    axis.text.y = element_text(
      size = 14,
      color = cores_fiap_asteria[['asteria_midnight_blue']]
    ),
    panel.grid.minor = element_blank(),
    panel.grid.major.x = element_blank(),
    panel.grid.major.y = element_line(
      color = cores_fiap_asteria[['light_gray']],
      linewidth = 0.6
    ),
    plot.background = element_rect(
      fill = cores_fiap_asteria[['pure_white']],
      color = NA
    ),
    panel.background = element_rect(
      fill = cores_fiap_asteria[['pure_white']],
      color = NA
    ),
    plot.margin = margin(25, 25, 25, 25)
  )

cat('Salvando gráfico 4K com paleta FIAP + Asteria...\n')
ggsave(
  filename = 'vendas_grafico.png',
  plot = grafico,
  width = 16,
  height = 12,
  dpi = 150,
  units = 'in',
  device = 'png',
  type = 'cairo-png',
  bg = cores_fiap_asteria[['pure_white']]
)

if(file.exists('vendas_grafico.png')) {
  cat('Gráfico salvo com sucesso usando paleta FIAP + Asteria!\n')
  cat('Cores aplicadas:\n')
  cat('• FIAP Pink Vibrant:', cores_fiap_asteria[['fiap_pink_vibrant']], '\n')
  cat('• Asteria Midnight Blue:', cores_fiap_asteria[['asteria_midnight_blue']], '\n')
  cat('• Asteria Ocean Blue:', cores_fiap_asteria[['asteria_ocean_blue']], '\n')
} else {
  stop('Erro ao salvar gráfico com paleta FIAP + Asteria')
}

cat('Script R concluído com paleta FIAP + Asteria!\n')
