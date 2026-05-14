package entity;

public class DadosPagamento {
    private String titular;
    private String numeroCartao;
    private String validade;
    private String cvv;

    public DadosPagamento(String titular, String numeroCartao, String validade, String cvv) {
        this.titular = titular;
        this.numeroCartao = numeroCartao;
        this.validade = validade;
        this.cvv = cvv;
    }

    public String getTitular() {
        return titular;
    }

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public String getValidade() {
        return validade;
    }

    public String getCvv() {
        return cvv;
    }
}
