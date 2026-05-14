package boundary;

import entity.Plano;
import java.util.List;
import java.util.Scanner;

public class TelaSelecaoPlano {
    private final Scanner scanner = new Scanner(System.in);

    public Plano selecionarPlano(List<Plano> planos) {
        System.out.println("\nSeleção de plano:");
        for (Plano plano : planos) {
            System.out.println(plano.getId() + " - " + plano.getNome() + " | R$ " + plano.getValorMensal());
        }

        while (true) {
            System.out.print("Selecione o plano pelo ID: ");
            int id = Integer.parseInt(scanner.nextLine());
            for (Plano plano : planos) {
                if (plano.getId() == id) {
                    return plano;
                }
            }
            System.out.println("Plano inválido. Tente novamente.");
        }
    }
}
