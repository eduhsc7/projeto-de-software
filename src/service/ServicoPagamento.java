package service;

import entity.DadosPagamento;

public class ServicoPagamento {
    public boolean cobrar(DadosPagamento dadosPagamento) {
        // Regra simples para simular aprovação:
        // pagamento aprovado quando o cartão tiver pelo menos 8 dígitos e o CVV tiver 3 dígitos.
        String numeroCartao = dadosPagamento.getNumeroCartao().replace(" ", "");
        String cvv = dadosPagamento.getCvv();

        return numeroCartao.length() >= 8 && cvv.length() == 3;
    }
}
