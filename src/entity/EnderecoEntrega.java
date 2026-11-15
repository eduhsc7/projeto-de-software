package entity;

/**
 * Representa o endereço cadastrado para entrega das refeições.
 * Correspondência UML: «entity» EnderecoEntrega.
 */
public class EnderecoEntrega {
    private final String logradouro;
    private final String numero;
    private final String complemento;
    private final String bairro;
    private final String cidade;
    private final String uf;
    private final String cep;

    /**
     * Correspondência no Diagrama de Sequência: mensagem 141 <<create>> EnderecoEntrega(dados: DadosEndereco).
     */
    public EnderecoEntrega(DadosEndereco dados) {
        this.logradouro = dados.logradouro;
        this.numero = dados.numero;
        this.complemento = dados.complemento == null ? "" : dados.complemento;
        this.bairro = dados.bairro;
        this.cidade = dados.cidade;
        this.uf = dados.uf;
        this.cep = dados.cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public String getUf() {
        return uf;
    }

    public String getCep() {
        return cep;
    }

    @Override
    public String toString() {
        String comp = (complemento != null && !complemento.isBlank()) ? " (" + complemento + ")" : "";
        return String.format("%s, %s%s - %s, %s/%s - CEP: %s",
                logradouro, numero, comp, bairro, cidade, uf, cep);
    }
}
