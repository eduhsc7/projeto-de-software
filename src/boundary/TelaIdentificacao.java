package boundary;

import controller.AssinaturaController;
import entity.Assinante;
import entity.CodigoConfirmacao;
import entity.SituacaoCodigo;

import java.util.Scanner;

/**
 * Objeto de interface (fronteira) responsável pelas Telas 1 a 3 do protótipo:
 * Identificação pelo celular e validação do código SMS, com o Fluxo Alternativo FA1 integrado.
 * Correspondência UML: «boundary» TelaIdentificacao.
 */
public class TelaIdentificacao {
    private final AssinaturaController controller;
    private final Scanner scanner;

    public TelaIdentificacao(AssinaturaController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    /**
     * Conduz a interação das Telas 1 a 3 (Identificação e Validação SMS, com FA1).
     * Correspondência no Diagrama de Sequência: grupo "Iniciar contratação e validar celular | telas 1 a 3 | FA1 integrado".
     */
    public Assinante realizarIdentificacao() {
        System.out.println("\n======================================================================");
        System.out.println(" [TELA 01] IDENTIFICAÇÃO DO ASSINANTE — INFORMAR CELULAR");
        System.out.println("======================================================================");
        String celular = "";
        while (celular.isBlank()) {
            System.out.print("Informe seu número de celular (ex: 11987654321): ");
            celular = scanner.nextLine().trim();
        }

        // Mensagem 2: TI -> C : iniciarContratacao(celular: Texto): Assinante
        Assinante assinante = controller.iniciarContratacao(celular);

        // Loop por emissão i, enquanto o celular não estiver VALIDADO
        while (!assinante.celularValidado()) {
            System.out.println("\n----------------------------------------------------------------------");
            System.out.println(" [TELA 02] VALIDAR CÓDIGO SMS");
            System.out.println(" (Dica para teste de FA1: digite um código errado até 3x para ver o");
            System.out.println("  bloqueio, ou digite 'R' para solicitar reenvio e substituir o pendente)");
            System.out.println("----------------------------------------------------------------------");
            System.out.print("Digite o código de 4 dígitos recebido por SMS (ou 'R' para reenviar): ");
            String entrada = scanner.nextLine().trim();

            if (entrada.equalsIgnoreCase("R")) {
                // FA1: Reenvio antes do bloqueio substitui o código ainda PENDENTE por SUBSTITUIDO
                System.out.println(" -> [FA1] Solicitando reenvio de novo código SMS...");
                controller.reenviarCodigo(assinante);
                continue;
            }

            // Mensagem 35: TI -> C : confirmarCelular(assinante: Assinante, valorInformado: Texto): SituacaoCodigo
            SituacaoCodigo situacao = controller.confirmarCelular(assinante, entrada);

            if (situacao == SituacaoCodigo.VALIDADO) {
                System.out.println("\n======================================================================");
                System.out.println(" [TELA 03] CÓDIGO VALIDADO COM SUCESSO!");
                System.out.println(" -> Situação do código: " + situacao);
                System.out.println(" -> Celular confirmado (" + assinante.getCelular() + "). Contratação liberada!");
                System.out.println("======================================================================");
            } else if (situacao == SituacaoCodigo.PENDENTE) {
                CodigoConfirmacao ultimo = assinante.getUltimoCodigo();
                int restantes = ultimo != null ? ultimo.getTentativasRestantes() : 0;
                System.out.println("\n [FA1 — CÓDIGO INVÁLIDO] O código informado está incorreto!");
                System.out.println(" -> Situação do código: " + situacao
                        + " | Tentativas inválidas: " + (ultimo != null ? ultimo.getTentativasInvalidas() : 0) + "/3"
                        + " | Tentativas restantes: " + restantes);
                System.out.println(" -> Você pode tentar novamente ou digitar 'R' para reenviar um novo código.");
            } else if (situacao == SituacaoCodigo.BLOQUEADO) {
                System.out.println("\n [FA1 — CÓDIGO BLOQUEADO] Limite de 3 tentativas inválidas atingido!");
                System.out.println(" -> Situação do código: " + situacao);
                System.out.print(" -> Pressione ENTER para solicitar um novo código SMS (contador zerado)... ");
                scanner.nextLine();
                controller.reenviarCodigo(assinante);
            }
        }

        return assinante;
    }
}
