package entity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Tipo de dados que transporta plano, itens, periodicidade, endereço, previsão e protocolo
 * para a interface (Tela 15) após a autorização e ativação da assinatura.
 * Correspondência UML: «datatype» ConfirmacaoContratacao.
 */
public class ConfirmacaoContratacao {
    public final PlanoAssinatura plano;
    public final List<ItemPedido> itens;
    public final String periodicidade;
    public final EnderecoEntrega endereco;
    public final LocalDate previsaoPrimeiraEntrega;
    public final String protocolo;

    public ConfirmacaoContratacao(PlanoAssinatura plano,
                                  List<ItemPedido> itens,
                                  String periodicidade,
                                  EnderecoEntrega endereco,
                                  LocalDate previsaoPrimeiraEntrega,
                                  String protocolo) {
        this.plano = plano;
        this.itens = itens == null ? Collections.emptyList() : Collections.unmodifiableList(itens);
        this.periodicidade = periodicidade;
        this.endereco = endereco;
        this.previsaoPrimeiraEntrega = previsaoPrimeiraEntrega;
        this.protocolo = protocolo;
    }
}
