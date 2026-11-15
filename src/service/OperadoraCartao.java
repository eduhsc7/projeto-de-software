package service;

import entity.DadosCartao;
import entity.SituacaoPagamento;

import java.math.BigDecimal;

/**
 * Representa o sistema externo da operadora de cartão de crédito responsável por analisar,
 * autorizar ou recusar cada solicitação de pagamento (Passo 11 e FA3).
 * Correspondência UML: «external» OperadoraCartao.
 */
public class OperadoraCartao {
    private final String nome;

    public OperadoraCartao(String nome) {
        this.nome = nome;
    }

    public OperadoraCartao() {
        this("Operadora CrediPay S.A.");
    }

    /**
     * Analisa os dados do cartão e o valor solicitado, retornando AUTORIZADO ou RECUSADO.
     * Regra de simulação:
     * - Retorna RECUSADO (FA3) se dados forem nulos/vazios, número do cartão tiver menos de 8 dígitos,
     *   terminar em "0000", ou CVV não tiver 3 dígitos ou for "000".
     * - Caso contrário, retorna AUTORIZADO.
     * Correspondência no Diagrama de Sequência: mensagem 162 autorizar(valor: Dinheiro, cartao: DadosCartao): SituacaoPagamento.
     */
    public SituacaoPagamento autorizar(BigDecimal valor, DadosCartao cartao) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0 || cartao == null) {
            return SituacaoPagamento.RECUSADO;
        }
        String numeroLimpo = cartao.numero == null ? "" : cartao.numero.replaceAll("\\s+", "");
        String cvvLimpo = cartao.cvv == null ? "" : cartao.cvv.trim();
        String titularLimpo = cartao.nomeTitular == null ? "" : cartao.nomeTitular.trim();
        String validadeLimpa = cartao.validade == null ? "" : cartao.validade.trim();

        boolean cartaoValido = numeroLimpo.length() >= 8 && !numeroLimpo.endsWith("0000");
        boolean cvvValido = cvvLimpo.length() == 3 && !cvvLimpo.equals("000");
        boolean dadosPreenchidos = !titularLimpo.isEmpty() && !validadeLimpa.isEmpty();

        if (cartaoValido && cvvValido && dadosPreenchidos) {
            return SituacaoPagamento.AUTORIZADO;
        }
        return SituacaoPagamento.RECUSADO;
    }

    public String getNome() {
        return nome;
    }
}
