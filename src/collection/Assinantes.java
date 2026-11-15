package collection;

import entity.Assinante;
import entity.CodigoConfirmacao;
import entity.PreferenciaAlimentar;
import entity.SituacaoCodigo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Coleção responsável por localizar o assinante pelo celular, criar seu cadastro quando
 * necessário e manter seu estado persistido em arquivos CSV (Passo 2).
 * Correspondência UML: «collection» Assinantes.
 */
public class Assinantes {
    private static final String ARQUIVO_ASSINANTES_CSV = "dados/assinantes.csv";
    private static final String ARQUIVO_CODIGOS_CSV = "dados/codigos_confirmacao.csv";

    private final List<Assinante> assinantes = new ArrayList<>();

    public Assinantes() {
        carregarDoCSV();
    }

    /**
     * Localiza um assinante existente pelo celular ou cria um novo caso ainda não cadastrado.
     * Correspondência no Diagrama de Sequência: mensagens 3 a 8 localizarOuCadastrar(celular: Texto): Assinante.
     */
    public Assinante localizarOuCadastrar(String celular) {
        String celularNormalizado = celular == null ? "" : celular.trim();
        Assinante existente = buscarPorCelular(celularNormalizado);
        if (existente != null) {
            return existente;
        }
        Assinante novoAssinante = new Assinante(celularNormalizado);
        this.assinantes.add(novoAssinante);
        persistirTodos();
        return novoAssinante;
    }

    /**
     * Busca na coleção um assinante pelo número de celular [0..1].
     * Correspondência no Diagrama de Sequência: mensagem 4 buscarPorCelular(celular: Texto): Assinante [0..1].
     */
    private Assinante buscarPorCelular(String celular) {
        for (Assinante a : assinantes) {
            if (a.getCelular().equalsIgnoreCase(celular)) {
                return a;
            }
        }
        return null;
    }

    /**
     * Sincroniza o estado dos assinantes e seus códigos de confirmação no CSV.
     */
    public void guardar(Assinante assinante) {
        if (assinante != null && buscarPorCelular(assinante.getCelular()) == null) {
            this.assinantes.add(assinante);
        }
        persistirTodos();
    }

    public List<Assinante> listarTodos() {
        return Collections.unmodifiableList(assinantes);
    }

    private void carregarDoCSV() {
        File arqAssinantes = new File(ARQUIVO_ASSINANTES_CSV);
        if (arqAssinantes.exists()) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(arqAssinantes), StandardCharsets.UTF_8))) {
                String linha;
                boolean cabecalho = true;
                while ((linha = reader.readLine()) != null) {
                    if (cabecalho) {
                        cabecalho = false;
                        continue;
                    }
                    if (linha.isBlank()) continue;
                    String[] partes = linha.split(";", -1);
                    if (partes.length >= 1 && !partes[0].isBlank()) {
                        Assinante a = new Assinante(partes[0].trim());
                        if (partes.length >= 3 && !partes[2].isBlank()) {
                            List<PreferenciaAlimentar> prefs = new ArrayList<>();
                            for (String p : partes[2].split(",")) {
                                if (!p.isBlank()) {
                                    prefs.add(new PreferenciaAlimentar(p.trim()));
                                }
                            }
                            a.definirPreferencias(prefs);
                        }
                        this.assinantes.add(a);
                    }
                }
            } catch (IOException e) {
                System.err.println("Aviso ao carregar " + ARQUIVO_ASSINANTES_CSV + ": " + e.getMessage());
            }
        }

        File arqCodigos = new File(ARQUIVO_CODIGOS_CSV);
        if (arqCodigos.exists()) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(arqCodigos), StandardCharsets.UTF_8))) {
                String linha;
                boolean cabecalho = true;
                while ((linha = reader.readLine()) != null) {
                    if (cabecalho) {
                        cabecalho = false;
                        continue;
                    }
                    if (linha.isBlank()) continue;
                    String[] partes = linha.split(";", -1);
                    if (partes.length >= 5) {
                        String cel = partes[0].trim();
                        String valor = partes[1].trim();
                        LocalDateTime emissao = LocalDateTime.parse(partes[2].trim());
                        int tentativas = Integer.parseInt(partes[3].trim());
                        SituacaoCodigo situacao = SituacaoCodigo.valueOf(partes[4].trim());
                        Assinante a = buscarPorCelular(cel);
                        if (a != null) {
                            a.adicionarCodigoCarregado(new CodigoConfirmacao(valor, emissao, tentativas, situacao));
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Aviso ao carregar " + ARQUIVO_CODIGOS_CSV + ": " + e.getMessage());
            }
        }
    }

    private void persistirTodos() {
        try {
            File arqAssinantes = new File(ARQUIVO_ASSINANTES_CSV);
            if (arqAssinantes.getParentFile() != null) {
                arqAssinantes.getParentFile().mkdirs();
            }
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(arqAssinantes, false), StandardCharsets.UTF_8))) {
                writer.write("celular;celularValidado;preferencias;totalCodigosEmitidos");
                writer.newLine();
                for (Assinante a : assinantes) {
                    String prefs = a.getPreferencias().stream()
                            .map(PreferenciaAlimentar::getDescricao)
                            .collect(Collectors.joining(","));
                    writer.write(String.format("%s;%s;%s;%d",
                            a.getCelular(),
                            a.celularValidado(),
                            prefs,
                            a.getCodigosEmitidos().size()));
                    writer.newLine();
                }
            }

            File arqCodigos = new File(ARQUIVO_CODIGOS_CSV);
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(arqCodigos, false), StandardCharsets.UTF_8))) {
                writer.write("celularAssinante;valorCodigo;dataHoraEmissao;tentativasInvalidas;situacao");
                writer.newLine();
                for (Assinante a : assinantes) {
                    for (CodigoConfirmacao c : a.getCodigosEmitidos()) {
                        writer.write(String.format("%s;%s;%s;%d;%s",
                                a.getCelular(),
                                c.getValor(),
                                c.getDataHoraEmissao(),
                                c.getTentativasInvalidas(),
                                c.getSituacao().name()));
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao persistir assinantes/códigos em CSV: " + e.getMessage());
        }
    }
}
