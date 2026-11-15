package boundary;

import controller.AssinaturaController;
import entity.Assinatura;
import entity.ConfirmacaoContratacao;
import entity.DadosCartao;
import entity.ItemPedido;
import entity.SituacaoPagamento;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Objeto de interface (fronteira) responsável pelas Telas 13 a 15 do protótipo:
 * Pagamento, Autorização com a OperadoraCartao e Confirmação da Contratação, com FA3 integrado.
 * Correspondência UML: «boundary» TelaPagamento.
 */
public class TelaPagamento {
    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final AssinaturaController controller;
    private final Scanner scanner;

    public TelaPagamento(AssinaturaController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    /**
     * Conduz as Telas 13 a 15 (Pagamento, Autorização e Confirmação, com FA3 integrado).
     */
    public void realizarPagamento(Assinatura assinatura) {
        // Mensagem 152: TP -> C : obterValorPagamento(assinatura: Assinatura): Dinheiro
        BigDecimal valorContratado = controller.obterValorPagamento(assinatura);

        boolean concluido = false;
        while (!concluido) {
            System.out.println("\n======================================================================");
            System.out.println(" [TELA 13] PAGAMENTO DA ASSINATURA");
            System.out.println("======================================================================");
            System.out.printf(" - Valor total a pagar: R$ %.2f%n", valorContratado);
            System.out.println(" - Status atual da assinatura: " + assinatura.getStatus());
            System.out.println("----------------------------------------------------------------------");
            System.out.println(" Dica para simulação da OperadoraCartao:");
            System.out.println(" - Para APROVAR (Fluxo Principal): cartão >= 8 dígitos (ex: 4532111122223333) e CVV de 3 dígitos != 000 (ex: 123).");
            System.out.println(" - Para RECUSAR (FA3): informe CVV '000' ou cartão terminado em '0000'.");
            System.out.println("----------------------------------------------------------------------");

            System.out.print("Número do cartão (padrão 4532111122223333): ");
            String numero = scanner.nextLine().trim();
            if (numero.isEmpty()) numero = "4532111122223333";

            System.out.print("Nome do titular (padrão VINICIUS R OLIVEIRA): ");
            String titular = scanner.nextLine().trim();
            if (titular.isEmpty()) titular = "VINICIUS R OLIVEIRA";

            System.out.print("Validade MM/AA (padrão 12/29): ");
            String validade = scanner.nextLine().trim();
            if (validade.isEmpty()) validade = "12/29";

            System.out.print("CVV de 3 dígitos (digite 123 para aprovar ou 000 para testar FA3): ");
            String cvv = scanner.nextLine().trim();
            if (cvv.isEmpty()) cvv = "123";

            DadosCartao cartao = new DadosCartao(numero, titular, validade, cvv);

            System.out.println("\n----------------------------------------------------------------------");
            System.out.println(" [TELA 14] SOLICITANDO AUTORIZAÇÃO À OPERADORA DO CARTÃO...");
            System.out.println("----------------------------------------------------------------------");

            // Mensagem 156: TP -> C : pagarAssinatura(assinatura: Assinatura, cartao: DadosCartao): SituacaoPagamento
            SituacaoPagamento resultado = controller.pagarAssinatura(assinatura, cartao);

            if (resultado == SituacaoPagamento.AUTORIZADO) {
                System.out.println(" -> Pagamento AUTORIZADO pela operadora!");
                // Mensagem 174: TP -> C : consultarConfirmacao(assinatura: Assinatura): ConfirmacaoContratacao
                ConfirmacaoContratacao confirmacao = controller.consultarConfirmacao(assinatura);
                exibirConfirmacao(assinatura, confirmacao);
                concluido = true;
            } else {
                System.out.println("\n [FA3 — PAGAMENTO NÃO AUTORIZADO]");
                System.out.println(" -> Resultado da operadora: " + resultado);
                System.out.println(" -> A tentativa recusada foi preservada no histórico ("
                        + assinatura.getTentativas().size() + " tentativa(s) registrada(s)).");
                System.out.println(" -> A assinatura permanece com status: " + assinatura.getStatus());
                System.out.println("----------------------------------------------------------------------");
                System.out.println(" [1] Tentar novamente / Usar outro cartão de crédito");
                System.out.println(" [2] Encerrar contratação sem pagamento autorizado");
                System.out.print("Escolha uma opção (1 ou 2): ");
                String op = scanner.nextLine().trim();
                if (op.equals("2")) {
                    System.out.println("\n -> Sessão encerrada pelo usuário. A assinatura continua em "
                            + assinatura.getStatus() + " e o registro foi salvo em dados/assinaturas.csv.");
                    concluido = true;
                }
            }
        }
    }

    private void exibirConfirmacao(Assinatura assinatura, ConfirmacaoContratacao confirmacao) {
        System.out.println("\n======================================================================");
        System.out.println(" [TELA 15] CONFIRMAÇÃO DA CONTRATAÇÃO — MINHA REFEIÇÃO");
        System.out.println("======================================================================");
        System.out.println(" Protocolo da Assinatura:      " + confirmacao.protocolo);
        System.out.println(" Status da Assinatura:         " + assinatura.getStatus());
        System.out.println(" Status do Pedido Inicial:     " + assinatura.getPedido().getStatus());
        System.out.println(" Assinante (Celular):          " + assinatura.getAssinante().getCelular());
        System.out.println(" Plano Contratado:             " + confirmacao.plano.getNome()
                + " (" + confirmacao.plano.getQuantidadeRefeicoes() + " refeições)");
        System.out.printf(" Valor Contratado:             R$ %.2f%n", assinatura.getValorContratado());
        System.out.println(" Periodicidade de Entrega:     " + confirmacao.periodicidade);
        System.out.println(" Endereço de Entrega:          " + confirmacao.endereco);
        System.out.println(" Previsão 1ª Entrega:          " + confirmacao.previsaoPrimeiraEntrega.format(FMT_DATA));
        System.out.println(" Tentativas de Pagamento:      " + assinatura.getTentativas().size() + " (Última: AUTORIZADO)");
        System.out.println(" Refeições Selecionadas:");
        for (ItemPedido item : confirmacao.itens) {
            System.out.println("   * " + item);
        }
        System.out.println("======================================================================");
        System.out.println(" [PERSISTÊNCIA CSV] Todos os registros foram salvos na pasta 'dados/'.");
        System.out.println("======================================================================");
    }
}
