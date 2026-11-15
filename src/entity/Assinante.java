package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pessoa que realiza a contratação do plano de refeições.
 * Administra seus códigos de confirmação emitidos e suas preferências alimentares.
 * Correspondência UML: «entity» Assinante.
 */
public class Assinante {
    private final String celular;
    private final List<CodigoConfirmacao> codigosEmitidos = new ArrayList<>();
    private final List<PreferenciaAlimentar> preferencias = new ArrayList<>();

    /**
     * Correspondência no Diagrama de Sequência: mensagem 7 <<create>> Assinante(celular: Texto).
     */
    public Assinante(String celular) {
        this.celular = celular;
    }

    /**
     * Emite um novo código de confirmação. Se houver um código anterior PENDENTE,
     * ele passa para SUBSTITUIDO e é preservado no histórico (FA1).
     * Correspondência no Diagrama de Sequência: mensagens 9 / 23 emitirCodigo(): CodigoConfirmacao.
     */
    public CodigoConfirmacao emitirCodigo() {
        CodigoConfirmacao codigoAnterior = codigoPendente();
        if (codigoAnterior != null) {
            codigoAnterior.substituir();
        }
        CodigoConfirmacao novoCodigo = new CodigoConfirmacao();
        this.codigosEmitidos.add(novoCodigo);
        return novoCodigo;
    }

    /**
     * Localiza o código atualmente PENDENTE, caso exista [0..1].
     * Correspondência no Diagrama de Sequência: mensagens 25 / 37 codigoPendente(): CodigoConfirmacao [0..1].
     */
    private CodigoConfirmacao codigoPendente() {
        for (int i = codigosEmitidos.size() - 1; i >= 0; i--) {
            CodigoConfirmacao c = codigosEmitidos.get(i);
            if (c.getSituacao() == SituacaoCodigo.PENDENTE) {
                return c;
            }
        }
        return null;
    }

    /**
     * Valida o código informado pelo usuário delegando ao código pendente.
     * Correspondência no Diagrama de Sequência: mensagem 36 validarCodigo(valorInformado: Texto): SituacaoCodigo.
     */
    public SituacaoCodigo validarCodigo(String valorInformado) {
        CodigoConfirmacao codigo = codigoPendente();
        if (codigo != null) {
            return codigo.validar(valorInformado);
        }
        if (celularValidado()) {
            return SituacaoCodigo.VALIDADO;
        }
        if (!codigosEmitidos.isEmpty()) {
            return codigosEmitidos.get(codigosEmitidos.size() - 1).getSituacao();
        }
        return SituacaoCodigo.BLOQUEADO;
    }

    /**
     * Verifica se o último código emitido para o assinante encontra-se na situação VALIDADO.
     * Correspondência no Diagrama de Sequência: mensagem 51 celularValidado(): Booleano.
     */
    public boolean celularValidado() {
        if (codigosEmitidos.isEmpty()) {
            return false;
        }
        return codigosEmitidos.get(codigosEmitidos.size() - 1).getSituacao() == SituacaoCodigo.VALIDADO;
    }

    /**
     * Define as preferências alimentares declaradas pelo assinante.
     * Correspondência no Diagrama de Sequência: mensagem 68 definirPreferencias(preferencias: Lista<PreferenciaAlimentar>): void.
     */
    public void definirPreferencias(List<PreferenciaAlimentar> novasPreferencias) {
        this.preferencias.clear();
        if (novasPreferencias != null) {
            this.preferencias.addAll(novasPreferencias);
        }
    }

    /**
     * Retorna as preferências alimentares do assinante.
     * Correspondência no Diagrama de Sequência: mensagens 76 / 98 getPreferencias(): Lista<PreferenciaAlimentar>.
     */
    public List<PreferenciaAlimentar> getPreferencias() {
        return Collections.unmodifiableList(preferencias);
    }

    public String getCelular() {
        return celular;
    }

    public List<CodigoConfirmacao> getCodigosEmitidos() {
        return Collections.unmodifiableList(codigosEmitidos);
    }

    public CodigoConfirmacao getUltimoCodigo() {
        if (codigosEmitidos.isEmpty()) return null;
        return codigosEmitidos.get(codigosEmitidos.size() - 1);
    }

    public void adicionarCodigoCarregado(CodigoConfirmacao codigo) {
        this.codigosEmitidos.add(codigo);
    }
}
