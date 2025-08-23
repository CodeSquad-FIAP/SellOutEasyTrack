suppressWarnings({
library(ggplot2)
library(dplyr)
library(scales)
library(gridExtra)
library(lubridate)
})

cat('Iniciando Analytics Avançado do SellOut EasyTrack...\n')

cores_fiap_asteria <- list(
fiap_pink_vibrant = '#F23064',
fiap_pink_dark = '#BF3B5E',
fiap_gray_medium = '#8C8C8C',
fiap_gray_dark = '#404040',
fiap_black_tech = '#262626',
asteria_midnight_blue = '#2C3E50',
asteria_amethyst = '#8E44AD',
asteria_ocean_blue = '#3498DB',
asteria_silver = '#BDC3C7',
success_emerald = '#2ECC71',
warning_amber = '#F39C12',
danger_cardinal = '#E74C3C',
info_azure = '#3498DB',
pure_white = '#FFFFFF',
light_gray = '#ECF0F1',
pearl_gray = '#DCDDE1',
soft_charcoal = '#2C3E50'
)

cores_series <- c(
cores_fiap_asteriafiap
p
​
 ink
v
​
 ibrant,cores
f
​
 iap
a
​
 steriaasteria_ocean_blue,
cores_fiap_asteriaasteria
a
​
 methyst,cores
f
​
 iap
a
​
 steriasuccess_emerald,
cores_fiap_asteriawarning
a
​
 mber,cores
f
​
 iap
a
​
 steriafiap_pink_dark,
cores_fiap_asteriaasteria
m
​
 idnight
b
​
 lue,cores
f
​
 iap
a
​
 steriafiap_gray_dark
)

tema_sellout <- theme_minimal(base_size = 12) +
theme(
plot.title = element_text(size = 16, face = 'bold', hjust = 0.5,
color = cores_fiap_asteriaasteria
m
​
 idnight
b
​
 lue,margin=margin(b=10)),plot.subtitle=element
t
​
 ext(size=12,hjust=0.5,color=cores
f
​
 iap
a
​
 steriafiap_gray_medium,
margin = margin(b = 15)),
plot.caption = element_text(size = 9, color = cores_fiap_asteriaasteria
s
​
 ilver,hjust=1),axis.title=element
t
​
 ext(size=11,face=
′
 bold
′
 ,color=cores
f
​
 iap
a
​
 steriasoft_charcoal),
axis.text = element_text(size = 10, color = cores_fiap_asteriaasteria
m
​
 idnight
b
​
 lue),panel.grid.minor=element
b
​
 lank(),panel.grid.major=element
l
​
 ine(color=cores
f
​
 iap
a
​
 sterialight_gray, linewidth = 0.5),
plot.background = element_rect(fill = cores_fiap_asteriapure
w
​
 hite,color=NA),panel.background=element
r
​
 ect(fill=cores
f
​
 iap
a
​
 steriapure_white, color = NA),
plot.margin = margin(15, 15, 15, 15),
legend.position = 'bottom',
legend.title = element_text(size = 10, face = 'bold'),
legend.text = element_text(size = 9)
)

tryCatch({
if (!file.exists('temp_vendas_data.csv')) {
stop('Arquivo de dados não encontrado: temp_vendas_data.csv')
}

dados_brutos <- read.csv('temp_vendas_data.csv', stringsAsFactors = FALSE, encoding = 'UTF-8')
cat(paste('Dados carregados:', nrow(dados_brutos), 'registros\n'))

if (ncol(dados_brutos) < 2) {
stop('Estrutura de dados inválida. Esperado: Produto, Quantidade')
}

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

tryCatch({
stats_basicas <- dados %>%
summarise(
total_produtos = n_distinct(Produto),
total_vendas = sum(Quantidade),
media_vendas = mean(Quantidade),
mediana_vendas = median(Quantidade),
desvio_padrao = sd(Quantidade),
coef_variacao = sd(Quantidade) / mean(Quantidade)
)

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

Q1 <- quantile(dadosQuantidade,0.25)Q3<−quantile(dadosQuantidade, 0.75)
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

tryCatch({
cat('Criando gráfico de ranking de produtos...\n')

top_produtos <- analise_produtos %>%
slice_head(n = 10) %>%
mutate(Produto = reorder(Produto, total_vendido))

grafico_ranking <- ggplot(top_produtos, aes(x = total_vendido, y = Produto)) +
geom_col(
aes(fill = categoria_abc),
width = 0.7,
color = cores_fiap_asteriapure
w
​
 hite,linewidth=0.5)+geom
t
​
 ext(aes(label=paste(total
v
​
 endido,
′
 un
′
 )),hjust=−0.1,size=3.5,fontface=
′
 bold
′
 ,color=cores
f
​
 iap
a
​
 steriasoft_charcoal
) +
scale_fill_manual(
name = 'Categoria ABC',
values = c(
'A - Top Performers' = cores_fiap_asteriafiap
p
​
 ink
v
​
 ibrant,
′
 B−PerformersM
e
ˊ
 dios
′
 =cores
f
​
 iap
a
​
 steriaasteria_ocean_blue,
'C - Performers Baixos' = cores_fiap_asteria$asteria_amethyst
)
) +
scale_x_continuous(
expand = expansion(mult = c(0, 0.15)),
labels = label_number(suffix = '')
) +
labs(
title = 'Ranking de Produtos Mais Vendidos',
subtitle = 'Top 10 produtos por quantidade vendida - Paleta FIAP + Asteria',
x = 'Quantidade Vendida (unidades)',
y = NULL,
caption = 'SellOut EasyTrack - Analytics Inteligente'
) +
tema_sellout +
theme(
axis.text.y = element_text(size = 10),
legend.position = 'right'
)

ggsave(
filename = 'ranking_produtos.png',
plot = grafico_ranking,
width = 12,
height = 8,
dpi = 300,
units = 'in',
type = 'cairo-png'
)

cat(' Gráfico de ranking salvo: ranking_produtos.png\n')

}, error = function(e) {
cat(' Erro no gráfico de ranking:', e$message, '\n')
})

tryCatch({
cat('Criando gráfico de análise ABC (Pareto)...\n')

dados_pareto <- analise_produtos %>%
slice_head(n = 15) %>%
mutate(
Produto = factor(Produto, levels = rev(Produto)),
cor_categoria = case_when(
categoria_abc == 'A - Top Performers' ~ cores_fiap_asteriafiap
p
​
 ink
v
​
 ibrant,categoria
a
​
 bc==
′
 B−PerformersM
e
ˊ
 dios
′
  cores
f
​
 iap
a
​
 steriaasteria_ocean_blue,
TRUE ~ cores_fiap_asteria$warning_amber
)
)

grafico_pareto <- ggplot(dados_pareto, aes(x = reorder(Produto, -total_vendido))) +
geom_col(
aes(y = participacao_pct, fill = categoria_abc),
width = 0.8,
alpha = 0.8
) +
geom_line(
aes(y = acumulado_pct, group = 1),
color = cores_fiap_asteriadanger
c
​
 ardinal,linewidth=1.2)+geom
p
​
 oint(aes(y=acumulado
p
​
 ct),color=cores
f
​
 iap
a
​
 steriadanger_cardinal,
size = 2
) +
geom_hline(
yintercept = 80,
linetype = 'dashed',
color = cores_fiap_asteriafiap
g
​
 ray
d
​
 ark,alpha=0.7)+annotate(
′
 text
′
 ,x=Inf,y=82,label=
′
 Regra80/20
′
 ,hjust=1,size=3,color=cores
f
​
 iap
a
​
 steriafiap_gray_dark
) +
scale_fill_manual(
name = 'Categoria ABC',
values = c(
'A - Top Performers' = cores_fiap_asteriafiap
p
​
 ink
v
​
 ibrant,
′
 B−PerformersM
e
ˊ
 dios
′
 =cores
f
​
 iap
a
​
 steriaasteria_ocean_blue,
'C - Performers Baixos' = cores_fiap_asteria$warning_amber
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

ggsave(
filename = 'analise_abc_pareto.png',
plot = grafico_pareto,
width = 14,
height = 9,
dpi = 300,
units = 'in',
type = 'cairo-png'
)

cat(' Gráfico ABC/Pareto salvo: analise_abc_pareto.png\n')

}, error = function(e) {
cat(' Erro no gráfico ABC:', e$message, '\n')
})

tryCatch({
cat('Criando gráfico de distribuição...\n')

grafico_distribuicao <- ggplot(dados, aes(x = 'Vendas', y = Quantidade)) +
geom_violin(
fill = cores_fiap_asteriafiap
p
​
 ink
v
​
 ibrant,alpha=0.3,color=cores
f
​
 iap
a
​
 steriaasteria_midnight_blue
) +
geom_boxplot(
width = 0.2,
fill = cores_fiap_asteriaasteria
o
​
 cean
b
​
 lue,alpha=0.7,outlier.color=cores
f
​
 iap
a
​
 steriadanger_cardinal,
outlier.size = 2
) +
stat_summary(
fun = mean,
geom = 'point',
color = cores_fiap_asteriafiap
b
​
 lack
t
​
 ech,size=3,shape=18)+annotate(
′
 text
′
 ,x=1.4,y=max(dadosQuantidade) * 0.9,
label = paste(
'Estatísticas:',
paste('Média:', round(stats_basicasmedia
v
​
 endas,1)),paste(
′
 Mediana:
′
 ,stats
b
​
 asicasmediana_vendas),
paste('Desvio:', round(stats_basicas$desvio_padrao, 1)),
sep = '\n'
),
hjust = 0,
size = 3,
color = cores_fiap_asteria$soft_charcoal
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

ggsave(
filename = 'distribuicao_vendas.png',
plot = grafico_distribuicao,
width = 10,
height = 8,
dpi = 300,
units = 'in',
type = 'cairo-png'
)

cat(' Gráfico de distribuição salvo: distribuicao_vendas.png\n')

}, error = function(e) {
cat(' Erro no gráfico de distribuição:', e$message, '\n')
})

tryCatch({
cat('Criando dashboard executivo...\n')

mini_top5 <- analise_produtos %>%
slice_head(n = 5) %>%
ggplot(aes(x = reorder(Produto, total_vendido), y = total_vendido)) +
geom_col(fill = cores_fiap_asteriafiap
p
​
 ink
v
​
 ibrant,alpha=0.8)+coord
f
​
 lip()+labs(title=
′
 Top5Produtos
′
 ,x=NULL,y=
′
 Vendas
′
 )+theme
m
​
 inimal(base
s
​
 ize=8)+theme(plot.title=element
t
​
 ext(size=10,face=
′
 bold
′
 ,color=cores
f
​
 iap
a
​
 steriaasteria_midnight_blue),
axis.text = element_text(size = 7, color = cores_fiap_asteria$soft_charcoal)
)

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
geom_col(width = 1, color = cores_fiap_asteriapure
w
​
 hite)+coord
p
​
 olar(
′
 y
′
 ,start=0)+scale
f
​
 ill
m
​
 anual(values=c(
′
 A−TopPerformers
′
 =cores
f
​
 iap
a
​
 steriafiap_pink_vibrant,
'B - Performers Médios' = cores_fiap_asteriaasteria
o
​
 cean
b
​
 lue,
'C - Performers Baixos' = cores_fiap_asteria$warning_amber
)
) +
labs(title = 'Análise ABC', fill = NULL) +
theme_void(base_size = 8) +
theme(
plot.title = element_text(size = 10, face = 'bold', color = cores_fiap_asteria$asteria_midnight_blue),
legend.text = element_text(size = 7)
)

metricas_texto <- data.frame(
metric = c('Total Produtos', 'Total Vendas', 'Produto Top', 'Média/Produto'),
value = c(
stats_basicastotal
p
​
 rodutos,format(stats
b
​
 asicastotal_vendas, big.mark = '.'),
analise_produtosProduto[1],paste(round(stats
b
​
 asicasmedia_vendas, 1), 'un')
)
)

painel_metricas <- ggplot(metricas_texto, aes(x = 1, y = seq_along(metric))) +
geom_text(
aes(label = paste(metric, ':', value)),
size = 3,
hjust = 0,
fontface = 'bold',
color = cores_fiap_asteriasoft
c
​
 harcoal)+xlim(0.5,2)+labs(title=
′
 M
e
ˊ
 tricasPrincipais
′
 )+theme
v
​
 oid(base
s
​
 ize=8)+theme(plot.title=element
t
​
 ext(size=10,face=
′
 bold
′
 ,color=cores
f
​
 iap
a
​
 steriaasteria_midnight_blue)
)
dashboard <- grid.arrange(
mini_top5, pizza_abc, painel_metricas,
ncol = 3,
top = textGrob(
'SellOut EasyTrack - Dashboard Executivo',
gp = gpar(fontsize = 16, fontface = 'bold', col = cores_fiap_asteriaasteria
m
​
 idnight
b
​
 lue)),bottom=textGrob(
′
 AnalyticsInteligente−PoweredbyR−PaletaFIAP+Asteria
′
 ,gp=gpar(fontsize=8,col=cores
f
​
 iap
a
​
 steriafiap_gray_medium)
)
)

ggsave(
filename = 'dashboard_executivo.png',
plot = dashboard,
width = 15,
height = 6,
dpi = 300,
units = 'in',
type = 'cairo-png'
)

cat(' Dashboard executivo salvo: dashboard_executivo.png\n')

}, error = function(e) {
cat(' Erro no dashboard:', e$message, '\n')
})

tryCatch({
cat('Criando gráfico principal otimizado...\n')

dados_principais <- analise_produtos %>%
slice_head(n = 12) %>%
mutate(
Produto = reorder(Produto, total_vendido),
destaque = ifelse(ranking <= 3, 'Top 3', 'Outros')
)

grafico_principal <- ggplot(dados_principais, aes(x = Produto, y = total_vendido)) +
geom_col(
aes(fill = destaque),
width = 0.8,
color = cores_fiap_asteria$pure_white,
linewidth = 0.5
) +
geom_text(
aes(label = paste(total_vendido, 'un\n(', participacao_pct, '%)')),
vjust = -0.3,
size = 3,
fontface = 'bold',
color = cores_fiap_asteria$soft_charcoal
) +
scale_fill_manual(
name = 'Categoria',
values = c(
'Top 3' = cores_fiap_asteriafiap
p
​
 ink
v
​
 ibrant,
′
 Outros
′
 =cores
f
​
 iap
a
​
 steriaasteria_ocean_blue
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
stats_basicastotal
p
​
 rodutos,
′
 produtos
u
ˊ
 nicose
′
 ,format(stats
b
​
 asicastotal_vendas, big.mark = '.'),
'vendas totais - Paleta FIAP + Asteria'
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

ggsave(
filename = 'vendas_grafico.png',
plot = grafico_principal,
width = 14,
height = 10,
dpi = 400,
units = 'in',
type = 'cairo-png'
)

cat(' Gráfico principal salvo: vendas_grafico.png\n')

}, error = function(e) {
cat(' Erro no gráfico principal:', e$message, '\n')
})

tryCatch({
cat('Gerando relatório analítico...\n')

insights <- list(
paleta_cores = list(
sistema = 'FIAP + Asteria',
cores_principais = list(
fiap_pink_vibrant = cores_fiap_asteriafiap
p
​
 ink
v
​
 ibrant,asteria
m
​
 idnight
b
​
 lue=cores
f
​
 iap
a
​
 steriaasteria_midnight_blue,
asteria_ocean_blue = cores_fiap_asteriaasteria
o
​
 cean
b
​
 lue,asteria
a
​
 methyst=cores
f
​
 iap
a
​
 steriaasteria_amethyst
)
),
estatisticas_gerais = list(
total_produtos = stats_basicastotal
p
​
 rodutos,total
v
​
 endas=stats
b
​
 asicastotal_vendas,
media_vendas = round(stats_basicasmedia
v
​
 endas,2),coeficiente
v
​
 ariacao=round(stats
b
​
 asicascoef_variacao, 3),
outliers_detectados = nrow(outliers)
),
top_performers = analise_produtos %>%
slice_head(n = 5) %>%
select(produto = Produto, vendas = total_vendido, participacao = participacao_pct),
analise_abc = abc_summary,
recomendacoes = list(
foco_categoria_a = paste('Foque nos',
sum(analise_produtoscategoria
a
​
 bc==
′
 A−TopPerformers
′
 ),
′
 produtosdacategoriaA
′
 ),diversificacao=ifelse(stats
b
​
 asicastotal_produtos < 5,
'Considere diversificar o portfólio',
'Portfólio bem diversificado'),
outliers = ifelse(nrow(outliers) > 0,
paste('Investigar', nrow(outliers), 'outliers detectados'),
'Distribuição normal, sem outliers significativos')
),
design_info = list(
tema_aplicado = 'FIAP + Asteria',
graficos_gerados = c(
'ranking_produtos.png',
'analise_abc_pareto.png',
'distribuicao_vendas.png',
'dashboard_executivo.png',
'vendas_grafico.png'
),
resolucao = '1080p+ (400 DPI)',
formato = 'PNG com fundo branco'
)
)

writeLines(
jsonlite::toJSON(insights, pretty = TRUE, auto_unbox = TRUE),
'analytics_insights.json'
)

cat(' Relatório analítico salvo: analytics_insights.json\n')

}, error = function(e) {
cat(' Erro no relatório:', e$message, '\n')
})

cat('\n=== ANALYTICS CONCLUÍDO ===\n')
cat(' PALETA APLICADA: FIAP + ASTERIA\n')
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
cat(paste(' ', arquivo, '\n'))
} else {
cat(paste(' ', arquivo, '(não gerado)\n'))
}
}

cat('\n Analytics Inteligente finalizado com sucesso!\n')
cat(' Visualizações prontas para análise executiva\n')
cat(' Paleta FIAP + Asteria aplicada em todos os gráficos\n')

cat('\n=== CORES UTILIZADAS ===\n')
cat('FIAP Pink Vibrant:', cores_fiap_asteria$fiap_pink_vibrant, '\n')
cat('Asteria Midnight Blue:', cores_fiap_asteria$asteria_midnight_blue, '\n')
cat('Asteria Ocean Blue:', cores_fiap_asteria$asteria_ocean_blue, '\n')
cat('Asteria Amethyst:', cores_fiap_asteria$asteria_amethyst, '\n')
cat('Success Emerald:', cores_fiap_asteria$success_emerald, '\n')
cat('Warning Amber:', cores_fiap_asteria$warning_amber, '\n')
cat('=========================\n')