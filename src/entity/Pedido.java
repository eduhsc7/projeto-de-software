package entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Pedido inicial da assinatura, criado vazio em EM_MONTAGEM junto com a Assinatura.
 * Administra seus itens (ItemPedido), valida as regras de seleção (FA2) e controla a confirmação do resumo.
 * Correspondência UML: «entity» Pedido.
 */
public class Pedido {
    private SituacaoPedido status;
    private boolean selecaoConfirmada = false;
    private final Assinatura assinatura;
    private final List<ItemPedido> itens = new ArrayList<>();

    /**
     * Cria o pedido inicial em EM_MONTAGEM e vazio.
     * Correspondência no Diagrama de Sequência: mensagem 55 <<create>> Pedido(assinatura: Assinatura).
     */
    public Pedido(Assinatura assinatura) {
        this.assinatura = assinatura;
        this.status = SituacaoPedido.EM_MONTAGEM;
        this.selecaoConfirmada = false;
    }

    /**
     * Valida a seleção informada e, somente se satisfizer todas as regras, sincroniza os itens do pedido.
     * Caso inválida (FA2), preserva os itens anteriores sem alterações e retorna false.
     * Correspondência no Diagrama de Sequência: mensagem 99 definirSelecao(...): Booleano.
     */
    public boolean definirSelecao(Map<ItemCardapio, Integer> selecao,
                                  PlanoAssinatura plano,
                                  List<PreferenciaAlimentar> preferencias,
                                  LocalDate data) {
        boolean selecaoValida = validarSelecao(selecao, plano, preferencias, data);
        if (!selecaoValida) {
            return false;
        }
        sincronizarItens(selecao);
        return true;
    }

    /**
     * Verifica se a seleção não é vazia, possui quantidades positivas, ofertas vigentes,
     * permitidas pelo plano, compatíveis com as preferências e dentro do limite de refeições do plano.
     * Correspondência no Diagrama de Sequência: mensagem 100 validarSelecao(...): Booleano.
     */
    private boolean validarSelecao(Map<ItemCardapio, Integer> selecao,
                                   PlanoAssinatura plano,
                                   List<PreferenciaAlimentar> preferencias,
                                   LocalDate data) {
        if (selecao == null || selecao.isEmpty() || plano == null) {
            return false;
        }
        for (Map.Entry<ItemCardapio, Integer> entrada : selecao.entrySet()) {
            ItemCardapio itemOferta = entrada.getKey();
            Integer quantidade = entrada.getValue();
            if (itemOferta == null || quantidade == null || quantidade <= 0) {
                return false;
            }
            boolean vigente = itemOferta.estaVigente(data);
            OpcaoCardapio produto = itemOferta.getOpcao();
            boolean permitida = plano.permiteOpcao(produto);
            boolean compativel = produto.atende(preferencias);
            if (!vigente || !permitida || !compativel) {
                return false;
            }
        }
        int totalRefeicoes = calcularTotalRefeicoes(selecao);
        return plano.permiteQuantidade(totalRefeicoes);
    }

    /**
     * Soma apenas as quantidades dos itens cuja categoria é PRATO_PRINCIPAL.
     * Correspondência no Diagrama de Sequência: mensagem 110 calcularTotalRefeicoes(selecao: Mapa<ItemCardapio, Inteiro>): Inteiro.
     */
    private int calcularTotalRefeicoes(Map<ItemCardapio, Integer> selecao) {
        int totalRefeicoes = 0;
        for (Map.Entry<ItemCardapio, Integer> entrada : selecao.entrySet()) {
            ItemCardapio itemOferta = entrada.getKey();
            Integer quantidade = entrada.getValue();
            if (itemOferta != null && quantidade != null && quantidade > 0) {
                OpcaoCardapio produto = itemOferta.getOpcao();
                CategoriaAlimento categoria = produto.getCategoria();
                if (categoria == CategoriaAlimento.PRATO_PRINCIPAL) {
                    totalRefeicoes += quantidade;
                }
            }
        }
        return totalRefeicoes;
    }

    /**
     * Sincroniza os itens do pedido: cria novos ItemPedido, altera a quantidade dos existentes
     * e remove os itens desmarcados, redefinindo selecaoConfirmada = false.
     * Correspondência no Diagrama de Sequência: mensagem 121 sincronizarItens(selecao: Mapa<ItemCardapio, Inteiro>): void.
     */
    private void sincronizarItens(Map<ItemCardapio, Integer> selecao) {
        for (Map.Entry<ItemCardapio, Integer> entrada : selecao.entrySet()) {
            ItemCardapio itemOferta = entrada.getKey();
            int quantidade = entrada.getValue();
            ItemPedido itemExistente = buscarItem(itemOferta);
            if (itemExistente == null) {
                ItemPedido novoItem = new ItemPedido(itemOferta, quantidade);
                this.itens.add(novoItem);
            } else {
                itemExistente.alterarQuantidade(quantidade);
            }
        }
        List<ItemPedido> paraRemover = new ArrayList<>();
        for (ItemPedido existente : this.itens) {
            if (!selecao.containsKey(existente.getItem())) {
                paraRemover.add(existente);
            }
        }
        for (ItemPedido desmarcado : paraRemover) {
            removerItem(desmarcado);
        }
        this.selecaoConfirmada = false;
    }

    /**
     * Busca um ItemPedido já existente no pedido para a mesma entrada do cardápio [0..1].
     * Correspondência no Diagrama de Sequência: mensagem 122 buscarItem(item: ItemCardapio): ItemPedido [0..1].
     */
    private ItemPedido buscarItem(ItemCardapio item) {
        for (ItemPedido ip : this.itens) {
            if (ip.getItem().equals(item)) {
                return ip;
            }
        }
        return null;
    }

    /**
     * Remove um ItemPedido desmarcado em um ajuste de seleção (FA2).
     * Correspondência no Diagrama de Sequência: mensagem 126 removerItem(item: ItemPedido): void.
     */
    private void removerItem(ItemPedido item) {
        Iterator<ItemPedido> it = this.itens.iterator();
        while (it.hasNext()) {
            if (it.next() == item) {
                it.remove();
                break;
            }
        }
    }

    /**
     * Confirma explicitamente o resumo da seleção do pedido.
     * O pedido permanece EM_MONTAGEM até a autorização do pagamento.
     * Correspondência no Diagrama de Sequência: mensagem 137 confirmarSelecao(): Booleano.
     */
    public boolean confirmarSelecao() {
        if (this.itens.isEmpty()) {
            return false;
        }
        this.selecaoConfirmada = true;
        return true;
    }

    /**
     * Retorna a lista de itens atualmente no pedido para apresentação do resumo.
     * Correspondência no Diagrama de Sequência: mensagem 131 getItens(): Lista<ItemPedido>.
     */
    public List<ItemPedido> getItens() {
        return Collections.unmodifiableList(this.itens);
    }

    /**
     * Aprova o pedido inicial após a autorização do pagamento.
     * Correspondência no Diagrama de Sequência: mensagem 170 aprovar(): void.
     */
    public void aprovar() {
        if (!this.selecaoConfirmada || this.itens.isEmpty()) {
            throw new IllegalStateException("Não é possível aprovar um pedido sem seleção confirmada e itens válidos.");
        }
        this.status = SituacaoPedido.APROVADO;
    }

    public SituacaoPedido getStatus() {
        return status;
    }

    public boolean isSelecaoConfirmada() {
        return selecaoConfirmada;
    }

    public Assinatura getAssinatura() {
        return assinatura;
    }

    public int getTotalRefeicoesAtual() {
        int total = 0;
        for (ItemPedido ip : itens) {
            if (ip.getItem().getOpcao().getCategoria() == CategoriaAlimento.PRATO_PRINCIPAL) {
                total += ip.getQuantidade();
            }
        }
        return total;
    }
}
