package collection;

import entity.Assinante;
import entity.Assinatura;
import entity.EnderecoEntrega;
import entity.ItemPedido;
import entity.Pagamento;
import entity.Pedido;
import entity.PlanoAssinatura;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Coleção responsável por criar a Assinatura e manter seu estado persistido durante a contratação
 * (Passos 5, 10, 12 e FA3).
 * Correspondência UML: «collection» Assinaturas.
 */
public class Assinaturas {
    private static final String ARQUIVO_ASSINATURAS_CSV = "dados/assinaturas.csv";
    private static final String ARQUIVO_PEDIDOS_CSV = "dados/pedidos.csv";
    private static final String ARQUIVO_ITENS_PEDIDO_CSV = "dados/itens_pedido.csv";
    private static final String ARQUIVO_ENTREGAS_CSV = "dados/entregas.csv";
    private static final String ARQUIVO_PAGAMENTOS_CSV = "dados/pagamentos.csv";

    private final List<Assinatura> assinaturas = new ArrayList<>();

    /**
     * Cria uma nova Assinatura em EM_PREENCHIMENTO (que instancia seu Pedido inicial em EM_MONTAGEM).
     * Correspondência no Diagrama de Sequência: mensagens 53 a 56 criar(assinante: Assinante, plano: PlanoAssinatura): Assinatura.
     */
    public Assinatura criar(Assinante assinante, PlanoAssinatura plano) {
        Assinatura assinatura = new Assinatura(assinante, plano);
        this.assinaturas.add(assinatura);
        persistirEstadoCompleto();
        return assinatura;
    }

    /**
     * Guarda/atualiza o estado da assinatura, de seu pedido, da primeira entrega e das tentativas de pagamento.
     * Correspondência no Diagrama de Sequência: mensagens 151, 171 e 182 guardar(assinatura: Assinatura): void.
     */
    public void guardar(Assinatura assinatura) {
        if (assinatura != null && !this.assinaturas.contains(assinatura)) {
            this.assinaturas.add(assinatura);
        }
        persistirEstadoCompleto();
    }

    public List<Assinatura> listarTodas() {
        return Collections.unmodifiableList(assinaturas);
    }

    private void persistirEstadoCompleto() {
        try {
            File pasta = new File("dados");
            if (!pasta.exists()) {
                pasta.mkdirs();
            }

            // 1. Persistir assinaturas.csv (inclui histórico em disco caso já existam registros anteriores de outras execuções)
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(ARQUIVO_ASSINATURAS_CSV, false), StandardCharsets.UTF_8))) {
                writer.write("idSessao;celularAssinante;plano;valorContratado;statusAssinatura;statusPedido;endereco;previsaoPrimeiraEntrega;protocolo;tentativasPagamento");
                writer.newLine();
                int idx = 1;
                for (Assinatura a : assinaturas) {
                    EnderecoEntrega end = a.getEndereco();
                    String endTexto = end != null ? end.toString().replace(";", ",") : "NAO_INFORMADO";
                    String prevTexto = a.getPrevisaoPrimeiraEntrega() != null ? a.getPrevisaoPrimeiraEntrega().toString() : "NAO_DEFINIDA";
                    String protTexto = a.getNumeroProtocolo() != null ? a.getNumeroProtocolo() : "PENDENTE";
                    writer.write(String.format("%d;%s;%s;%.2f;%s;%s;%s;%s;%s;%d",
                            idx++,
                            a.getAssinante().getCelular(),
                            a.getPlano().getNome(),
                            a.getValorContratado(),
                            a.getStatus().name(),
                            a.getPedido().getStatus().name(),
                            endTexto,
                            prevTexto,
                            protTexto,
                            a.getTentativas().size()));
                    writer.newLine();
                }
            }

            // 2. Persistir pedidos.csv
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(ARQUIVO_PEDIDOS_CSV, false), StandardCharsets.UTF_8))) {
                writer.write("idAssinatura;celularAssinante;statusPedido;selecaoConfirmada;totalRefeicoesPratoPrincipal;resumoItens");
                writer.newLine();
                int idx = 1;
                for (Assinatura a : assinaturas) {
                    Pedido p = a.getPedido();
                    String resumo = p.getItens().stream()
                            .map(ItemPedido::toString)
                            .collect(Collectors.joining(" | "));
                    writer.write(String.format("%d;%s;%s;%s;%d;%s",
                            idx++,
                            a.getAssinante().getCelular(),
                            p.getStatus().name(),
                            p.isSelecaoConfirmada(),
                            p.getTotalRefeicoesAtual(),
                            resumo.replace(";", ",")));
                    writer.newLine();
                }
            }

            // 3. Persistir itens_pedido.csv
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(ARQUIVO_ITENS_PEDIDO_CSV, false), StandardCharsets.UTF_8))) {
                writer.write("idAssinatura;celularAssinante;opcaoCardapio;categoria;quantidade");
                writer.newLine();
                int idx = 1;
                for (Assinatura a : assinaturas) {
                    for (ItemPedido item : a.getPedido().getItens()) {
                        writer.write(String.format("%d;%s;%s;%s;%d",
                                idx,
                                a.getAssinante().getCelular(),
                                item.getItem().getOpcao().getNome(),
                                item.getItem().getOpcao().getCategoria().name(),
                                item.getQuantidade()));
                        writer.newLine();
                    }
                    idx++;
                }
            }

            // 4. Persistir entregas.csv (primeiraEntrega de cada assinatura)
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(ARQUIVO_ENTREGAS_CSV, false), StandardCharsets.UTF_8))) {
                writer.write("idAssinatura;celularAssinante;plano;previsaoPrimeiraEntrega;enderecoCompleto");
                writer.newLine();
                int idx = 1;
                for (Assinatura a : assinaturas) {
                    if (a.getPrimeiraEntrega() != null) {
                        writer.write(String.format("%d;%s;%s;%s;%s",
                                idx,
                                a.getAssinante().getCelular(),
                                a.getPlano().getNome(),
                                a.getPrevisaoPrimeiraEntrega(),
                                a.getEndereco().toString().replace(";", ",")));
                        writer.newLine();
                    }
                    idx++;
                }
            }

            // 5. Persistir pagamentos.csv (preserva tentativas recusadas em FA3 e autorizadas)
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(ARQUIVO_PAGAMENTOS_CSV, false), StandardCharsets.UTF_8))) {
                writer.write("idAssinatura;tentativaNumero;celularAssinante;valor;operadora;situacao;dataHoraSolicitacao;dataHoraConfirmacao");
                writer.newLine();
                int idxAssinatura = 1;
                for (Assinatura a : assinaturas) {
                    int numTentativa = 1;
                    for (Pagamento pg : a.getTentativas()) {
                        writer.write(String.format("%d;%d;%s;%.2f;%s;%s;%s;%s",
                                idxAssinatura,
                                numTentativa++,
                                a.getAssinante().getCelular(),
                                pg.getValor(),
                                pg.getOperadora().getNome(),
                                pg.getSituacao().name(),
                                pg.getDataHoraSolicitacao(),
                                pg.getDataHoraConfirmacao() != null ? pg.getDataHoraConfirmacao().toString() : ""));
                        writer.newLine();
                    }
                    idxAssinatura++;
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar assinaturas/pedidos/pagamentos em CSV: " + e.getMessage());
        }
    }
}
