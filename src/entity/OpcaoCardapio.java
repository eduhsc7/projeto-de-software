package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Representa o produto (alimento) reutilizável oferecido nos cardápios,
 * com nome, categoria (PRATO_PRINCIPAL, ACOMPANHAMENTO, SOBREMESA) e preferências atendidas.
 * Correspondência UML: «entity» OpcaoCardapio.
 */
public class OpcaoCardapio {
    private final String nome;
    private final CategoriaAlimento categoria;
    private final List<PreferenciaAlimentar> preferenciasAtendidas = new ArrayList<>();

    public OpcaoCardapio(String nome, CategoriaAlimento categoria, List<PreferenciaAlimentar> preferenciasAtendidas) {
        this.nome = nome;
        this.categoria = categoria;
        if (preferenciasAtendidas != null) {
            this.preferenciasAtendidas.addAll(preferenciasAtendidas);
        }
    }

    /**
     * Verifica se a opção do cardápio é compatível com as preferências alimentares do assinante.
     * Correspondência no Diagrama de Sequência: mensagens 84 / 108 atende(preferencias: Lista<PreferenciaAlimentar>): Booleano.
     */
    public boolean atende(List<PreferenciaAlimentar> preferencias) {
        if (preferencias == null || preferencias.isEmpty()) {
            return true;
        }
        return this.preferenciasAtendidas.containsAll(preferencias);
    }

    /**
     * Retorna a categoria do alimento (PRATO_PRINCIPAL, ACOMPANHAMENTO ou SOBREMESA).
     * Correspondência no Diagrama de Sequência: mensagem 113 getCategoria(): CategoriaAlimento.
     */
    public CategoriaAlimento getCategoria() {
        return categoria;
    }

    public String getNome() {
        return nome;
    }

    public List<PreferenciaAlimentar> getPreferenciasAtendidas() {
        return Collections.unmodifiableList(preferenciasAtendidas);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpcaoCardapio)) return false;
        OpcaoCardapio that = (OpcaoCardapio) o;
        return Objects.equals(nome, that.nome) && categoria == that.categoria;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, categoria);
    }

    @Override
    public String toString() {
        return nome + " [" + categoria.getDescricao() + "]";
    }
}
