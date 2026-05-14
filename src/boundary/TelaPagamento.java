package boundary;

import entity.DadosPagamento;
import java.util.Scanner;

public class TelaPagamento {
    private final Scanner scanner = new Scanner(System.in);

    public DadosPagamento solicitarDadosPagamento() {
        System.out.println("\nPagamento:");
        System.out.print("Nome do titular: ");
        String titular = scanner.nextLine();
        System.out.print("Número do cartão: ");
        String numeroCartao = scanner.nextLine();
        System.out.print("Validade MM/AA: ");
        String validade = scanner.nextLine();
        System.out.print("CVV: ");
        String cvv = scanner.nextLine();

        return new DadosPagamento(titular, numeroCartao, validade, cvv);
    }

    public void exibirErroPagamento() {
        System.out.println("Pagamento reprovado. Assinatura não confirmada.");
    }
}
