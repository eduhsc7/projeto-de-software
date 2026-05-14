package boundary;

import entity.Endereco;
import java.util.Scanner;

public class TelaEnderecoEntrega {
    private final Scanner scanner = new Scanner(System.in);

    public Endereco solicitarEndereco() {
        System.out.println("\nEndereço de entrega:");
        System.out.print("Rua: ");
        String rua = scanner.nextLine();
        System.out.print("Número: ");
        String numero = scanner.nextLine();
        System.out.print("Bairro: ");
        String bairro = scanner.nextLine();
        System.out.print("Cidade: ");
        String cidade = scanner.nextLine();
        System.out.print("Estado: ");
        String estado = scanner.nextLine();
        System.out.print("CEP: ");
        String cep = scanner.nextLine();

        return new Endereco(rua, numero, bairro, cidade, estado, cep);
    }
}
