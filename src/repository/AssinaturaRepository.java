package repository;

import entity.Assinatura;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AssinaturaRepository {
    private static final String CAMINHO_ARQUIVO = "dados/assinaturas.csv";

    public void salvar(Assinatura assinatura) {
        try {
            File arquivo = new File(CAMINHO_ARQUIVO);
            File pasta = arquivo.getParentFile();
            if (pasta != null && !pasta.exists()) {
                pasta.mkdirs();
            }

            boolean arquivoNovo = !arquivo.exists();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo, true))) {
                if (arquivoNovo) {
                    writer.write("protocolo;numeroCelular;plano;produtos;endereco;statusPagamento");
                    writer.newLine();
                }

                writer.write(assinatura.getProtocolo() + ";"
                        + assinatura.getAssinante().getNumeroCelular() + ";"
                        + assinatura.getPlano().getNome() + ";"
                        + assinatura.getProdutosComoTexto() + ";"
                        + assinatura.getEndereco() + ";"
                        + assinatura.getStatusPagamento());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar assinatura no arquivo CSV: " + e.getMessage());
        }
    }
}
