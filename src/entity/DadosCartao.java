package entity;

/**
 * Tipo de dados para transporte dos dados do cartão de crédito informados na interface (Tela 13).
 * Conforme especificação de domínio e projeto, os dados do cartão não são armazenados.
 * Correspondência UML: «datatype» DadosCartao.
 */
public class DadosCartao {
    public final String numero;
    public final String nomeTitular;
    public final String validade;
    public final String cvv;

    public DadosCartao(String numero, String nomeTitular, String validade, String cvv) {
        this.numero = numero;
        this.nomeTitular = nomeTitular;
        this.validade = validade;
        this.cvv = cvv;
    }
}
