package entity;

import java.util.Objects;

/**
 * Representa uma preferência alimentar declarada pelo assinante (ex.: Tradicional, Vegetariana, Sem lactose).
 * Correspondência UML: «entity» PreferenciaAlimentar.
 */
public class PreferenciaAlimentar {
    private final String descricao;

    public PreferenciaAlimentar(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PreferenciaAlimentar)) return false;
        PreferenciaAlimentar that = (PreferenciaAlimentar) o;
        return descricao != null && that.descricao != null
                && descricao.trim().equalsIgnoreCase(that.descricao.trim());
    }

    @Override
    public int hashCode() {
        return Objects.hash(descricao == null ? "" : descricao.trim().toLowerCase());
    }

    @Override
    public String toString() {
        return descricao;
    }
}
