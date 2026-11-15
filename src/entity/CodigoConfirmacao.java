package entity;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Código emitido para validar o celular do assinante, controlando tentativas inválidas e bloqueio (FA1).
 * Correspondência UML: «entity» CodigoConfirmacao.
 */
public class CodigoConfirmacao {
    private static final Random RANDOM = new Random();

    private final String valor;
    private final LocalDateTime dataHoraEmissao;
    private int tentativasInvalidas = 0;
    private SituacaoCodigo situacao;

    /**
     * Nova emissão: situação PENDENTE e tentativasInvalidas = 0.
     * Correspondência no Diagrama de Sequência: mensagem 28 <<create>> CodigoConfirmacao().
     */
    public CodigoConfirmacao() {
        this.valor = String.format("%04d", RANDOM.nextInt(9000) + 1000);
        this.dataHoraEmissao = LocalDateTime.now();
        this.tentativasInvalidas = 0;
        this.situacao = SituacaoCodigo.PENDENTE;
    }

    /**
     * Construtor auxiliar para reconstituição a partir da persistência CSV.
     */
    public CodigoConfirmacao(String valor, LocalDateTime dataHoraEmissao, int tentativasInvalidas, SituacaoCodigo situacao) {
        this.valor = valor;
        this.dataHoraEmissao = dataHoraEmissao;
        this.tentativasInvalidas = tentativasInvalidas;
        this.situacao = situacao;
    }

    /**
     * Valida o valor informado pelo usuário contra o código emitido.
     * Na 3ª tentativa inválida, o código passa para BLOQUEADO (FA1).
     * Correspondência no Diagrama de Sequência: mensagem 39 validar(valorInformado: Texto): SituacaoCodigo.
     */
    public SituacaoCodigo validar(String valorInformado) {
        if (this.situacao != SituacaoCodigo.PENDENTE) {
            return this.situacao;
        }
        if (this.valor != null && this.valor.equals(valorInformado != null ? valorInformado.trim() : "")) {
            this.situacao = SituacaoCodigo.VALIDADO;
        } else {
            registrarTentativaInvalida();
        }
        return this.situacao;
    }

    /**
     * Substitui um código anteriormente pendente quando um novo código é emitido (FA1).
     * Correspondência no Diagrama de Sequência: mensagem 27 substituir(): void.
     */
    public void substituir() {
        if (this.situacao == SituacaoCodigo.PENDENTE) {
            this.situacao = SituacaoCodigo.SUBSTITUIDO;
        }
    }

    /**
     * Incrementa o contador de tentativas inválidas e bloqueia na 3ª falha.
     * Correspondência no Diagrama de Sequência: mensagem 40 registrarTentativaInvalida(): void.
     */
    private void registrarTentativaInvalida() {
        this.tentativasInvalidas++;
        if (this.tentativasInvalidas >= 3) {
            this.situacao = SituacaoCodigo.BLOQUEADO;
        } else {
            this.situacao = SituacaoCodigo.PENDENTE;
        }
    }

    public String getValor() {
        return valor;
    }

    public LocalDateTime getDataHoraEmissao() {
        return dataHoraEmissao;
    }

    public int getTentativasInvalidas() {
        return tentativasInvalidas;
    }

    public int getTentativasRestantes() {
        return Math.max(0, 3 - tentativasInvalidas);
    }

    public SituacaoCodigo getSituacao() {
        return situacao;
    }
}
