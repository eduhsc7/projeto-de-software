package service;

import entity.Produto;
import java.util.ArrayList;
import java.util.List;

public class ServicoCatalogoProdutos {
    public List<Produto> obterProdutos() {
        List<Produto> produtos = new ArrayList<>();
        produtos.add(new Produto(1, "Arroz"));
        produtos.add(new Produto(2, "Feijão"));
        produtos.add(new Produto(3, "Café"));
        produtos.add(new Produto(4, "Leite"));
        produtos.add(new Produto(5, "Macarrão"));
        produtos.add(new Produto(6, "Açúcar"));
        return produtos;
    }
}
