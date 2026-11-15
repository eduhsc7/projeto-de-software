package entity;

import java.time.LocalDate;

/**
 * Registra o destino (EnderecoEntrega) e a previsão da primeira entrega da assinatura.
 * Correspondência UML: «entity» Entrega.
 */
public class Entrega {
    private final EnderecoEntrega endereco;
    private final LocalDate previsao;

    /**
     * Cria a primeira entrega e calcula sua previsão com base na periodicidade do plano.
     * Correspondência no Diagrama de Sequência: mensagem 144 <<create>> Entrega(endereco: EnderecoEntrega, plano: PlanoAssinatura).
     */
    public Entrega(EnderecoEntrega endereco, PlanoAssinatura plano) {
        this.endereco = endereco;
        this.previsao = calcularPrevisao(plano);
    }

    /**
     * Calcula a data prevista da primeira entrega a partir da periodicidade do plano.
     * Correspondência no Diagrama de Sequência: mensagem 145 calcularPrevisao(plano: PlanoAssinatura): Data.
     */
    private LocalDate calcularPrevisao(PlanoAssinatura plano) {
        String periodicidade = (plano != null && plano.getPeriodicidadeEntrega() != null)
                ? plano.getPeriodicidadeEntrega().toLowerCase()
                : "";
        if (periodicidade.contains("diár") || periodicidade.contains("diar")) {
            return LocalDate.now().plusDays(1);
        } else if (periodicidade.contains("quinzenal")) {
            return LocalDate.now().plusDays(3);
        } else if (periodicidade.contains("mensal")) {
            return LocalDate.now().plusDays(4);
        }
        return LocalDate.now().plusDays(2);
    }

    /**
     * Retorna a data prevista da primeira entrega.
     * Correspondência no Diagrama de Sequência: mensagem 147 getPrevisao(): Data.
     */
    public LocalDate getPrevisao() {
        return previsao;
    }

    public EnderecoEntrega getEndereco() {
        return endereco;
    }
}
