package entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Oferta de opções de alimentos em um período de vigência.
 * Compõe suas entradas exclusivas (ItemCardapio).
 * Correspondência UML: «entity» Cardapio.
 */
public class Cardapio {
    private final LocalDate inicioVigencia;
    private final LocalDate fimVigencia;
    private final List<ItemCardapio> ofertas = new ArrayList<>();

    public Cardapio(LocalDate inicioVigencia, LocalDate fimVigencia) {
        this.inicioVigencia = inicioVigencia;
        this.fimVigencia = fimVigencia;
    }

    /**
     * Adiciona uma opção ao cardápio criando a entrada exclusiva ItemCardapio (composição).
     * Garante a unicidade do par (Cardapio, OpcaoCardapio) conforme R7 do domínio.
     */
    public ItemCardapio adicionarOferta(OpcaoCardapio opcao) {
        for (ItemCardapio existente : ofertas) {
            if (existente.getOpcao().equals(opcao)) {
                return existente;
            }
        }
        ItemCardapio novoItem = new ItemCardapio(this, opcao);
        this.ofertas.add(novoItem);
        return novoItem;
    }

    /**
     * Filtra e retorna os itens do cardápio permitidos pelo plano e compatíveis com as preferências.
     * Correspondência no Diagrama de Sequência: mensagem 79 itensElegiveis(plano: PlanoAssinatura, preferencias: Lista<PreferenciaAlimentar>): Lista<ItemCardapio>.
     */
    public List<ItemCardapio> itensElegiveis(PlanoAssinatura plano, List<PreferenciaAlimentar> preferencias) {
        List<ItemCardapio> elegiveis = new ArrayList<>();
        for (ItemCardapio itemOferta : ofertas) {
            OpcaoCardapio produto = itemOferta.getOpcao();
            boolean permitida = plano.permiteOpcao(produto);
            boolean compativel = produto.atende(preferencias);
            if (permitida && compativel) {
                elegiveis.add(itemOferta);
            }
        }
        return elegiveis;
    }

    /**
     * Verifica se o cardápio está vigente na data informada (inicioVigencia <= data <= fimVigencia).
     * Correspondência no Diagrama de Sequência: mensagem 102 estaVigente(data: Data): Booleano.
     */
    public boolean estaVigente(LocalDate data) {
        if (data == null) return false;
        return !data.isBefore(inicioVigencia) && !data.isAfter(fimVigencia);
    }

    public LocalDate getInicioVigencia() {
        return inicioVigencia;
    }

    public LocalDate getFimVigencia() {
        return fimVigencia;
    }

    public List<ItemCardapio> getOfertas() {
        return Collections.unmodifiableList(ofertas);
    }
}
