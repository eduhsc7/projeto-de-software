package entity;

/**
 * Parte exclusiva de um Pedido (composição) que registra a seleção de um ItemCardapio e sua quantidade.
 * Correspondência UML: «entity» ItemPedido.
 */
public class ItemPedido {
    private final ItemCardapio item;
    private int quantidade;

    /**
     * Correspondência no Diagrama de Sequência: mensagem 124 <<create>> ItemPedido(item: ItemCardapio, quantidade: Inteiro).
     */
    public ItemPedido(ItemCardapio item, int quantidade) {
        this.item = item;
        this.quantidade = quantidade;
    }

    /**
     * Altera a quantidade de um item já existente no mesmo pedido durante um ajuste (FA2).
     * Correspondência no Diagrama de Sequência: mensagem 125 alterarQuantidade(quantidade: Inteiro): void.
     */
    public void alterarQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public ItemCardapio getItem() {
        return item;
    }

    public int getQuantidade() {
        return quantidade;
    }

    @Override
    public String toString() {
        return String.format("%dx %s (%s)",
                quantidade,
                item.getOpcao().getNome(),
                item.getOpcao().getCategoria().getDescricao());
    }
}
