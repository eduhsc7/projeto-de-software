package boundary;

import java.util.Scanner;

public class TelaIdentificacaoAssinante {
    private final Scanner scanner = new Scanner(System.in);

    public String solicitarNumeroCelular() {
        System.out.print("Insira o número celular: ");
        return scanner.nextLine();
    }
}
