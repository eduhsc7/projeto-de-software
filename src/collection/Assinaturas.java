package collection;

import entity.Assinante;
import entity.Assinatura;
import entity.EnderecoEntrega;
import entity.ItemPedido;
import entity.Pagamento;
import entity.Pedido;
import entity.PlanoAssinatura;

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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Coleção responsável por criar a Assinatura e manter seu estado persistido durante a contratação
 * (Passos 5, 10, 12 e FA3).
 * Correspondência UML: «collection» Assinaturas.
 *
 * Persistência: cada assinatura recebe um identificador único gerado pela coleção. A gravação
 * atualiza somente as linhas das assinaturas desta execução (upsert por id), preservando nos
 * arquivos CSV o histórico das execuções anteriores.
 */
public class Assinaturas {
    private static final String ARQUIVO_ASSINATURAS_CSV = "dados/assinaturas.csv";
    private static final String ARQUIVO_PEDIDOS_CSV = "dados/pedidos.csv";
    private static final String ARQUIVO_ITENS_PEDIDO_CSV = "dados/itens_pedido.csv";
    private static final String ARQUIVO_ENTREGAS_CSV = "dados/entregas.csv";
    private static final String ARQUIVO_PAGAMENTOS_CSV = "dados/pagamentos.csv";

    private static final String CAB_ASSINATURAS = "idAssinatura;celularAssinante;plano;valorContratado;statusAssinatura;statusPedido;endereco;previsaoPrimeiraEntrega;protocolo;tentativasPagamento";
    private static final String CAB_PEDIDOS = "idAssinatura;celularAssinante;statusPedido;selecaoConfirmada;totalRefeicoesPratoPrincipal;resumoItens";
    private static final String CAB_ITENS = "idAssinatura;celularAssinante;opcaoCardapio;categoria;quantidade";
    private static final String CAB_ENTREGAS = "idAssinatura;celularAssinante;plano;previsaoPrimeiraEntrega;enderecoCompleto";
    private static final String CAB_PAGAMENTOS = "idAssinatura;tentativaNumero;celularAssinante;valor;operadora;situacao;dataHoraSolicitacao;dataHoraConfirmacao";

    private static final DateTimeFormatter FMT_ID = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final List<Assinatura> assinaturas = new ArrayList<>();
    private final Map<Assinatura, String> ids = new IdentityHashMap<>();
    private int sequencial = 0;

    /**
     * Cria uma nova Assinatura em EM_PREENCHIMENTO (que instancia seu Pedido inicial em EM_MONTAGEM).
     * Correspondência no Diagrama de Sequência: mensagens 53 a 56 criar(assinante: Assinante, plano: PlanoAssinatura): Assinatura.
     */
    public Assinatura criar(Assinante assinante, PlanoAssinatura plano) {
        Assinatura assinatura = new Assinatura(assinante, plano);
        registrar(assinatura);
        persistirEstadoCompleto();
        return assinatura;
    }

    /**
     * Guarda/atualiza o estado da assinatura, de seu pedido, da primeira entrega e das tentativas de pagamento.
     * Correspondência no Diagrama de Sequência: mensagens 151, 171 e 182 guardar(assinatura: Assinatura): void.
     */
    public void guardar(Assinatura assinatura) {
        if (assinatura != null && !ids.containsKey(assinatura)) {
            registrar(assinatura);
        }
        persistirEstadoCompleto();
    }

    public List<Assinatura> listarTodas() {
        return Collections.unmodifiableList(assinaturas);
    }

    public String getId(Assinatura assinatura) {
        return ids.get(assinatura);
    }

    private void registrar(Assinatura assinatura) {
        sequencial++;
        String id = "A" + LocalDateTime.now().format(FMT_ID) + "-" + sequencial;
        assinaturas.add(assinatura);
        ids.put(assinatura, id);
    }

    private void persistirEstadoCompleto() {
        List<String> linhasAssinaturas = new ArrayList<>();
        List<String> linhasPedidos = new ArrayList<>();
        List<String> linhasItens = new ArrayList<>();
        List<String> linhasEntregas = new ArrayList<>();
        List<String> linhasPagamentos = new ArrayList<>();

        for (Assinatura a : assinaturas) {
            String id = ids.get(a);
            String celular = a.getAssinante().getCelular();
            Pedido p = a.getPedido();

            EnderecoEntrega end = a.getEndereco();
            linhasAssinaturas.add(String.format("%s;%s;%s;%.2f;%s;%s;%s;%s;%s;%d",
                    id,
                    celular,
                    a.getPlano().getNome(),
                    a.getValorContratado(),
                    a.getStatus().name(),
                    p.getStatus().name(),
                    end != null ? limpar(end.toString()) : "NAO_INFORMADO",
                    a.getPrevisaoPrimeiraEntrega() != null ? a.getPrevisaoPrimeiraEntrega().toString() : "NAO_DEFINIDA",
                    a.getNumeroProtocolo() != null ? a.getNumeroProtocolo() : "PENDENTE",
                    a.getTentativas().size()));

            String resumo = p.getItens().stream().map(ItemPedido::toString).collect(Collectors.joining(" | "));
            linhasPedidos.add(String.format("%s;%s;%s;%s;%d;%s",
                    id, celular, p.getStatus().name(), p.isSelecaoConfirmada(),
                    p.getTotalRefeicoesAtual(), limpar(resumo)));

            for (ItemPedido item : p.getItens()) {
                linhasItens.add(String.format("%s;%s;%s;%s;%d",
                        id, celular,
                        limpar(item.getItem().getOpcao().getNome()),
                        item.getItem().getOpcao().getCategoria().name(),
                        item.getQuantidade()));
            }

            if (a.getPrimeiraEntrega() != null) {
                linhasEntregas.add(String.format("%s;%s;%s;%s;%s",
                        id, celular, a.getPlano().getNome(),
                        a.getPrevisaoPrimeiraEntrega(), limpar(end.toString())));
            }

            int numTentativa = 1;
            for (Pagamento pg : a.getTentativas()) {
                linhasPagamentos.add(String.format("%s;%d;%s;%.2f;%s;%s;%s;%s",
                        id, numTentativa++, celular,
                        pg.getValor(),
                        pg.getOperadora().getNome(),
                        pg.getSituacao().name(),
                        pg.getDataHoraSolicitacao(),
                        pg.getDataHoraConfirmacao() != null ? pg.getDataHoraConfirmacao().toString() : ""));
            }
        }

        atualizarArquivo(ARQUIVO_ASSINATURAS_CSV, CAB_ASSINATURAS, linhasAssinaturas);
        atualizarArquivo(ARQUIVO_PEDIDOS_CSV, CAB_PEDIDOS, linhasPedidos);
        atualizarArquivo(ARQUIVO_ITENS_PEDIDO_CSV, CAB_ITENS, linhasItens);
        atualizarArquivo(ARQUIVO_ENTREGAS_CSV, CAB_ENTREGAS, linhasEntregas);
        atualizarArquivo(ARQUIVO_PAGAMENTOS_CSV, CAB_PAGAMENTOS, linhasPagamentos);
    }

    /**
     * Mantém as linhas de execuções anteriores e substitui somente as linhas cujos ids
     * pertencem às assinaturas desta execução.
     */
    private void atualizarArquivo(String caminho, String cabecalho, List<String> linhasAtuais) {
        File arquivo = new File(caminho);
        List<String> preservadas = new ArrayList<>();
        Set<String> idsDaSessao = new HashSet<>(ids.values());

        if (arquivo.exists()) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(arquivo), StandardCharsets.UTF_8))) {
                String linha;
                boolean primeira = true;
                while ((linha = reader.readLine()) != null) {
                    if (primeira) {
                        primeira = false;
                        continue;
                    }
                    if (linha.isBlank()) continue;
                    String id = linha.split(";", 2)[0];
                    if (!idsDaSessao.contains(id)) {
                        preservadas.add(linha);
                    }
                }
            } catch (IOException e) {
                System.err.println("Aviso ao ler " + caminho + ": " + e.getMessage());
            }
        }

        try {
            if (arquivo.getParentFile() != null) {
                arquivo.getParentFile().mkdirs();
            }
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(arquivo, false), StandardCharsets.UTF_8))) {
                writer.write(cabecalho);
                writer.newLine();
                for (String l : preservadas) {
                    writer.write(l);
                    writer.newLine();
                }
                for (String l : linhasAtuais) {
                    writer.write(l);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar " + caminho + ": " + e.getMessage());
        }
    }

    private String limpar(String s) {
        return s == null ? "" : s.replace(";", ",");
    }
}
