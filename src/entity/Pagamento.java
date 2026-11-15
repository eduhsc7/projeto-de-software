package entity;

import service.OperadoraCartao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa uma tentativa de pagamento da assinatura junto à OperadoraCartao,
 * preservando tanto autorizações quanto recusas (FA3).
 * Correspondência UML: «entity» Pagamento.
 */
public class Pagamento {
    private final BigDecimal valor;
    private final LocalDateTime dataHoraSolicitacao;
    private LocalDateTime dataHoraConfirmacao;
    private SituacaoPagamento situacao;
    private final OperadoraCartao operadora;

    /**
     * Nova instância a cada tentativa de pagamento, iniciada em EM_ANALISE.
     * Correspondência no Diagrama de Sequência: mensagem 158 <<create>> Pagamento(valor: Dinheiro, operadora: OperadoraCartao).
     */
    public Pagamento(BigDecimal valor, OperadoraCartao operadora) {
        this.valor = valor;
        this.operadora = operadora;
        this.dataHoraSolicitacao = LocalDateTime.now();
        this.dataHoraConfirmacao = null;
        this.situacao = SituacaoPagamento.EM_ANALISE;
    }

    /**
     * Registra a autorização do pagamento e preenche dataHoraConfirmacao.
     * Correspondência no Diagrama de Sequência: mensagem 166 confirmarAutorizacao(): void.
     */
    public void confirmarAutorizacao() {
        this.situacao = SituacaoPagamento.AUTORIZADO;
        this.dataHoraConfirmacao = LocalDateTime.now();
    }

    /**
     * Registra a recusa do pagamento, preservando a tentativa no histórico da assinatura (FA3).
     * Correspondência no Diagrama de Sequência: mensagem 181 registrarRecusa(): void.
     */
    public void registrarRecusa() {
        this.situacao = SituacaoPagamento.RECUSADO;
        this.dataHoraConfirmacao = null;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDateTime getDataHoraSolicitacao() {
        return dataHoraSolicitacao;
    }

    public LocalDateTime getDataHoraConfirmacao() {
        return dataHoraConfirmacao;
    }

    public SituacaoPagamento getSituacao() {
        return situacao;
    }

    public OperadoraCartao getOperadora() {
        return operadora;
    }
}
