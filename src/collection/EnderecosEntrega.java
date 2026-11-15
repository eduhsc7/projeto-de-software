package collection;

import entity.DadosEndereco;
import entity.EnderecoEntrega;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Coleção responsável por criar, manter e persistir os endereços informados para entrega (Passo 10).
 * Correspondência UML: «collection» EnderecosEntrega.
 */
public class EnderecosEntrega {
    private static final String ARQUIVO_CSV = "dados/enderecos_entrega.csv";
    private final List<EnderecoEntrega> enderecos = new ArrayList<>();

    /**
     * Instancia e registra um novo EnderecoEntrega a partir de DadosEndereco.
     * Correspondência no Diagrama de Sequência: mensagens 140 a 142 cadastrar(dados: DadosEndereco): EnderecoEntrega.
     */
    public EnderecoEntrega cadastrar(DadosEndereco dados) {
        EnderecoEntrega endereco = new EnderecoEntrega(dados);
        this.enderecos.add(endereco);
        persistirNoCSV(endereco);
        return endereco;
    }

    public List<EnderecoEntrega> listar() {
        return Collections.unmodifiableList(enderecos);
    }

    private void persistirNoCSV(EnderecoEntrega endereco) {
        try {
            File arquivo = new File(ARQUIVO_CSV);
            if (arquivo.getParentFile() != null) {
                arquivo.getParentFile().mkdirs();
            }
            boolean novo = !arquivo.exists();
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(arquivo, true), StandardCharsets.UTF_8))) {
                if (novo) {
                    writer.write("logradouro;numero;complemento;bairro;cidade;uf;cep");
                    writer.newLine();
                }
                writer.write(String.format("%s;%s;%s;%s;%s;%s;%s",
                        limpar(endereco.getLogradouro()),
                        limpar(endereco.getNumero()),
                        limpar(endereco.getComplemento()),
                        limpar(endereco.getBairro()),
                        limpar(endereco.getCidade()),
                        limpar(endereco.getUf()),
                        limpar(endereco.getCep())));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar endereço em CSV: " + e.getMessage());
        }
    }

    private String limpar(String s) {
        return s == null ? "" : s.replace(";", ",");
    }
}
