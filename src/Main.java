import boundary.*;
import controller.AssinaturaController;
import entity.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        AssinaturaController controller = new AssinaturaController();

        TelaIdentificacaoAssinante telaIdentificacao = new TelaIdentificacaoAssinante();
        TelaValidacaoSMS telaValidacaoSMS = new TelaValidacaoSMS();
        TelaSelecaoPlano telaSelecaoPlano = new TelaSelecaoPlano();
        TelaSelecaoProdutos telaSelecaoProdutos = new TelaSelecaoProdutos();
        TelaEnderecoEntrega telaEnderecoEntrega = new TelaEnderecoEntrega();
        TelaPagamento telaPagamento = new TelaPagamento();
        TelaConfirmacaoAssinatura telaConfirmacao = new TelaConfirmacaoAssinatura();

        System.out.println("===========================================");
        System.out.println(" SISTEMA DE ASSINATURA SEMANAL");
        System.out.println("===========================================");

        String numeroCelular = telaIdentificacao.solicitarNumeroCelular();
        controller.solicitarSMS(numeroCelular);

        String codigo = telaValidacaoSMS.solicitarCodigoSMS();
        boolean codigoValido = controller.validarCodigo(codigo, numeroCelular);

        if (!codigoValido) {
            telaValidacaoSMS.exibirCodigoInvalido();
            return;
        }

        List<Plano> planos = controller.listarPlanos();
        Plano planoSelecionado = telaSelecaoPlano.selecionarPlano(planos);
        controller.selecionarPlano(planoSelecionado.getId());

        List<Produto> produtosDisponiveis = controller.listarProdutos();
        List<Produto> produtosSelecionados = telaSelecaoProdutos.selecionarProdutos(produtosDisponiveis);
        controller.adicionarCesta(produtosSelecionados);

        Endereco endereco = telaEnderecoEntrega.solicitarEndereco();
        controller.salvarEndereco(endereco);

        DadosPagamento dadosPagamento = telaPagamento.solicitarDadosPagamento();
        boolean pagamentoAprovado = controller.processarPagamento(dadosPagamento);

        if (pagamentoAprovado) {
            Assinatura assinatura = controller.confirmarAssinatura(numeroCelular, planoSelecionado, produtosSelecionados, endereco, dadosPagamento);
            telaConfirmacao.exibirConfirmacao(assinatura);
        } else {
            telaPagamento.exibirErroPagamento();
        }
    }
}
