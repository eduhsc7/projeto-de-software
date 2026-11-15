package collection;

import entity.OpcaoCardapio;
import entity.PlanoAssinatura;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Coleção responsável por fornecer e persistir os planos de assinatura disponíveis (Passo 4).
 * Correspondência UML: «collection» Planos.
 */
public class Planos {
    private static final String ARQUIVO_CSV = "dados/planos.csv";
    private final List<PlanoAssinatura> planos = new ArrayList<>();

    public Planos(List<OpcaoCardapio> opcoesDoCatalogo) {
        inicializarPlanosPadrao(opcoesDoCatalogo);
    }

    /**
     * Retorna os planos de assinatura disponíveis para contratação.
     * Correspondência no Diagrama de Sequência: mensagem 47 listar(): Lista<PlanoAssinatura>.
     */
    public List<PlanoAssinatura> listar() {
        return Collections.unmodifiableList(planos);
    }

    private void inicializarPlanosPadrao(List<OpcaoCardapio> opcoesDoCatalogo) {
        // Os três planos correspondem exatamente ao Protótipo (Tela 4 - Planos disponíveis)
        planos.add(new PlanoAssinatura(
                "Plano Semanal",
                5,
                "Semanal",
                new BigDecimal("149.90"),
                opcoesDoCatalogo
        ));
        planos.add(new PlanoAssinatura(
                "Plano Mensal",
                20,
                "Semanal",
                new BigDecimal("519.90"),
                opcoesDoCatalogo
        ));
        planos.add(new PlanoAssinatura(
                "Plano Personalizado",
                10,
                "Quinzenal",
                new BigDecimal("279.90"),
                opcoesDoCatalogo
        ));

        salvarPlanosCSV();
    }

    private void salvarPlanosCSV() {
        try {
            File arquivo = new File(ARQUIVO_CSV);
            if (arquivo.getParentFile() != null) {
                arquivo.getParentFile().mkdirs();
            }
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(arquivo, false), StandardCharsets.UTF_8))) {
                writer.write("nome;quantidadeRefeicoes;periodicidadeEntrega;valor");
                writer.newLine();
                for (PlanoAssinatura p : planos) {
                    writer.write(String.format("%s;%d;%s;%.2f",
                            p.getNome(),
                            p.getQuantidadeRefeicoes(),
                            p.getPeriodicidadeEntrega(),
                            p.getValor()));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar " + ARQUIVO_CSV + ": " + e.getMessage());
        }
    }
}
