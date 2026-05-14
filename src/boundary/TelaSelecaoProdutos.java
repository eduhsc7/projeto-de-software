package boundary;

import entity.Produto;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TelaSelecaoProdutos {
    private final Scanner scanner = new Scanner(System.in);

    public List<Produto> selecionarProdutos(List<Produto> produtos) {
        System.out.println("\nSeleção de produtos:");
        for (Produto produto : produtos) {
            System.out.println(produto.getId() + " - " + produto.getNome());
        }

        System.out.print("Digite os IDs dos produtos separados por vírgula. Exemplo: 1,3,5: ");
        String entrada = scanner.nextLine();
        String[] ids = entrada.split(",");

        List<Produto> selecionados = new ArrayList<>();
        for (String idTexto : ids) {
            int id = Integer.parseInt(idTexto.trim());
            for (Produto produto : produtos) {
                if (produto.getId() == id) {
                    selecionados.add(produto);
                }
            }
        }

        return selecionados;
    }
}
