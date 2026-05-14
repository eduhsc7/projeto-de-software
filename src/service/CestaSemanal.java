package service;

import entity.Produto;
import java.util.ArrayList;
import java.util.List;

public class CestaSemanal {
    private List<Produto> produtos = new ArrayList<>();

    public void adicionar(List<Produto> produtos) {
        this.produtos.addAll(produtos);
    }

    public List<Produto> getProdutos() {
        return produtos;
    }
}
