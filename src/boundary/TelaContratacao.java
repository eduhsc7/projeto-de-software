package boundary;

import controller.AssinaturaController;
import entity.Assinante;
import entity.Assinatura;
import entity.Cardapio;
import entity.CategoriaAlimento;
import entity.DadosEndereco;
import entity.ItemCardapio;
import entity.ItemPedido;
import entity.OpcaoCardapio;
import entity.PlanoAssinatura;
import entity.PreferenciaAlimentar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Objeto de interface (fronteira) responsável pelas Telas 4 a 12 do protótipo:
 * Seleção e confirmação do plano, preferências alimentares, seleção de pratos, acompanhamentos
 * e sobremesas, resumo do pedido (com FA2 integrado), endereço de entrega e aguardando pagamento.
 * Correspondência UML: «boundary» TelaContratacao.
 */
public class TelaContratacao {
    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final AssinaturaController controller;
    private final Scanner scanner;

    public TelaContratacao(AssinaturaController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    /**
     * Conduz as Telas 4 a 12 da jornada de contratação.
     */
    public Assinatura realizarContratacao(Assinante assinante) {
        // 1. Telas 4 e 5: Consultar e contratar plano
        Assinatura assinatura = selecionarEConfirmarPlano(assinante);

        // 2. Tela 6: Definir preferências alimentares
        selecionarPreferencias(assinante);

        // 3. Telas 7 a 10: Consultar refeições, montar pedido inicial e confirmar resumo (com FA2)
        montarEConfirmarPedido(assinatura);

        // 4. Telas 11 e 12: Informar endereço de entrega, registrar primeira entrega e aguardar pagamento
        informarEnderecoEntrega(assinatura);

        return assinatura;
    }

    private Assinatura selecionarEConfirmarPlano(Assinante assinante) {
        while (true) {
            System.out.println("\n======================================================================");
            System.out.println(" [TELA 04] PLANOS DE ASSINATURA DISPONÍVEIS");
            System.out.println("======================================================================");
            // Mensagem 46: TC -> C : consultarPlanos(): Lista<PlanoAssinatura>
            List<PlanoAssinatura> planos = controller.consultarPlanos();
            for (int i = 0; i < planos.size(); i++) {
                PlanoAssinatura p = planos.get(i);
                System.out.printf(" [%d] %-20s | %2d refeições | Entrega %-10s | R$ %6.2f%n",
                        i + 1, p.getNome(), p.getQuantidadeRefeicoes(), p.getPeriodicidadeEntrega(), p.getValor());
            }

            int escolha = lerInteiro("Escolha o número do plano desejado (1 a " + planos.size() + "): ", 1, planos.size());
            PlanoAssinatura planoEscolhido = planos.get(escolha - 1);

            System.out.println("\n----------------------------------------------------------------------");
            System.out.println(" [TELA 05] CONFIRMAR PLANO ESCOLHIDO");
            System.out.println("----------------------------------------------------------------------");
            System.out.println(" - Plano:                " + planoEscolhido.getNome());
            System.out.println(" - Quantidade refeições: " + planoEscolhido.getQuantidadeRefeicoes() + " pratos principais");
            System.out.println(" - Periodicidade:        " + planoEscolhido.getPeriodicidadeEntrega());
            System.out.printf(" - Valor da assinatura:  R$ %.2f%n", planoEscolhido.getValor());
            System.out.print("Confirmar contratação deste plano? [S/N] (padrão S): ");
            String conf = scanner.nextLine().trim();
            if (conf.isEmpty() || conf.equalsIgnoreCase("S")) {
                // Mensagem 50: TC -> C : contratarPlano(assinante: Assinante, plano: PlanoAssinatura): Assinatura
                Assinatura assinatura = controller.contratarPlano(assinante, planoEscolhido);
                System.out.println(" -> Assinatura criada com status: " + assinatura.getStatus()
                        + " | Pedido inicial criado com status: " + assinatura.getPedido().getStatus());
                return assinatura;
            }
        }
    }

    private void selecionarPreferencias(Assinante assinante) {
        System.out.println("\n======================================================================");
        System.out.println(" [TELA 06] PREFERÊNCIAS ALIMENTARES");
        System.out.println("======================================================================");
        // Mensagem 63: TC -> C : consultarPreferencias(): Lista<PreferenciaAlimentar>
        List<PreferenciaAlimentar> disponiveis = controller.consultarPreferencias();
        for (int i = 0; i < disponiveis.size(); i++) {
            System.out.printf(" [%d] %s%n", i + 1, disponiveis.get(i).getDescricao());
        }
        System.out.println("Informe os números das suas preferências separados por vírgula (ex: 1 ou 2,3): ");
        System.out.print("Sua escolha (padrão 1 - Tradicional): ");
        String linha = scanner.nextLine().trim();

        List<PreferenciaAlimentar> escolhidas = new ArrayList<>();
        if (!linha.isEmpty()) {
            for (String parte : linha.split(",")) {
                try {
                    int idx = Integer.parseInt(parte.trim());
                    if (idx >= 1 && idx <= disponiveis.size()) {
                        PreferenciaAlimentar pref = disponiveis.get(idx - 1);
                        if (!escolhidas.contains(pref)) {
                            escolhidas.add(pref);
                        }
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        if (escolhidas.isEmpty() && !disponiveis.isEmpty()) {
            escolhidas.add(disponiveis.get(0));
        }

        // Mensagem 67: TC -> C : definirPreferencias(assinante: Assinante, preferencias: Lista<PreferenciaAlimentar>): void
        controller.definirPreferencias(assinante, escolhidas);
        System.out.println(" -> Preferências salvas: " + escolhidas);
    }

    private void montarEConfirmarPedido(Assinatura assinatura) {
        // Mensagem 71: TC -> C : consultarRefeicoes(assinatura: Assinatura): Lista<ItemCardapio>
        List<ItemCardapio> elegiveis = controller.consultarRefeicoes(assinatura);
        PlanoAssinatura plano = assinatura.getPlano();

        boolean resumoConfirmado = false;
        while (!resumoConfirmado) {
            System.out.println("\n======================================================================");
            System.out.println(" [TELAS 07, 08 e 09] CARDÁPIO VIGENTE — MONTAGEM DO PEDIDO INICIAL");
            System.out.println(" Plano: " + plano.getNome() + " | Limite: de 1 até "
                    + plano.getQuantidadeRefeicoes() + " unidades de PRATO PRINCIPAL");
            System.out.println(" Preferências ativas: " + assinatura.getAssinante().getPreferencias());
            System.out.println("======================================================================");

            exibirItensPorCategoria(" [TELA 07] PRATOS PRINCIPAIS (1 unidade = 1 refeição do plano)",
                    elegiveis, CategoriaAlimento.PRATO_PRINCIPAL);
            exibirItensPorCategoria(" [TELA 08] ACOMPANHAMENTOS (complementam as refeições)",
                    elegiveis, CategoriaAlimento.ACOMPANHAMENTO);
            exibirItensPorCategoria(" [TELA 09] SOBREMESAS (complementam as refeições)",
                    elegiveis, CategoriaAlimento.SOBREMESA);

            System.out.println("\n----------------------------------------------------------------------");
            System.out.println(" Dica para exercitar FA2 (Seleção Incompatível):");
            System.out.println(" - Pressione ENTER sem itens para testar seleção vazia (FA2);");
            System.out.println(" - Digite quantidade <= 0 ou total de Pratos Principais > " + plano.getQuantidadeRefeicoes() + " para testar limite excedido (FA2);");
            System.out.println(" - Digite 'EXP' para tentar incluir um item de cardápio fora de vigência (FA2).");
            System.out.println("----------------------------------------------------------------------");
            System.out.println("Informe os itens e quantidades no formato ID:QTD separados por vírgula");
            System.out.print("(ex: 1:2, 2:3, 4:2, 7:1) ou 'EXP' para testar item expirado: ");
            String entrada = scanner.nextLine().trim();

            Map<ItemCardapio, Integer> selecao = new LinkedHashMap<>();
            if (entrada.equalsIgnoreCase("EXP")) {
                // Busca o cardápio encerrado apenas para demonstrar FA2 (oferta fora de vigência)
                List<Cardapio> todosCardapios = controller.getCardapios().getCardapios();
                if (todosCardapios.size() > 1 && !todosCardapios.get(1).getOfertas().isEmpty()) {
                    selecao.put(todosCardapios.get(1).getOfertas().get(0), 2);
                }
            } else if (!entrada.isEmpty()) {
                for (String par : entrada.split(",")) {
                    String[] kv = par.trim().split(":");
                    try {
                        int idx = Integer.parseInt(kv[0].trim());
                        int qtd = kv.length > 1 ? Integer.parseInt(kv[1].trim()) : 1;
                        if (idx >= 1 && idx <= elegiveis.size()) {
                            ItemCardapio itemEscolhido = elegiveis.get(idx - 1);
                            selecao.merge(itemEscolhido, qtd, Integer::sum);
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            // Mensagem 93: TC -> C : montarPedido(assinatura: Assinatura, selecao: Mapa<ItemCardapio, Inteiro>): Booleano
            boolean selecaoValida = controller.montarPedido(assinatura, selecao);

            if (!selecaoValida) {
                System.out.println("\n [FA2 — SELEÇÃO INCOMPATÍVEL] O pedido informado não atende às regras!");
                System.out.println(" -> Regras verificadas por Pedido.validarSelecao:");
                System.out.println("    1. A seleção não pode ser vazia e todas as quantidades devem ser > 0;");
                System.out.println("    2. Todas as ofertas devem pertencer a um Cardápio vigente, ser permitidas pelo plano e compatíveis com suas preferências;");
                System.out.println("    3. A soma das quantidades de PRATO_PRINCIPAL deve estar entre 1 e "
                        + plano.getQuantidadeRefeicoes() + " refeições.");
                if (!assinatura.getPedido().getItens().isEmpty()) {
                    System.out.println(" -> Os itens válidos da seleção anterior foram preservados: " + assinatura.getPedido().getItens());
                }
                System.out.println(" -> Por favor, ajuste sua seleção.");
                continue;
            }

            // Mensagem 128: TC -> C : consultarResumoPedido(assinatura: Assinatura): Lista<ItemPedido>
            List<ItemPedido> itensResumo = controller.consultarResumoPedido(assinatura);
            System.out.println("\n======================================================================");
            System.out.println(" [TELA 10] RESUMO DO PEDIDO PARA CONFERÊNCIA");
            System.out.println("======================================================================");
            System.out.println(" - Plano contratado:        " + plano.getNome());
            System.out.println(" - Limite do plano:         " + plano.getQuantidadeRefeicoes() + " refeições (Pratos Principais)");
            System.out.println(" - Total Pratos Principais: " + assinatura.getPedido().getTotalRefeicoesAtual() + " refeições");
            System.out.println(" - Periodicidade:           " + plano.getPeriodicidadeEntrega());
            System.out.printf(" - Valor da assinatura:     R$ %.2f%n", plano.getValor());
            System.out.println(" - Itens selecionados:");
            for (ItemPedido ip : itensResumo) {
                System.out.println("    * " + ip);
            }
            System.out.println("----------------------------------------------------------------------");
            System.out.println(" [1] Confirmar pedido inicial");
            System.out.println(" [2] Ajustar composição do pedido inicial (FA2 — voltar para seleção)");
            int opcaoResumo = lerInteiro("Escolha uma opção (1 ou 2): ", 1, 2);

            if (opcaoResumo == 1) {
                // Mensagem 134: TC -> C : confirmarPedido(assinatura: Assinatura): Booleano
                boolean confirmado = controller.confirmarPedido(assinatura);
                if (confirmado) {
                    System.out.println(" -> Resumo do pedido confirmado! (selecaoConfirmada = true; status permanece "
                            + assinatura.getPedido().getStatus() + " até autorização do pagamento).");
                    resumoConfirmado = true;
                }
            } else {
                System.out.println(" -> [FA2] Retornando à seleção para ajustar itens/quantidades do pedido...");
            }
        }
    }

    private void informarEnderecoEntrega(Assinatura assinatura) {
        System.out.println("\n======================================================================");
        System.out.println(" [TELA 11] ENDEREÇO DE ENTREGA");
        System.out.println("======================================================================");
        String logradouro = lerTextoObrigatorio("Rua ou Avenida (ex: Av. Paulista): ", "Av. Paulista");
        String numero = lerTextoObrigatorio("Número (ex: 1000): ", "1000");
        System.out.print("Complemento (opcional, ex: Apto 42): ");
        String complemento = scanner.nextLine().trim();
        String bairro = lerTextoObrigatorio("Bairro (ex: Bela Vista): ", "Bela Vista");
        String cidade = lerTextoObrigatorio("Cidade (ex: São Paulo): ", "São Paulo");
        String uf = lerTextoObrigatorio("UF (ex: SP): ", "SP");
        String cep = lerTextoObrigatorio("CEP (ex: 01310-100): ", "01310-100");

        DadosEndereco dados = new DadosEndereco(logradouro, numero, complemento, bairro, cidade, uf, cep);

        // Mensagem 139: TC -> C : definirLocalEntrega(assinatura: Assinatura, dados: DadosEndereco): Data
        LocalDate previsao = controller.definirLocalEntrega(assinatura, dados);

        System.out.println("\n======================================================================");
        System.out.println(" [TELA 12] AGUARDANDO PAGAMENTO");
        System.out.println("======================================================================");
        System.out.println(" - Endereço registrado:            " + assinatura.getEndereco());
        System.out.println(" - Previsão da primeira entrega:   " + previsao.format(FMT_DATA));
        System.out.println(" - Novo status da assinatura:      " + assinatura.getStatus());
        System.out.println(" - Próxima etapa: preenchimento dos dados do cartão de crédito.");
        System.out.print("Pressione ENTER para ir para o pagamento... ");
        scanner.nextLine();
    }

    private void exibirItensPorCategoria(String titulo, List<ItemCardapio> elegiveis, CategoriaAlimento categoria) {
        System.out.println("\n" + titulo);
        for (int i = 0; i < elegiveis.size(); i++) {
            OpcaoCardapio op = elegiveis.get(i).getOpcao();
            if (op.getCategoria() == categoria) {
                String prefs = op.getPreferenciasAtendidas().stream()
                        .map(PreferenciaAlimentar::getDescricao)
                        .collect(Collectors.joining(", "));
                System.out.printf("   [%d] %-45s (Atende: %s)%n", i + 1, op.getNome(), prefs);
            }
        }
    }

    private int lerInteiro(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String linha = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(linha);
                if (val >= min && val <= max) {
                    return val;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println(" -> Opção inválida. Informe um número entre " + min + " e " + max + ".");
        }
    }

    private String lerTextoObrigatorio(String prompt, String valorPadrao) {
        System.out.print(prompt);
        String linha = scanner.nextLine().trim();
        return linha.isEmpty() ? valorPadrao : linha;
    }
}
