package controller;

import entity.*;
import repository.AssinaturaRepository;
import service.*;

import java.util.List;

public class AssinaturaController {
    private ServicoSMS servicoSMS;
    private ServicoPlanoAssinatura servicoPlanoAssinatura;
    private ServicoCatalogoProdutos servicoCatalogoProdutos;
    private CestaSemanal cestaSemanal;
    private ServicoPagamento servicoPagamento;
    private GeradorProtocolo geradorProtocolo;
    private AssinaturaRepository assinaturaRepository;

    private Plano planoSelecionado;
    private Endereco enderecoEntrega;

    public AssinaturaController() {
        this.servicoSMS = new ServicoSMS();
        this.servicoPlanoAssinatura = new ServicoPlanoAssinatura();
        this.servicoCatalogoProdutos = new ServicoCatalogoProdutos();
        this.cestaSemanal = new CestaSemanal();
        this.servicoPagamento = new ServicoPagamento();
        this.geradorProtocolo = new GeradorProtocolo();
        this.assinaturaRepository = new AssinaturaRepository();
    }

    public void solicitarSMS(String numeroCelular) {
        servicoSMS.enviarCodigo(numeroCelular);
    }

    public boolean validarCodigo(String codigo, String numeroCelular) {
        return servicoSMS.verificarCodigo(codigo, numeroCelular);
    }

    public List<Plano> listarPlanos() {
        return servicoPlanoAssinatura.obterPlanos();
    }

    public void selecionarPlano(int planoId) {
        for (Plano plano : listarPlanos()) {
            if (plano.getId() == planoId) {
                this.planoSelecionado = plano;
                return;
            }
        }
    }

    public List<Produto> listarProdutos() {
        return servicoCatalogoProdutos.obterProdutos();
    }

    public void adicionarCesta(List<Produto> produtos) {
        cestaSemanal.adicionar(produtos);
    }

    public void salvarEndereco(Endereco endereco) {
        this.enderecoEntrega = endereco;
    }

    public boolean processarPagamento(DadosPagamento dadosPagamento) {
        return servicoPagamento.cobrar(dadosPagamento);
    }

    public Assinatura confirmarAssinatura(String numeroCelular, Plano plano, List<Produto> produtos, Endereco endereco, DadosPagamento dadosPagamento) {
        Assinante assinante = new Assinante(numeroCelular);
        String assinaturaId = String.valueOf(System.currentTimeMillis());
        String protocolo = geradorProtocolo.gerarProtocolo(assinaturaId);

        Assinatura assinatura = new Assinatura(
                protocolo,
                assinante,
                plano,
                produtos,
                endereco,
                "APROVADO"
        );

        assinaturaRepository.salvar(assinatura);
        return assinatura;
    }

    public Plano getPlanoSelecionado() {
        return planoSelecionado;
    }

    public Endereco getEnderecoEntrega() {
        return enderecoEntrega;
    }
}
