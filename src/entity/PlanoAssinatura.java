package entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Oferta comercial reutilizável de assinatura de marmitas.
 * Aplica o limite de quantidade de refeições e verifica se uma opção do cardápio é permitida pelo plano.
 * Correspondência UML: «entity» PlanoAssinatura.
 */
public class PlanoAssinatura {
    private final String nome;
    private final int quantidadeRefeicoes;
    private final String periodicidadeEntrega;
    private final BigDecimal valor;
    private final List<OpcaoCardapio> opcoesPermitidas = new ArrayList<>();

    public PlanoAssinatura(String nome, int quantidadeRefeicoes, String periodicidadeEntrega,
                           BigDecimal valor, List<OpcaoCardapio> opcoesPermitidas) {
        this.nome = nome;
        this.quantidadeRefeicoes = quantidadeRefeicoes;
        this.periodicidadeEntrega = periodicidadeEntrega;
        this.valor = valor;
        if (opcoesPermitidas != null) {
            this.opcoesPermitidas.addAll(opcoesPermitidas);
        }
    }

    /**
     * Verifica se este plano permite a seleção da opção de cardápio informada.
     * Correspondência no Diagrama de Sequência: mensagens 82 / 106 permiteOpcao(opcao: OpcaoCardapio): Booleano.
     */
    public boolean permiteOpcao(OpcaoCardapio opcao) {
        if (opcao == null) return false;
        if (opcoesPermitidas.isEmpty()) return true;
        return opcoesPermitidas.contains(opcao);
    }

    /**
     * Verifica se o total de refeições (unidades de PRATO_PRINCIPAL) está entre 1 e quantidadeRefeicoes.
     * Correspondência no Diagrama de Sequência: mensagem 116 permiteQuantidade(totalRefeicoes: Inteiro): Booleano.
     */
    public boolean permiteQuantidade(int totalRefeicoes) {
        return totalRefeicoes >= 1 && totalRefeicoes <= this.quantidadeRefeicoes;
    }

    public void adicionarOpcaoPermitida(OpcaoCardapio opcao) {
        if (opcao != null && !opcoesPermitidas.contains(opcao)) {
            opcoesPermitidas.add(opcao);
        }
    }

    public String getNome() {
        return nome;
    }

    public int getQuantidadeRefeicoes() {
        return quantidadeRefeicoes;
    }

    public String getPeriodicidadeEntrega() {
        return periodicidadeEntrega;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public List<OpcaoCardapio> getOpcoesPermitidas() {
        return Collections.unmodifiableList(opcoesPermitidas);
    }

    @Override
    public String toString() {
        return String.format("%s (%d refeições | Entrega %s | R$ %.2f)",
                nome, quantidadeRefeicoes, periodicidadeEntrega, valor);
    }
}
