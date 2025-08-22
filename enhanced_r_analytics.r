# Script R Avançado para SellOut EasyTrack - Analytics Inteligente
# Gera múltiplos tipos de visualizações e análises estatísticas

# Carregar bibliotecas necessárias
suppressWarnings({
  library(ggplot2)
  library(dplyr)
  library(scales)
  library(gridExtra)
  library(lubridate)
})

cat('Iniciando Analytics Avançado do SellOut EasyTrack...\n')

# ===== CONFIGURAÇÕES GLOBAIS =====
tema_sellout <- theme_minimal(base_size = 12) +
  theme(
    plot.title = element_text(size = 16, face = 'bold', hjust = 0.5, color = '#2c3e50', margin = margin(b = 10)),
    plot.subtitle = element_text(size = 12, hjust = 0.5, color = '#7f8c8d', margin = margin(b = 15)),
    plot.caption = element_text(size = 9, color = '#95a5a6', hjust = 1),
    axis.title = element_text(size = 11, face = 'bold', color = '#34495e'),
    axis.text = element_text(size = 10, color = '#2c3e50'),
    panel.grid.minor = element_blank(),
    panel.grid.major = element_line(color = '#ecf0f1', linewidth = 0.5),
    plot.background = element_rect(fill = 'white', color = NA),
    panel.background = element_rect(fill = 'white', color = NA),
    plot.margin = margin(15, 15, 15, 15),
    legend.position = 'bottom',
    legend.title = element_text(size = 10, face = 'bold'),
    legend.text = element_text(size = 9)
  )

cores_sellout <- c(
  primaria = '#3498db',
  secundaria = '#2980b9', 
  sucesso = '#2ecc71',
  alerta = '#f39c12',
  perigo = '#e74c3c',
  escuro = '#2c3e50'
)

# ===== LEITURA E PREPARAÇÃO DOS DADOS =====
tryCatch({
  # Verificar se arquivo existe
  if (!file.exists('temp_vendas_data.csv')) {
    stop('Arquivo de dados não encontrado: temp_vendas_data.csv')
  }
  
  # Ler dados
  dados_brutos <- read.csv('temp_vendas_data.csv', stringsAsFactors = FALSE, encoding = 'UTF-8')
  cat(paste('Dados carregados:', nrow(dados_brutos), 'registros\n'))
  
  # Validar estrutura
  if (ncol(dados_brutos) < 2) {
    stop('Estrutura de dados inválida. Esperado: Produto, Quantidade')
  }
  
  # Limpar e preparar dados
  dados <- dados_brutos %>%
    filter(!is.na(Quantidade) & Quantidade > 0) %>%
    mutate(
      Produto = trimws(as.character(Produto)),
      Quantidade = as.numeric(Quantidade)
    ) %>%
    filter(nchar(Produto) > 0)
  
  if (nrow(dados) == 0) {
    stop('Nenhum dado válido encontrado após limpeza')
  }
  
  cat(paste('Dados limpos:', nrow(dados), 'registros válidos\n'))
  
}, error = function(e) {
  cat('ERRO na preparação dos dados:', e$message, '\n')
  quit(status = 1)
})

# ===== ANÁLISES ESTATÍSTICAS =====
tryCatch({
  # Estatísticas descritivas
  stats_basicas <- dados %>%
    summarise(
      total_produtos = n_distinct(Produto),
      total_vendas = sum(Quantidade),
      media_vendas = mean(Quantidade),
      mediana_vendas = median(Quantidade),
      desvio_padrao = sd(Quantidade),
      coef_variacao = sd(Quantidade) / mean(Quantidade)
    )
  
  # Análise por produto
  analise_produtos <- dados %>%
    group_by(Produto) %>%
    summarise(
      total_vendido = sum(Quantidade),
      participacao_pct = round(sum(Quantidade) / sum(dados$Quantidade) * 100, 1),
      .groups = 'drop'
    ) %>%
    arrange(desc(total_vendido)) %>%
    mutate(
      ranking = row_number(),
      categoria_abc = case_when(
        participacao_pct >= 20 ~ 'A - Top Performers',
        participacao_pct >= 5 ~ 'B - Performers Médios', 
        TRUE ~ 'C - Performers Baixos'
      ),
      acumulado_pct = cumsum(participacao_pct)
    )
  
  # Identificar outliers
  Q1 <- quantile(dados$Quantidade, 0.25)
  Q3 <- quantile(dados$Quantidade, 0.75)
  IQR <- Q3 - Q1
  outliers <- dados %>%
    filter(Quantidade > Q3 + 1.5 * IQR | Quantidade < Q1 - 1.5 * IQR)
  
  cat('=== INSIGHTS ESTATÍSTICOS ===\n')
  cat(paste('Total de produtos únicos:', stats_basicas$total_produtos, '\n'))
  cat(paste('Total de vendas:', stats_basicas$total_vendas, '\n'))
  cat(paste('Média de vendas por produto:', round(stats_basicas$media_vendas, 2), '\n'))
  cat(paste('Coeficiente de variação:', round(stats_basicas$coef_variacao, 3), '\n'))
  cat(paste('Outliers detectados:', nrow(outliers), '\n'))
  
}, error = function(e) {
  cat('ERRO na análise estatística:', e$message, '\n')
})

# ===== GRÁFICO 1: RANKING DE PRODUTOS (HORIZONTAL) =====
tryCatch({
  cat('Criando gráfico de ranking de produtos...\n')
  
  # Preparar dados (top 10)
  top_produtos <- analise_produtos %>%
    slice_head(n = 10) %>%
    mutate(Produto = reorder(Produto, total_vendido))
  
  # Criar gráfico
  grafico_ranking <- ggplot(top_produtos, aes(x = total_vendido, y = Produto)) +
    geom_col(
      aes(fill = categoria_abc),
      width = 0.7,
      color = 'white',
      linewidth = 0.5
    ) +
    geom_text(
      aes(label = paste(total_vendido, 'un')),
      hjust = -0.1,
      size = 3.5,
      fontface = 'bold',
      color = cores_sellout[['escuro']]
    ) +
    scale_fill_manual(
      name = 'Categoria ABC',
      values = c(
        'A - Top Performers' = cores_sellout[['primaria']],
        'B - Performers Médios' = cores_sellout[['sucesso']],
        'C - Performers Baixos' = cores_sellout[['alerta']]
      )
    ) +
    scale_x_continuous(
      expand = expansion(mult = c(0, 0.15)),
      labels = label_number(suffix = '')
    ) +
    labs(
      title = 'Ranking de Produtos Mais Vendidos',
      subtitle = 'Top 10 produtos por quantidade vendida',
      x = 'Quantidade Vendida (unidades)',
      y = NULL,
      caption = 'SellOut EasyTrack - Analytics Inteligente'
    ) +
    tema_sellout +
    theme(
      axis.text.y = element_text(size = 10),
      legend.position = 'right'
    )
  
  # Salvar gráfico
  ggsave(
    filename = 'ranking_produtos.png',
    plot = grafico_ranking,
    width = 12,
    height = 8,
    dpi = 300,
    units = 'in',
    type = 'cairo-png'
  )
  
  cat('✅ Gráfico de ranking salvo: ranking_produtos.png\n')
  
}, error = function(e) {
  cat('❌ Erro no gráfico de ranking:', e$message, '\n')
})

# ===== GRÁFICO 2: ANÁLISE ABC (PARETO) =====
tryCatch({
  cat('Criando gráfico de análise ABC (Pareto)...\n')
  
  # Preparar dados para Pareto
  dados_pareto <- analise_produtos %>%
    slice_head(n = 15) %>%
    mutate(
      Produto = factor(Produto, levels = rev(Produto)),
      cor_categoria = case_when(
        categoria_abc == 'A - Top Performers' ~ cores_sellout[['primaria']],
        categoria_abc == 'B - Performers Médios' ~ cores_sellout[['sucesso']],
        TRUE ~ cores_sellout[['alerta']]
      )
    )
  
  # Gráfico de Pareto
  grafico_pareto <- ggplot(dados_pareto, aes(x = reorder(Produto, -total_vendido))) +
    # Barras
    geom_col(
      aes(y = participacao_pct, fill = categoria_abc),
      width = 0.8,
      alpha = 0.8
    ) +
    # Linha acumulada
    geom_line(
      aes(y = acumulado_pct, group = 1),
      color = cores_sellout[['perigo']],
      linewidth = 1.2
    ) +
    geom_point(
      aes(y = acumulado_pct),
      color = cores_sellout[['perigo']],
      size = 2
    ) +
    # Linha 80%
    geom_hline(
      yintercept = 80,
      linetype = 'dashed',
      color = cores_sellout[['escuro']],
      alpha = 0.7
    ) +
    annotate(
      'text',
      x = Inf, y = 82,
      label = 'Regra 80/20',
      hjust = 1,
      size = 3,
      color = cores_sellout[['escuro']]
    ) +
    scale_fill_manual(
      name = 'Categoria ABC',
      values = c(
        'A - Top Performers' = cores_sellout[['primaria']],
        'B - Performers Médios' = cores_sellout[['sucesso']],
        'C - Performers Baixos' = cores_sellout[['alerta']]
      )
    ) +
    scale_y_continuous(
      name = 'Participação (%)',
      sec.axis = sec_axis(~., name = 'Acumulado (%)')
    ) +
    labs(
      title = 'Análise ABC - Curva de Pareto',
      subtitle = 'Participação e contribuição acumulada dos produtos',
      x = 'Produtos',
      caption = 'SellOut EasyTrack - Analytics Inteligente'
    ) +
    tema_sellout +
    theme(
      axis.text.x = element_text(angle = 45, hjust = 1, size = 9),
      legend.position = 'bottom'
    )
  
  # Salvar gráfico
  ggsave(
    filename = 'analise_abc_pareto.png',
    plot = grafico_pareto,
    width = 14,
    height = 9,
    dpi = 300,
    units = 'in',
    type = 'cairo-png'
  )
  
  cat('✅ Gráfico ABC/Pareto salvo: analise_abc_pareto.png\n')
  
}, error = function(e) {
  cat('❌ Erro no gráfico ABC:', e$message, '\n')
})

# ===== GRÁFICO 3: DISTRIBUIÇÃO E OUTLIERS =====
tryCatch({
  cat('Criando gráfico de distribuição...\n')
  
  # Box plot + violin plot combinado
  grafico_distribuicao <- ggplot(dados, aes(x = 'Vendas', y = Quantidade)) +
    # Violin plot (distribuição)
    geom_violin(
      fill = cores_sellout[['primaria']],
      alpha = 0.3,
      color = cores_sellout[['secundaria']]
    ) +
    # Box plot
    geom_boxplot(
      width = 0.2,
      fill = cores_sellout[['sucesso']],
      alpha = 0.7,
      outlier.color = cores_sellout[['perigo']],
      outlier.size = 2
    ) +
    # Média
    stat_summary(
      fun = mean,
      geom = 'point',
      color = cores_sellout[['escuro']],
      size = 3,
      shape = 18
    ) +
    # Estatísticas
    annotate(
      'text',
      x = 1.4, y = max(dados$Quantidade) * 0.9,
      label = paste(
        'Estatísticas:',
        paste('Média:', round(stats_basicas$media_vendas, 1)),
        paste('Mediana:', stats_basicas$mediana_vendas),
        paste('Desvio:', round(stats_basicas$desvio_padrao, 1)),
        sep = '\n'
      ),
      hjust = 0,
      size = 3,
      color = cores_sellout[['escuro']]
    ) +
    scale_y_continuous(
      labels = label_number(suffix = ' un')
    ) +
    labs(
      title = 'Distribuição das Vendas por Produto',
      subtitle = 'Análise de distribuição, outliers e estatísticas descritivas',
      x = NULL,
      y = 'Quantidade Vendida',
      caption = 'SellOut EasyTrack - Analytics Inteligente'
    ) +
    tema_sellout +
    theme(
      axis.text.x = element_blank(),
      axis.ticks.x = element_blank()
    )
  
  # Salvar gráfico
  ggsave(
    filename = 'distribuicao_vendas.png',
    plot = grafico_distribuicao,
    width = 10,
    height = 8,
    dpi = 300,
    units = 'in',
    type = 'cairo-png'
  )
  
  cat('✅ Gráfico de distribuição salvo: distribuicao_vendas.png\n')
  
}, error = function(e) {
  cat('❌ Erro no gráfico de distribuição:', e$message, '\n')
})

# ===== GRÁFICO 4: DASHBOARD EXECUTIVO =====
tryCatch({
  cat('Criando dashboard executivo...\n')
  
  # Gráfico 1: Top 5 produtos (mini)
  mini_top5 <- analise_produtos %>%
    slice_head(n = 5) %>%
    ggplot(aes(x = reorder(Produto, total_vendido), y = total_vendido)) +
    geom_col(fill = cores_sellout[['primaria']], alpha = 0.8) +
    coord_flip() +
    labs(title = 'Top 5 Produtos', x = NULL, y = 'Vendas') +
    theme_minimal(base_size = 8) +
    theme(
      plot.title = element_text(size = 10, face = 'bold'),
      axis.text = element_text(size = 7)
    )
  
  # Gráfico 2: Distribuição ABC (pizza)
  abc_summary <- analise_produtos %>%
    group_by(categoria_abc) %>%
    summarise(
      total = sum(total_vendido),
      produtos = n(),
      .groups = 'drop'
    ) %>%
    mutate(
      pct = round(total / sum(total) * 100, 1),
      label = paste0(categoria_abc, '\n', pct, '%')
    )
  
  pizza_abc <- ggplot(abc_summary, aes(x = '', y = pct, fill = categoria_abc)) +
    geom_col(width = 1, color = 'white') +
    coord_polar('y', start = 0) +
    scale_fill_manual(
      values = c(
        'A - Top Performers' = cores_sellout[['primaria']],
        'B - Performers Médios' = cores_sellout[['sucesso']],
        'C - Performers Baixos' = cores_sellout[['alerta']]
      )
    ) +
    labs(title = 'Análise ABC', fill = NULL) +
    theme_void(base_size = 8) +
    theme(
      plot.title = element_text(size = 10, face = 'bold'),
      legend.text = element_text(size = 7)
    )
  
  # Gráfico 3: Métricas principais
  metricas_texto <- data.frame(
    metric = c('Total Produtos', 'Total Vendas', 'Produto Top', 'Média/Produto'),
    value = c(
      stats_basicas$total_produtos,
      format(stats_basicas$total_vendas, big.mark = '.'),
      analise_produtos$Produto[1],
      paste(round(stats_basicas$media_vendas, 1), 'un')
    )
  )
  
  painel_metricas <- ggplot(metricas_texto, aes(x = 1, y = seq_along(metric))) +
    geom_text(
      aes(label = paste(metric, ':', value)),
      size = 3,
      hjust = 0,
      fontface = 'bold'
    ) +
    xlim(0.5, 2) +
    labs(title = 'Métricas Principais') +
    theme_void(base_size = 8) +
    theme(
      plot.title = element_text(size = 10, face = 'bold')
    )
  
  # Combinar gráficos
  dashboard <- grid.arrange(
    mini_top5, pizza_abc, painel_metricas,
    ncol = 3,
    top = textGrob(
      'SellOut EasyTrack - Dashboard Executivo',
      gp = gpar(fontsize = 16, fontface = 'bold')
    ),
    bottom = textGrob(
      'Analytics Inteligente - Powered by R',
      gp = gpar(fontsize = 8, col = 'grey50')
    )
  )
  
  # Salvar dashboard
  ggsave(
    filename = 'dashboard_executivo.png',
    plot = dashboard,
    width = 15,
    height = 6,
    dpi = 300,
    units = 'in',
    type = 'cairo-png'
  )
  
  cat('✅ Dashboard executivo salvo: dashboard_executivo.png\n')
  
}, error = function(e) {
  cat('❌ Erro no dashboard:', e$message, '\n')
})

# ===== GRÁFICO PRINCIPAL OTIMIZADO =====
tryCatch({
  cat('Criando gráfico principal otimizado...\n')
  
  # Top 12 produtos para melhor visualização
  dados_principais <- analise_produtos %>%
    slice_head(n = 12) %>%
    mutate(
      Produto = reorder(Produto, total_vendido),
      destaque = ifelse(ranking <= 3, 'Top 3', 'Outros')
    )
  
  # Gráfico principal
  grafico_principal <- ggplot(dados_principais, aes(x = Produto, y = total_vendido)) +
    geom_col(
      aes(fill = destaque),
      width = 0.8,
      color = 'white',
      linewidth = 0.5
    ) +
    geom_text(
      aes(label = paste(total_vendido, 'un\n(', participacao_pct, '%)')),
      vjust = -0.3,
      size = 3,
      fontface = 'bold',
      color = cores_sellout[['escuro']]
    ) +
    scale_fill_manual(
      name = 'Categoria',
      values = c(
        'Top 3' = cores_sellout[['primaria']],
        'Outros' = cores_sellout[['sucesso']]
      )
    ) +
    scale_y_continuous(
      expand = expansion(mult = c(0, 0.15)),
      labels = label_number(suffix = ' un')
    ) +
    labs(
      title = 'Produtos Mais Vendidos - Análise Detalhada',
      subtitle = paste(
        'Período de análise com', 
        stats_basicas$total_produtos, 
        'produtos únicos e',
        format(stats_basicas$total_vendas, big.mark = '.'),
        'vendas totais'
      ),
      x = 'Produtos',
      y = 'Quantidade Vendida',
      caption = 'SellOut EasyTrack - Sistema de Gestão de Vendas'
    ) +
    tema_sellout +
    theme(
      axis.text.x = element_text(angle = 45, hjust = 1, size = 9),
      legend.position = 'top'
    )
  
  # Salvar gráfico principal
  ggsave(
    filename = 'vendas_grafico.png',
    plot = grafico_principal,
    width = 14,
    height = 10,
    dpi = 400,
    units = 'in',
    type = 'cairo-png'
  )
  
  cat('✅ Gráfico principal salvo: vendas_grafico.png\n')
  
}, error = function(e) {
  cat('❌ Erro no gráfico principal:', e$message, '\n')
})

# ===== RELATÓRIO ANALÍTICO EM JSON =====
tryCatch({
  cat('Gerando relatório analítico...\n')
  
  # Preparar insights automáticos
  insights <- list(
    estatisticas_gerais = list(
      total_produtos = stats_basicas$total_produtos,
      total_vendas = stats_basicas$total_vendas,
      media_vendas = round(stats_basicas$media_vendas, 2),
      coeficiente_variacao = round(stats_basicas$coef_variacao, 3),
      outliers_detectados = nrow(outliers)
    ),
    top_performers = analise_produtos %>%
      slice_head(n = 5) %>%
      select(produto = Produto, vendas = total_vendido, participacao = participacao_pct),
    analise_abc = abc_summary,
    recomendacoes = list(
      foco_categoria_a = paste('Foque nos', 
                              sum(analise_produtos$categoria_abc == 'A - Top Performers'),
                              'produtos da categoria A'),
      diversificacao = ifelse(stats_basicas$total_produtos < 5,
                             'Considere diversificar o portfólio',
                             'Portfólio bem diversificado'),
      outliers = ifelse(nrow(outliers) > 0,
                       paste('Investigar', nrow(outliers), 'outliers detectados'),
                       'Distribuição normal, sem outliers significativos')
    )
  )
  
  # Salvar insights em JSON
  writeLines(
    jsonlite::toJSON(insights, pretty = TRUE, auto_unbox = TRUE),
    'analytics_insights.json'
  )
  
  cat('✅ Relatório analítico salvo: analytics_insights.json\n')
  
}, error = function(e) {
  cat('❌ Erro no relatório:', e$message, '\n')
})

# ===== FINALIZAÇÃO =====
cat('\n=== ANALYTICS CONCLUÍDO ===\n')
cat('Arquivos gerados:\n')

arquivos_gerados <- c(
  'vendas_grafico.png',
  'ranking_produtos.png', 
  'analise_abc_pareto.png',
  'distribuicao_vendas.png',
  'dashboard_executivo.png',
  'analytics_insights.json'
)

for (arquivo in arquivos_gerados) {
  if (file.exists(arquivo)) {
    cat(paste('✅', arquivo, '\n'))
  } else {
    cat(paste('❌', arquivo, '(não gerado)\n'))
  }
}

cat('\n🎯 Analytics Inteligente finalizado com sucesso!\n')
cat('📊 Visualizações prontas para análise executiva\n')