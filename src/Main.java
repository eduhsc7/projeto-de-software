import boundary.TelaContratacao;
import boundary.TelaIdentificacao;
import boundary.TelaPagamento;
import controller.AssinaturaController;
import entity.Assinante;
import entity.Assinatura;
import entity.CodigoConfirmacao;
import entity.ConfirmacaoContratacao;
import entity.DadosCartao;
import entity.DadosEndereco;
import entity.ItemCardapio;
import entity.ItemPedido;
import entity.PlanoAssinatura;
import entity.PreferenciaAlimentar;
import entity.SituacaoCodigo;
import entity.SituacaoPagamento;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Ponto de entrada da aplicação Java do projeto "Minha Refeição" — Serviço de Assinatura de Marmitas.
 * Traduz o Diagrama de Sequência Completo (10-sequencia-completa) e o Diagrama de Classes de Projeto
 * em uma implementação prática com objetos Boundary, Control, Collection (com persistência CSV) e Entity.
 */
public class Main {
    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        } catch (Exception ignored) {
        }

        AssinaturaController controller = new AssinaturaController();

        if (args.length > 0 && args[0].equalsIgnoreCase("--demo")) {
            executarDemonstracaoCompleta(controller);
            exibirArquivosCSV();
            return;
        }

        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        TelaIdentificacao telaIdentificacao = new TelaIdentificacao(controller, scanner);
        TelaContratacao telaContratacao = new TelaContratacao(controller, scanner);
        TelaPagamento telaPagamento = new TelaPagamento(controller, scanner);

        while (true) {
            System.out.println("\n======================================================================");
            System.out.println("      MINHA REFEIÇÃO — SERVIÇO DE ASSINATURA DE MARMITAS (JAVA)       ");
            System.out.println("      Caso de Uso: Assinar Plano de Refeições (Principal + FA1–FA3)   ");
            System.out.println("======================================================================");
            System.out.println(" [1] Executar Contratação Interativa (Telas 01 a 15 no Terminal)");
            System.out.println(" [2] Executar Demonstração Guiada Automática (Fluxo Principal + FA1, FA2 e FA3)");
            System.out.println(" [3] Consultar Dados Persistidos nos Arquivos CSV (pasta dados/)");
            System.out.println(" [0] Sair");
            System.out.println("----------------------------------------------------------------------");
            System.out.print("Escolha uma opção: ");

            if (!scanner.hasNextLine()) {
                break;
            }
            String opcao = scanner.nextLine().trim();

            switch (opcao) {
                case "1":
                    Assinante assinante = telaIdentificacao.realizarIdentificacao();
                    Assinatura assinatura = telaContratacao.realizarContratacao(assinante);
                    telaPagamento.realizarPagamento(assinatura);
                    break;
                case "2":
                    executarDemonstracaoCompleta(controller);
                    break;
                case "3":
                    exibirArquivosCSV();
                    break;
                case "0":
                    System.out.println("\nEncerrando o sistema Minha Refeição. Até logo!");
                    return;
                default:
                    System.out.println(" -> Opção inválida. Digite 1, 2, 3 ou 0.");
            }
        }
    }

    /**
     * Executa e narra passo a passo o Diagrama de Sequência Completo (10-sequencia-completa),
     * exercitando tanto os Fluxos Alternativos (FA1, FA2 e FA3) quanto o Fluxo Principal até a confirmação.
     */
    private static void executarDemonstracaoCompleta(AssinaturaController controller) {
        System.out.println("\n######################################################################");
        System.out.println(" DEMONSTRAÇÃO GUIADA: DIAGRAMA DE SEQUÊNCIA COMPLETO (PRINCIPAL + FA1..FA3)");
        System.out.println("######################################################################");

        // =====================================================================
        // GRUPO 1: Iniciar contratação e validar celular | Telas 1 a 3 | FA1
        // =====================================================================
        System.out.println("\n>>> [ETAPA 1 — TELAS 01 a 03] Iniciar contratação e validar celular (com FA1)");
        String celular = "11991234567";
        System.out.println("1. [TelaIdentificacao -> AssinaturaController] iniciarContratacao(\"" + celular + "\")");
        Assinante assinante = controller.iniciarContratacao(celular);

        // Demonstrar FA1: 3 tentativas inválidas levando ao BLOQUEIO do 1º código
        System.out.println("\n--- Exercitando FA1 (Código Inválido e Bloqueio na 3ª tentativa) ---");
        for (int tentativa = 1; tentativa <= 3; tentativa++) {
            SituacaoCodigo sit = controller.confirmarCelular(assinante, "0000");
            CodigoConfirmacao codAtual = assinante.getUltimoCodigo();
            System.out.println("   Tentativa inválida #" + tentativa + " com '0000' -> SituacaoCodigo = " + sit
                    + " (tentativasInvalidas = " + codAtual.getTentativasInvalidas() + ")");
        }

        // Solicitar novo código após bloqueio
        System.out.println("   [FA1] Código bloqueado! Solicitando reenviarCodigo(assinante)...");
        controller.reenviarCodigo(assinante);

        // Demonstrar FA1: reenvio enquanto há código PENDENTE (substitui o anterior para SUBSTITUIDO)
        System.out.println("   [FA1] Solicitando reenviarCodigo(assinante) antes de validar para demonstrar SUBSTITUIDO...");
        controller.reenviarCodigo(assinante);

        // Validar com o código correto atual
        String codigoCorreto = assinante.getUltimoCodigo().getValor();
        SituacaoCodigo sitFinal = controller.confirmarCelular(assinante, codigoCorreto);
        System.out.println("   [Fluxo Principal] Informando código correto (" + codigoCorreto + ") -> SituacaoCodigo = "
                + sitFinal + " | celularValidado() = " + assinante.celularValidado());

        // =====================================================================
        // GRUPO 2: Consultar e contratar plano | Telas 4 e 5
        // =====================================================================
        System.out.println("\n>>> [ETAPA 2 — TELAS 04 e 05] Consultar e contratar plano");
        List<PlanoAssinatura> planos = controller.consultarPlanos();
        PlanoAssinatura planoSemanal = planos.get(0); // Plano Semanal (5 refeições)
        System.out.println("2. Planos consultados: " + planos.size() + " disponíveis. Selecionado: " + planoSemanal);

        Assinatura assinatura = controller.contratarPlano(assinante, planoSemanal);
        System.out.println("3. [AssinaturaController -> Assinaturas] criar(assinante, plano) -> Assinatura status = "
                + assinatura.getStatus() + " | Pedido inicial status = " + assinatura.getPedido().getStatus());

        // =====================================================================
        // GRUPO 3: Definir preferências e consultar refeições | Telas 6 a 9
        // =====================================================================
        System.out.println("\n>>> [ETAPA 3 — TELAS 06 a 09] Definir preferências e consultar refeições elegíveis");
        List<PreferenciaAlimentar> todasPrefs = controller.consultarPreferencias();
        List<PreferenciaAlimentar> prefsEscolhidas = Arrays.asList(todasPrefs.get(0), todasPrefs.get(2)); // Tradicional, Sem lactose
        controller.definirPreferencias(assinante, prefsEscolhidas);
        System.out.println("4. Preferências definidas para o assinante: " + assinante.getPreferencias());

        List<ItemCardapio> elegiveis = controller.consultarRefeicoes(assinatura);
        System.out.println("5. Itens elegíveis no cardápio vigente compatíveis com plano e preferências (" + elegiveis.size() + " opções):");
        for (int i = 0; i < elegiveis.size(); i++) {
            System.out.println("   [" + (i + 1) + "] " + elegiveis.get(i));
        }

        // =====================================================================
        // GRUPO 4: Montar e confirmar pedido inicial | Telas 7 a 10 | FA2
        // =====================================================================
        System.out.println("\n>>> [ETAPA 4 — TELAS 07 a 10] Montar e confirmar pedido inicial (com FA2)");

        // FA2 Caso A: Seleção vazia
        System.out.println("--- Exercitando FA2 (Caso A: Seleção vazia) ---");
        boolean resVazio = controller.montarPedido(assinatura, Collections.emptyMap());
        System.out.println("   montarPedido(seleção vazia) -> retorno = " + resVazio);

        // FA2 Caso B: Excedendo o limite de 5 refeições (Pratos Principais) do Plano Semanal
        System.out.println("--- Exercitando FA2 (Caso B: Excedendo limite de " + planoSemanal.getQuantidadeRefeicoes() + " Pratos Principais do plano) ---");
        Map<ItemCardapio, Integer> selecaoExcedente = new LinkedHashMap<>();
        selecaoExcedente.put(elegiveis.get(0), 4); // 4 Pratos Principais
        selecaoExcedente.put(elegiveis.get(1), 3); // +3 Pratos Principais = 7 (> 5)
        boolean resExcedente = controller.montarPedido(assinatura, selecaoExcedente);
        System.out.println("   montarPedido(7 Pratos Principais em plano de 5) -> retorno = " + resExcedente);

        // Fluxo Principal: Seleção válida inicial
        System.out.println("--- Selecionando composição inicial válida (4 Pratos Principais + Acompanhamentos + Sobremesa) ---");
        Map<ItemCardapio, Integer> selecaoValida1 = new LinkedHashMap<>();
        selecaoValida1.put(elegiveis.get(0), 2); // Prato Principal 1 (2x)
        selecaoValida1.put(elegiveis.get(1), 2); // Prato Principal 2 (2x)
        selecaoValida1.put(elegiveis.get(4), 2); // Acompanhamento 1 (2x)
        selecaoValida1.put(elegiveis.get(7), 1); // Sobremesa 1 (1x)
        boolean resValido1 = controller.montarPedido(assinatura, selecaoValida1);
        System.out.println("   montarPedido(selecaoValida1) -> retorno = " + resValido1);
        System.out.println("   consultarResumoPedido -> " + controller.consultarResumoPedido(assinatura));

        // FA2 Caso C: Usuário decide ajustar o pedido no resumo (alterarQuantidade, criar novoItem e removerItem)
        System.out.println("--- Exercitando FA2 (Caso C: Ajustar composição no resumo — sincronizarItens) ---");
        Map<ItemCardapio, Integer> selecaoAjustada = new LinkedHashMap<>();
        selecaoAjustada.put(elegiveis.get(0), 3); // Altera quantidade de 2 para 3 (ItemPedido.alterarQuantidade)
        selecaoAjustada.put(elegiveis.get(2), 2); // Novo prato adicionado (<<create>> ItemPedido) — total 5 refeições
        selecaoAjustada.put(elegiveis.get(4), 2); // Mantém acompanhamento
        selecaoAjustada.put(elegiveis.get(8), 2); // Nova sobremesa (e remove elegiveis.get(1) e elegiveis.get(7))
        boolean resAjuste = controller.montarPedido(assinatura, selecaoAjustada);
        System.out.println("   montarPedido(selecaoAjustada com 5 refeições) -> retorno = " + resAjuste);
        List<ItemPedido> resumoFinal = controller.consultarResumoPedido(assinatura);
        System.out.println("   consultarResumoPedido atualizado -> " + resumoFinal);

        // Confirmar resumo do pedido
        boolean pedidoConfirmado = controller.confirmarPedido(assinatura);
        System.out.println("6. confirmarPedido(assinatura) -> " + pedidoConfirmado
                + " (selecaoConfirmada = " + assinatura.getPedido().isSelecaoConfirmada()
                + ", status = " + assinatura.getPedido().getStatus() + ")");

        // =====================================================================
        // GRUPO 5: Definir local da primeira entrega | Telas 11 e 12
        // =====================================================================
        System.out.println("\n>>> [ETAPA 5 — TELAS 11 e 12] Definir local da primeira entrega");
        DadosEndereco dadosEndereco = new DadosEndereco(
                "Rua da Consolação", "930", "Bloco G", "Consolação", "São Paulo", "SP", "01302-907"
        );
        LocalDate previsao = controller.definirLocalEntrega(assinatura, dadosEndereco);
        System.out.println("7. definirLocalEntrega -> Endereço: " + assinatura.getEndereco());
        System.out.println("   Previsão 1ª entrega: " + previsao.format(FMT_DATA)
                + " | Novo status da Assinatura: " + assinatura.getStatus());

        // =====================================================================
        // GRUPO 6: Pagar e concluir contratação | Telas 13 a 15 | FA3
        // =====================================================================
        System.out.println("\n>>> [ETAPA 6 — TELAS 13 a 15] Pagar e concluir contratação (com FA3)");
        System.out.printf("8. obterValorPagamento(assinatura) -> R$ %.2f%n", controller.obterValorPagamento(assinatura));

        // FA3: Tentativa de pagamento recusada pela operadora
        System.out.println("--- Exercitando FA3 (Pagamento Não Autorizado / Recusado pela OperadoraCartao) ---");
        DadosCartao cartaoRecusado = new DadosCartao("4532000000000000", "EDUARDO H S CRUZ", "08/28", "000");
        SituacaoPagamento sitPagamento1 = controller.pagarAssinatura(assinatura, cartaoRecusado);
        System.out.println("   1ª Tentativa (CVV 000 / final 0000) -> Resultado: " + sitPagamento1
                + " | Status Assinatura permanece: " + assinatura.getStatus()
                + " | Tentativas preservadas: " + assinatura.getTentativas().size());

        // Fluxo Principal: Nova tentativa com outro cartão válido -> AUTORIZADO
        System.out.println("--- Nova tentativa com cartão válido (Fluxo Principal após FA3) ---");
        DadosCartao cartaoValido = new DadosCartao("5412751234567890", "EDUARDO H S CRUZ", "10/30", "458");
        SituacaoPagamento sitPagamento2 = controller.pagarAssinatura(assinatura, cartaoValido);
        System.out.println("   2ª Tentativa (Cartão válido) -> Resultado: " + sitPagamento2);

        // Consultar confirmação final
        ConfirmacaoContratacao confirmacao = controller.consultarConfirmacao(assinatura);
        System.out.println("\n======================================================================");
        System.out.println(" [TELA 15] CONFIRMAÇÃO DA CONTRATAÇÃO GERADA COM SUCESSO");
        System.out.println("======================================================================");
        System.out.println(" - Protocolo:              " + confirmacao.protocolo);
        System.out.println(" - Status da Assinatura:   " + assinatura.getStatus());
        System.out.println(" - Status do Pedido:       " + assinatura.getPedido().getStatus());
        System.out.println(" - Plano Contratado:       " + confirmacao.plano.getNome()
                + " (" + confirmacao.plano.getQuantidadeRefeicoes() + " refeições)");
        System.out.println(" - Periodicidade:          " + confirmacao.periodicidade);
        System.out.println(" - Endereço de Entrega:    " + confirmacao.endereco);
        System.out.println(" - Previsão 1ª Entrega:    " + confirmacao.previsaoPrimeiraEntrega.format(FMT_DATA));
        System.out.println(" - Histórico Pagamentos:   " + assinatura.getTentativas().size()
                + " tentativas (1 RECUSADO, 1 AUTORIZADO)");
        System.out.println(" - Itens Confirmados:");
        for (ItemPedido item : confirmacao.itens) {
            System.out.println("     * " + item);
        }
        System.out.println("======================================================================");
    }

    /**
     * Exibe no terminal o conteúdo de todos os arquivos CSV gerados na pasta 'dados/'.
     */
    private static void exibirArquivosCSV() {
        System.out.println("\n======================================================================");
        System.out.println(" ARQUIVOS DE PERSISTÊNCIA CSV GERADOS EM 'dados/'");
        System.out.println("======================================================================");
        String[] arquivos = {
                "dados/assinantes.csv",
                "dados/codigos_confirmacao.csv",
                "dados/planos.csv",
                "dados/preferencias_alimentares.csv",
                "dados/cardapios.csv",
                "dados/enderecos_entrega.csv",
                "dados/assinaturas.csv",
                "dados/pedidos.csv",
                "dados/itens_pedido.csv",
                "dados/entregas.csv",
                "dados/pagamentos.csv"
        };
        for (String caminho : arquivos) {
            File f = new File(caminho);
            if (f.exists()) {
                System.out.println("\n--- Arquivo: " + caminho + " ---");
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
                    String linha;
                    while ((linha = reader.readLine()) != null) {
                        System.out.println("  " + linha);
                    }
                } catch (Exception e) {
                    System.out.println("  Erro ao ler arquivo: " + e.getMessage());
                }
            }
        }
        System.out.println("======================================================================");
    }
}
