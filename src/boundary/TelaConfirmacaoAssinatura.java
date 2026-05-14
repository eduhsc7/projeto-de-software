package boundary;

import entity.Assinatura;

public class TelaConfirmacaoAssinatura {
    public void exibirConfirmacao(Assinatura assinatura) {
        System.out.println("\n===========================================");
        System.out.println(" ASSINATURA CONFIRMADA");
        System.out.println("===========================================");
        System.out.println("Protocolo: " + assinatura.getProtocolo());
        System.out.println("Celular: " + assinatura.getAssinante().getNumeroCelular());
        System.out.println("Plano: " + assinatura.getPlano().getNome());
        System.out.println("Produtos: " + assinatura.getProdutosComoTexto());
        System.out.println("Endereço: " + assinatura.getEndereco());
        System.out.println("Status pagamento: " + assinatura.getStatusPagamento());
    }
}
