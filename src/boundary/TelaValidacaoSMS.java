package boundary;

import java.util.Scanner;

public class TelaValidacaoSMS {
    private final Scanner scanner = new Scanner(System.in);

    public String solicitarCodigoSMS() {
        System.out.println("SMS enviado com código.");
        System.out.print("Insira o código SMS: ");
        return scanner.nextLine();
    }

    public void exibirCodigoInvalido() {
        System.out.println("Código inválido. Assinatura não realizada.");
    }
}
