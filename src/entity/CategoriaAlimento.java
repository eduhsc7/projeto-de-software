package entity;

/**
 * Enumeração que classifica as opções do cardápio.
 * Correspondência UML: «enumeration» CategoriaAlimento.
 */
public enum CategoriaAlimento {
    PRATO_PRINCIPAL("Prato Principal"),
    ACOMPANHAMENTO("Acompanhamento"),
    SOBREMESA("Sobremesa");

    private final String descricao;

    CategoriaAlimento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
