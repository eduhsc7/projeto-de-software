package collection;

import entity.PreferenciaAlimentar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Coleção responsável por fornecer e persistir as preferências alimentares disponíveis (Passo 6).
 * Correspondência UML: «collection» PreferenciasAlimentares.
 */
public class PreferenciasAlimentares {
    private static final String ARQUIVO_CSV = "dados/preferencias_alimentares.csv";
    private final List<PreferenciaAlimentar> preferencias = new ArrayList<>();

    public PreferenciasAlimentares() {
        carregarOuInicializarPadrao();
    }

    /**
     * Lista todas as preferências alimentares disponíveis para seleção.
     * Correspondência no Diagrama de Sequência: mensagem 64 listar(): Lista<PreferenciaAlimentar>.
     */
    public List<PreferenciaAlimentar> listar() {
        return Collections.unmodifiableList(preferencias);
    }

    public PreferenciaAlimentar buscarPorDescricao(String descricao) {
        if (descricao == null) return null;
        for (PreferenciaAlimentar p : preferencias) {
            if (p.getDescricao().equalsIgnoreCase(descricao.trim())) {
                return p;
            }
        }
        return new PreferenciaAlimentar(descricao.trim());
    }

    private void carregarOuInicializarPadrao() {
        File arquivo = new File(ARQUIVO_CSV);
        if (arquivo.exists()) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(arquivo), StandardCharsets.UTF_8))) {
                String linha;
                boolean cabecalho = true;
                while ((linha = reader.readLine()) != null) {
                    if (cabecalho) {
                        cabecalho = false;
                        continue;
                    }
                    String desc = linha.trim();
                    if (!desc.isEmpty()) {
                        preferencias.add(new PreferenciaAlimentar(desc));
                    }
                }
            } catch (IOException e) {
                System.err.println("Aviso ao ler " + ARQUIVO_CSV + ": " + e.getMessage());
            }
        }

        if (preferencias.isEmpty()) {
            preferencias.add(new PreferenciaAlimentar("Tradicional"));
            preferencias.add(new PreferenciaAlimentar("Vegetariana"));
            preferencias.add(new PreferenciaAlimentar("Sem lactose"));
            salvarPadrao();
        }
    }

    private void salvarPadrao() {
        try {
            File arquivo = new File(ARQUIVO_CSV);
            if (arquivo.getParentFile() != null) {
                arquivo.getParentFile().mkdirs();
            }
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(arquivo, false), StandardCharsets.UTF_8))) {
                writer.write("descricao");
                writer.newLine();
                for (PreferenciaAlimentar p : preferencias) {
                    writer.write(p.getDescricao());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar " + ARQUIVO_CSV + ": " + e.getMessage());
        }
    }
}
