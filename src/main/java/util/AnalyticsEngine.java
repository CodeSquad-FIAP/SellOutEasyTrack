package util;

import model.Venda;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Motor de Analytics Inteligente para SellOut EasyTrack
 * Gera insights automáticos baseado nos dados de vendas
 */
public class AnalyticsEngine {

    // Constantes para classificação de insights
    private static final double CRESCIMENTO_ALTO = 0.15; // 15%
    private static final double QUEDA_PREOCUPANTE = -0.10; // -10%
    private static final int DIAS_ANALISE_TREND = 30;
    private static final double DESVIO_PADRAO_LIMITE = 2.0;

    /**
     * Gera insights automáticos baseado na lista de vendas
     */
    public List<Insight> gerarInsightsAutomaticos(List<Venda> vendas) {
        List<Insight> insights = new ArrayList<>();

        if (vendas.isEmpty()) {
            insights.add(new Insight(
                    TipoInsight.INFO,
                    "Sistema Inicializado",
                    "Nenhuma venda registrada ainda. Comece registrando suas primeiras vendas!",
                    "Registre vendas para começar a receber insights inteligentes."
            ));
            return insights;
        }

        // Análises principais
        insights.addAll(analisarTendenciaVendas(vendas));
        insights.addAll(analisarProdutosMaisVendidos(vendas));
        insights.addAll(analisarPerformanceTempoReal(vendas));
        insights.addAll(analisarAnomalias(vendas));
        insights.addAll(analisarSazonalidade(vendas));
        insights.addAll(analisarOportunidades(vendas));

        // Ordenar por prioridade (crítico > alerta > info)
        insights.sort((a, b) -> a.getTipo().getPrioridade() - b.getTipo().getPrioridade());

        return insights;
    }

    /**
     * Analisa tendências de vendas (crescimento/queda)
     */
    private List<Insight> analisarTendenciaVendas(List<Venda> vendas) {
        List<Insight> insights = new ArrayList<>();

        // Separar vendas dos últimos 30 dias vs 30 dias anteriores
        LocalDate hoje = LocalDate.now();
        LocalDate inicio30Dias = hoje.minusDays(30);
        LocalDate inicio60Dias = hoje.minusDays(60);

        List<Venda> vendasRecentes = vendas.stream()
                .filter(v -> v.getData().toLocalDate().isAfter(inicio30Dias))
                .collect(Collectors.toList());

        List<Venda> vendasAnteriores = vendas.stream()
                .filter(v -> v.getData().toLocalDate().isAfter(inicio60Dias)
                        && v.getData().toLocalDate().isBefore(inicio30Dias))
                .collect(Collectors.toList());

        if (vendasAnteriores.isEmpty()) {
            insights.add(new Insight(
                    TipoInsight.INFO,
                    "Dados Insuficientes para Tendência",
                    "Você precisa de pelo menos 60 dias de dados para análise de tendências.",
                    "Continue registrando vendas por mais alguns dias."
            ));
            return insights;
        }

        double faturamentoRecente = calcularFaturamento(vendasRecentes);
        double faturamentoAnterior = calcularFaturamento(vendasAnteriores);

        if (faturamentoAnterior > 0) {
            double crescimento = (faturamentoRecente - faturamentoAnterior) / faturamentoAnterior;

            if (crescimento > CRESCIMENTO_ALTO) {
                insights.add(new Insight(
                        TipoInsight.SUCESSO,
                        "Crescimento Acelerado Detectado! 🚀",
                        String.format("Suas vendas cresceram %.1f%% nos últimos 30 dias!", crescimento * 100),
                        "Mantenha essa estratégia! Considere aumentar o estoque dos produtos em alta."
                ));
            } else if (crescimento < QUEDA_PREOCUPANTE) {
                insights.add(new Insight(
                        TipoInsight.CRITICO,
                        "Queda nas Vendas Detectada ⚠️",
                        String.format("Suas vendas caíram %.1f%% nos últimos 30 dias.", Math.abs(crescimento * 100)),
                        "Analise os produtos com baixa performance e revise sua estratégia de vendas."
                ));
            } else if (crescimento >= 0) {
                insights.add(new Insight(
                        TipoInsight.INFO,
                        "Vendas Estáveis",
                        String.format("Crescimento moderado de %.1f%% nos últimos 30 dias.", crescimento * 100),
                        "Performance consistente. Busque oportunidades de crescimento."
                ));
            }
        }

        return insights;
    }

    /**
     * Analisa performance dos produtos mais vendidos
     */
    private List<Insight> analisarProdutosMaisVendidos(List<Venda> vendas) {
        List<Insight> insights = new ArrayList<>();

        Map<String, Integer> produtosPorQuantidade = vendas.stream()
                .collect(Collectors.groupingBy(
                        Venda::getProduto,
                        Collectors.summingInt(Venda::getQuantidade)
                ));

        Map<String, Double> produtosPorFaturamento = vendas.stream()
                .collect(Collectors.groupingBy(
                        Venda::getProduto,
                        Collectors.summingDouble(v -> v.getQuantidade() * v.getValorUnitario())
                ));

        // Top 3 produtos por quantidade
        List<Map.Entry<String, Integer>> topQuantidade = produtosPorQuantidade.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toList());

        // Top 3 produtos por faturamento
        List<Map.Entry<String, Double>> topFaturamento = produtosPorFaturamento.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toList());

        if (!topQuantidade.isEmpty()) {
            String produtoMaisVendido = topQuantidade.get(0).getKey();
            int quantidade = topQuantidade.get(0).getValue();

            insights.add(new Insight(
                    TipoInsight.SUCESSO,
                    "Produto Campeão de Vendas! 🏆",
                    String.format("%s é seu produto mais vendido com %d unidades!", produtoMaisVendido, quantidade),
                    "Considere aumentar o estoque deste produto e criar promoções relacionadas."
            ));
        }

        if (!topFaturamento.isEmpty()) {
            String produtoMaiorFaturamento = topFaturamento.get(0).getKey();
            double faturamento = topFaturamento.get(0).getValue();

            if (!produtoMaiorFaturamento.equals(topQuantidade.get(0).getKey())) {
                insights.add(new Insight(
                        TipoInsight.INFO,
                        "Produto de Alto Valor Identificado 💰",
                        String.format("%s gera mais faturamento (R$ %.2f) apesar de não ser o mais vendido.",
                                produtoMaiorFaturamento, faturamento),
                        "Foque em vender mais deste produto de alta margem."
                ));
            }
        }

        return insights;
    }

    /**
     * Analisa performance em tempo real (últimos 7 dias)
     */
    private List<Insight> analisarPerformanceTempoReal(List<Venda> vendas) {
        List<Insight> insights = new ArrayList<>();

        LocalDate hoje = LocalDate.now();
        LocalDate seteDiasAtras = hoje.minusDays(7);

        List<Venda> vendasRecentes = vendas.stream()
                .filter(v -> v.getData().toLocalDate().isAfter(seteDiasAtras))
                .collect(Collectors.toList());

        if (vendasRecentes.isEmpty()) {
            insights.add(new Insight(
                    TipoInsight.ALERTA,
                    "Nenhuma Venda Recente",
                    "Você não registrou vendas nos últimos 7 dias.",
                    "Verifique se há vendas pendentes de registro ou revise sua estratégia comercial."
            ));
            return insights;
        }

        double faturamentoSemana = calcularFaturamento(vendasRecentes);
        double ticketMedio = faturamentoSemana / vendasRecentes.size();

        insights.add(new Insight(
                TipoInsight.INFO,
                "Performance da Última Semana",
                String.format("Foram %d vendas totalizando R$ %.2f (ticket médio: R$ %.2f).",
                        vendasRecentes.size(), faturamentoSemana, ticketMedio),
                "Continue monitorando este desempenho diariamente."
        ));

        // Verificar se teve vendas hoje
        boolean vendasHoje = vendasRecentes.stream()
                .anyMatch(v -> v.getData().toLocalDate().equals(hoje));

        if (!vendasHoje) {
            insights.add(new Insight(
                    TipoInsight.ALERTA,
                    "Sem Vendas Hoje",
                    "Ainda não foram registradas vendas para hoje.",
                    "Considere ações promocionais ou verifique pendências de registro."
            ));
        }

        return insights;
    }

    /**
     * Detecta anomalias nos dados (vendas muito altas ou baixas)
     */
    private List<Insight> analisarAnomalias(List<Venda> vendas) {
        List<Insight> insights = new ArrayList<>();

        if (vendas.size() < 10) {
            return insights; // Dados insuficientes para análise estatística
        }

        // Calcular faturamento diário
        Map<LocalDate, Double> faturamentoPorDia = vendas.stream()
                .collect(Collectors.groupingBy(
                        v -> v.getData().toLocalDate(),
                        Collectors.summingDouble(v -> v.getQuantidade() * v.getValorUnitario())
                ));

        List<Double> faturamentosDiarios = new ArrayList<>(faturamentoPorDia.values());
        double media = faturamentosDiarios.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double desvioPadrao = calcularDesvioPadrao(faturamentosDiarios, media);

        // Detectar dias com performance anômala
        for (Map.Entry<LocalDate, Double> entry : faturamentoPorDia.entrySet()) {
            double faturamento = entry.getValue();
            double zScore = Math.abs(faturamento - media) / desvioPadrao;

            if (zScore > DESVIO_PADRAO_LIMITE) {
                if (faturamento > media) {
                    insights.add(new Insight(
                            TipoInsight.SUCESSO,
                            "Dia Excepcional de Vendas! ⭐",
                            String.format("No dia %s você faturou R$ %.2f (%.1fx acima da média)!",
                                    entry.getKey(), faturamento, faturamento / media),
                            "Analise o que funcionou nesse dia para replicar o sucesso."
                    ));
                } else {
                    insights.add(new Insight(
                            TipoInsight.ALERTA,
                            "Dia de Baixa Performance",
                            String.format("No dia %s o faturamento foi R$ %.2f (%.1fx abaixo da média).",
                                    entry.getKey(), faturamento, media / faturamento),
                            "Investigue possíveis causas e previna futuras quedas."
                    ));
                }
            }
        }

        return insights;
    }

    /**
     * Analisa padrões sazonais (dias da semana, fins de semana)
     */
    private List<Insight> analisarSazonalidade(List<Venda> vendas) {
        List<Insight> insights = new ArrayList<>();

        if (vendas.size() < 14) {
            return insights; // Dados insuficientes
        }

        Map<String, Double> faturamentoPorDiaSemana = vendas.stream()
                .collect(Collectors.groupingBy(
                        v -> v.getData().toLocalDate().getDayOfWeek().toString(),
                        Collectors.summingDouble(v -> v.getQuantidade() * v.getValorUnitario())
                ));

        String melhorDia = faturamentoPorDiaSemana.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        double maiorFaturamento = faturamentoPorDiaSemana.values().stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0);

        if (!melhorDia.equals("N/A")) {
            insights.add(new Insight(
                    TipoInsight.INFO,
                    "Melhor Dia da Semana Identificado",
                    String.format("%s é seu melhor dia com R$ %.2f em faturamento total.",
                            traduzirDiaSemana(melhorDia), maiorFaturamento),
                    "Concentre esforços promocionais neste dia da semana."
            ));
        }

        return insights;
    }

    /**
     * Identifica oportunidades de crescimento
     */
    private List<Insight> analisarOportunidades(List<Venda> vendas) {
        List<Insight> insights = new ArrayList<>();

        // Produtos com baixa frequência mas alto valor
        Map<String, Long> frequenciaPorProduto = vendas.stream()
                .collect(Collectors.groupingBy(
                        Venda::getProduto,
                        Collectors.counting()
                ));

        Map<String, Double> valorMedioPorProduto = vendas.stream()
                .collect(Collectors.groupingBy(
                        Venda::getProduto,
                        Collectors.averagingDouble(v -> v.getQuantidade() * v.getValorUnitario())
                ));

        for (String produto : frequenciaPorProduto.keySet()) {
            long frequencia = frequenciaPorProduto.get(produto);
            double valorMedio = valorMedioPorProduto.get(produto);

            if (frequencia <= 2 && valorMedio > 500) { // Produto caro mas pouco vendido
                insights.add(new Insight(
                        TipoInsight.OPORTUNIDADE,
                        "Oportunidade de Produto Premium",
                        String.format("%s tem alto valor (R$ %.2f) mas baixa frequência de vendas.",
                                produto, valorMedio),
                        "Considere campanhas específicas para este produto de alta margem."
                ));
            }
        }

        // Sugestão de diversificação se há poucos produtos
        long produtosUnicos = vendas.stream()
                .map(Venda::getProduto)
                .distinct()
                .count();

        if (produtosUnicos < 5) {
            insights.add(new Insight(
                    TipoInsight.OPORTUNIDADE,
                    "Oportunidade de Diversificação",
                    String.format("Você tem apenas %d produtos diferentes em vendas.", produtosUnicos),
                    "Considere ampliar seu portfólio para aumentar oportunidades de venda."
            ));
        }

        return insights;
    }

    // Métodos auxiliares
    private double calcularFaturamento(List<Venda> vendas) {
        return vendas.stream()
                .mapToDouble(v -> v.getQuantidade() * v.getValorUnitario())
                .sum();
    }

    private double calcularDesvioPadrao(List<Double> valores, double media) {
        double somaDiferencasQuadrado = valores.stream()
                .mapToDouble(v -> Math.pow(v - media, 2))
                .sum();
        return Math.sqrt(somaDiferencasQuadrado / valores.size());
    }

    private String traduzirDiaSemana(String diaIngles) {
        Map<String, String> traducao = Map.of(
                "MONDAY", "Segunda-feira",
                "TUESDAY", "Terça-feira",
                "WEDNESDAY", "Quarta-feira",
                "THURSDAY", "Quinta-feira",
                "FRIDAY", "Sexta-feira",
                "SATURDAY", "Sábado",
                "SUNDAY", "Domingo"
        );
        return traducao.getOrDefault(diaIngles, diaIngles);
    }

    /**
     * Enum para tipos de insight
     */
    public enum TipoInsight {
        CRITICO(1, "🔴"),
        ALERTA(2, "🟡"),
        INFO(3, "🔵"),
        SUCESSO(4, "🟢"),
        OPORTUNIDADE(5, "💡");

        private final int prioridade;
        private final String emoji;

        TipoInsight(int prioridade, String emoji) {
            this.prioridade = prioridade;
            this.emoji = emoji;
        }

        public int getPrioridade() { return prioridade; }
        public String getEmoji() { return emoji; }
    }

    /**
     * Classe para representar um insight
     */
    public static class Insight {
        private final TipoInsight tipo;
        private final String titulo;
        private final String descricao;
        private final String recomendacao;
        private final LocalDate dataGeracao;

        public Insight(TipoInsight tipo, String titulo, String descricao, String recomendacao) {
            this.tipo = tipo;
            this.titulo = titulo;
            this.descricao = descricao;
            this.recomendacao = recomendacao;
            this.dataGeracao = LocalDate.now();
        }

        // Getters
        public TipoInsight getTipo() { return tipo; }
        public String getTitulo() { return titulo; }
        public String getDescricao() { return descricao; }
        public String getRecomendacao() { return recomendacao; }
        public LocalDate getDataGeracao() { return dataGeracao; }

        @Override
        public String toString() {
            return String.format("%s %s\n%s\n💡 %s\n",
                    tipo.getEmoji(), titulo, descricao, recomendacao);
        }
    }
}