package entity;

import service.OperadoraCartao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contratação concreta realizada por um Assinante para um PlanoAssinatura.
 * Compõe seu Pedido inicial, sua primeira Entrega [0..1] e suas tentativas de Pagamento [0..*].
 * Correspondência UML: «entity» Assinatura.
 */
public class Assinatura {
    private SituacaoAssinatura status;
    private final BigDecimal valorContratado;
    private String numeroProtocolo;
    private final Assinante assinante;
    private final PlanoAssinatura plano;
    private final Pedido pedidoInicial;
    private Entrega primeiraEntrega;
    private final List<Pagamento> tentativas = new ArrayList<>();

    /**
     * Cria a assinatura em EM_PREENCHIMENTO e instancia seu Pedido inicial em EM_MONTAGEM.
     * Correspondência no Diagrama de Sequência: mensagens 54 e 55 <<create>> Assinatura(...) -> <<create>> Pedido(this).
     */
    public Assinatura(Assinante assinante, PlanoAssinatura plano) {
        this.assinante = assinante;
        this.plano = plano;
        this.valorContratado = plano.getValor();
        this.status = SituacaoAssinatura.EM_PREENCHIMENTO;
        this.numeroProtocolo = null;
        this.primeiraEntrega = null;
        this.pedidoInicial = new Pedido(this);
    }

    /**
     * Retorna o plano contratado.
     * Correspondência no Diagrama de Sequência: mensagens 72 / 94 getPlano(): PlanoAssinatura.
     */
    public PlanoAssinatura getPlano() {
        return plano;
    }

    /**
     * Retorna o assinante responsável pela contratação.
     * Correspondência no Diagrama de Sequência: mensagens 74 / 96 getAssinante(): Assinante.
     */
    public Assinante getAssinante() {
        return assinante;
    }

    /**
     * Retorna o pedido inicial da assinatura.
     * Correspondência no Diagrama de Sequência: mensagens 78 / 98 / 129 / 135 getPedido(): Pedido.
     */
    public Pedido getPedido() {
        return pedidoInicial;
    }

    /**
     * Cria a primeira Entrega associando o endereço e calculando a previsão conforme o plano.
     * Correspondência no Diagrama de Sequência: mensagem 143 definirPrimeiraEntrega(endereco: EnderecoEntrega): Data.
     */
    public LocalDate definirPrimeiraEntrega(EnderecoEntrega endereco) {
        this.primeiraEntrega = new Entrega(endereco, this.plano);
        return this.primeiraEntrega.getPrevisao();
    }

    /**
     * Coloca a assinatura em AGUARDANDO_PAGAMENTO.
     * Exige seleção confirmada no pedido inicial e primeira entrega com endereço e previsão (R4/Diagrama).
     * Correspondência no Diagrama de Sequência: mensagem 150 aguardarPagamento(): void.
     */
    public void aguardarPagamento() {
        if (!this.pedidoInicial.isSelecaoConfirmada()) {
            throw new IllegalStateException("A seleção do pedido inicial precisa estar confirmada.");
        }
        if (this.primeiraEntrega == null || getEndereco() == null || getPrevisaoPrimeiraEntrega() == null) {
            throw new IllegalStateException("A primeira entrega (endereço e previsão) é obrigatória para aguardar pagamento.");
        }
        this.status = SituacaoAssinatura.AGUARDANDO_PAGAMENTO;
    }

    /**
     * Inicia uma nova tentativa de pagamento em EM_ANALISE.
     * Garante que haja no máximo um pagamento EM_ANALISE ou AUTORIZADO (R5).
     * Correspondência no Diagrama de Sequência: mensagem 157 iniciarPagamento(operadora: OperadoraCartao): Pagamento.
     */
    public Pagamento iniciarPagamento(OperadoraCartao operadora) {
        for (Pagamento t : tentativas) {
            if (t.getSituacao() == SituacaoPagamento.EM_ANALISE) {
                throw new IllegalStateException("Já existe um pagamento em análise para esta assinatura.");
            }
            if (t.getSituacao() == SituacaoPagamento.AUTORIZADO) {
                throw new IllegalStateException("Esta assinatura já possui pagamento autorizado.");
            }
        }
        Pagamento pagamento = new Pagamento(this.valorContratado, operadora);
        this.tentativas.add(pagamento);
        return pagamento;
    }

    /**
     * Retorna o valor contratado da assinatura.
     * Correspondência no Diagrama de Sequência: mensagens 153 / 160 getValorContratado(): Dinheiro.
     */
    public BigDecimal getValorContratado() {
        return valorContratado;
    }

    /**
     * Ativa a assinatura com o protocolo gerado e aprova o pedido inicial.
     * Exige endereço/entrega, pedido com itens válidos, exatamente um pagamento AUTORIZADO e protocolo (R4).
     * Correspondência no Diagrama de Sequência: mensagens 169 e 170 ativar(protocolo: Texto): void -> pedido.aprovar().
     */
    public void ativar(String protocolo) {
        if (protocolo == null || protocolo.isBlank()) {
            throw new IllegalArgumentException("O protocolo é obrigatório para ativar a assinatura.");
        }
        if (this.primeiraEntrega == null || getEndereco() == null || getPrevisaoPrimeiraEntrega() == null) {
            throw new IllegalStateException("Endereço e previsão da primeira entrega são obrigatórios para ativar a assinatura.");
        }
        long autorizados = tentativas.stream()
                .filter(p -> p.getSituacao() == SituacaoPagamento.AUTORIZADO)
                .count();
        if (autorizados != 1) {
            throw new IllegalStateException("A ativação exige exatamente um pagamento AUTORIZADO.");
        }
        this.numeroProtocolo = protocolo;
        this.pedidoInicial.aprovar();
        this.status = SituacaoAssinatura.ATIVA;
    }

    /**
     * Produz o objeto ConfirmacaoContratacao com todos os dados exigidos pela tela de confirmação.
     * Correspondência no Diagrama de Sequência: mensagem 175 obterConfirmacao(): ConfirmacaoContratacao.
     */
    public ConfirmacaoContratacao obterConfirmacao() {
        return new ConfirmacaoContratacao(
                this.plano,
                this.pedidoInicial.getItens(),
                this.plano.getPeriodicidadeEntrega(),
                getEndereco(),
                getPrevisaoPrimeiraEntrega(),
                this.numeroProtocolo
        );
    }

    /**
     * Atributo derivado /endereco obtido de primeiraEntrega, sem duplicação de dados.
     */
    public EnderecoEntrega getEndereco() {
        return primeiraEntrega != null ? primeiraEntrega.getEndereco() : null;
    }

    /**
     * Atributo derivado /previsaoPrimeiraEntrega obtido de primeiraEntrega, sem duplicação de dados.
     */
    public LocalDate getPrevisaoPrimeiraEntrega() {
        return primeiraEntrega != null ? primeiraEntrega.getPrevisao() : null;
    }

    public SituacaoAssinatura getStatus() {
        return status;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public Entrega getPrimeiraEntrega() {
        return primeiraEntrega;
    }

    public List<Pagamento> getTentativas() {
        return Collections.unmodifiableList(tentativas);
    }
}
