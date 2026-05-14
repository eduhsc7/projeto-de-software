package entity;

import java.util.List;
import java.util.stream.Collectors;

public class Assinatura {
    private String protocolo;
    private Assinante assinante;
    private Plano plano;
    private List<Produto> produtos;
    private Endereco endereco;
    private String statusPagamento;

    public Assinatura(String protocolo, Assinante assinante, Plano plano, List<Produto> produtos, Endereco endereco, String statusPagamento) {
        this.protocolo = protocolo;
        this.assinante = assinante;
        this.plano = plano;
        this.produtos = produtos;
        this.endereco = endereco;
        this.statusPagamento = statusPagamento;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public Assinante getAssinante() {
        return assinante;
    }

    public Plano getPlano() {
        return plano;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public String getStatusPagamento() {
        return statusPagamento;
    }

    public String getProdutosComoTexto() {
        return produtos.stream().map(Produto::getNome).collect(Collectors.joining(", "));
    }
}
