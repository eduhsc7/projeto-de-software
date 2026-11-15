package entity;

/**
 * Tipo de dados para transporte das informações de endereço informadas na interface (Tela 11).
 * Correspondência UML: «datatype» DadosEndereco.
 */
public class DadosEndereco {
    public final String logradouro;
    public final String numero;
    public final String complemento;
    public final String bairro;
    public final String cidade;
    public final String uf;
    public final String cep;

    public DadosEndereco(String logradouro, String numero, String complemento,
                         String bairro, String cidade, String uf, String cep) {
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento == null ? "" : complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.cep = cep;
    }
}
