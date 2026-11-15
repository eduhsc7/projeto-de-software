package entity;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entrada exclusiva de um Cardapio que oferece uma OpcaoCardapio (navegável nos dois sentidos).
 * Correspondência UML: «entity» ItemCardapio.
 */
public class ItemCardapio {
    private final Cardapio cardapio;
    private final OpcaoCardapio opcao;

    public ItemCardapio(Cardapio cardapio, OpcaoCardapio opcao) {
        this.cardapio = cardapio;
        this.opcao = opcao;
    }

    /**
     * Retorna a opção de cardápio (produto) associada a esta oferta.
     * Correspondência no Diagrama de Sequência: mensagens 80 / 104 / 111 getOpcao(): OpcaoCardapio.
     */
    public OpcaoCardapio getOpcao() {
        return opcao;
    }

    /**
     * Consulta o cardápio ao qual pertence para verificar se a oferta está vigente na data informada.
     * Correspondência no Diagrama de Sequência: mensagem 101 estaVigente(data: Data): Booleano.
     */
    public boolean estaVigente(LocalDate data) {
        return cardapio != null && cardapio.estaVigente(data);
    }

    public Cardapio getCardapio() {
        return cardapio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemCardapio)) return false;
        ItemCardapio that = (ItemCardapio) o;
        return Objects.equals(cardapio, that.cardapio) && Objects.equals(opcao, that.opcao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardapio, opcao);
    }

    @Override
    public String toString() {
        return opcao.toString();
    }
}
