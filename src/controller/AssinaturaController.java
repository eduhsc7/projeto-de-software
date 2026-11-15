package controller;

import collection.Assinantes;
import collection.Assinaturas;
import collection.Cardapios;
import collection.EnderecosEntrega;
import collection.Planos;
import collection.PreferenciasAlimentares;
import entity.Assinante;
import entity.Assinatura;
import entity.Cardapio;
import entity.CodigoConfirmacao;
import entity.ConfirmacaoContratacao;
import entity.DadosCartao;
import entity.DadosEndereco;
import entity.EnderecoEntrega;
import entity.ItemCardapio;
import entity.ItemPedido;
import entity.Pagamento;
import entity.Pedido;
import entity.PlanoAssinatura;
import entity.PreferenciaAlimentar;
import entity.SituacaoCodigo;
import entity.SituacaoPagamento;
import service.OperadoraCartao;
import service.ProcessadorProtocolo;
import service.ProcessadorSMS;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controlador de caso de uso responsável por receber as solicitações das interfaces (boundaries)
 * e coordenar o cenário "Assinar Plano de Refeições", delegando regras e criação às coleções
 * e entidades responsáveis.
 * Correspondência UML: «control» AssinaturaController.
 */
public class AssinaturaController {
    private final Assinantes assinantes;
    private final Assinaturas assinaturas;
    private final Planos planos;
    private final PreferenciasAlimentares preferenciasAlimentares;
    private final Cardapios cardapios;
    private final EnderecosEntrega enderecosEntrega;
    private final ProcessadorSMS processadorSMS;
    private final ProcessadorProtocolo processadorProtocolo;
    private final OperadoraCartao operadoraCartao;

    public AssinaturaController() {
        this.assinantes = new Assinantes();
        this.assinaturas = new Assinaturas();
        this.preferenciasAlimentares = new PreferenciasAlimentares();
        this.cardapios = new Cardapios();
        this.planos = new Planos(this.cardapios.getCatalogoOpcoes());
        this.enderecosEntrega = new EnderecosEntrega();
        this.processadorSMS = new ProcessadorSMS();
        this.processadorProtocolo = new ProcessadorProtocolo();
        this.operadoraCartao = new OperadoraCartao();
    }

    /**
     * Passo 1 e 2: Inicia a contratação identificando ou cadastrando o assinante pelo celular
     * e enviando um código de confirmação por SMS.
     * Correspondência no Diagrama de Sequência: mensagens 2 a 33 iniciarContratacao(celular: Texto): Assinante.
     */
    public Assinante iniciarContratacao(String celular) {
        Assinante assinante = assinantes.localizarOuCadastrar(celular);
        CodigoConfirmacao codigo = assinante.emitirCodigo();
        assinantes.guardar(assinante);
        processadorSMS.enviar(celular, "Código de confirmação Minha Refeição: " + codigo.getValor());
        return assinante;
    }

    /**
     * Passo 3 e FA1: Valida o código informado pelo assinante.
     * Correspondência no Diagrama de Sequência: mensagens 35 a 42 confirmarCelular(assinante: Assinante, valorInformado: Texto): SituacaoCodigo.
     */
    public SituacaoCodigo confirmarCelular(Assinante assinante, String valorInformado) {
        SituacaoCodigo situacao = assinante.validarCodigo(valorInformado);
        assinantes.guardar(assinante);
        return situacao;
    }

    /**
     * FA1: Reenvia um novo código de confirmação por SMS (substituindo eventual código PENDENTE anterior).
     * Correspondência no Diagrama de Sequência: mensagens 22 a 34 reenviarCodigo(assinante: Assinante): void.
     */
    public void reenviarCodigo(Assinante assinante) {
        CodigoConfirmacao codigo = assinante.emitirCodigo();
        assinantes.guardar(assinante);
        processadorSMS.enviar(assinante.getCelular(), "Novo código de confirmação Minha Refeição: " + codigo.getValor());
    }

    /**
     * Passo 4: Consulta os planos de assinatura disponíveis.
     * Correspondência no Diagrama de Sequência: mensagens 46 a 49 consultarPlanos(): Lista<PlanoAssinatura>.
     */
    public List<PlanoAssinatura> consultarPlanos() {
        return planos.listar();
    }

    /**
     * Passo 5: Contrata o plano escolhido (exigindo celular validado), criando a Assinatura (EM_PREENCHIMENTO)
     * e seu Pedido inicial (EM_MONTAGEM).
     * Correspondência no Diagrama de Sequência: mensagens 50 a 57 contratarPlano(assinante: Assinante, plano: PlanoAssinatura): Assinatura.
     */
    public Assinatura contratarPlano(Assinante assinante, PlanoAssinatura plano) {
        boolean validado = assinante.celularValidado();
        if (!validado) {
            throw new IllegalStateException("O celular do assinante precisa estar VALIDADO antes de contratar um plano.");
        }
        return assinaturas.criar(assinante, plano);
    }

    /**
     * Passo 6: Consulta as preferências alimentares disponíveis.
     * Correspondência no Diagrama de Sequência: mensagens 63 a 66 consultarPreferencias(): Lista<PreferenciaAlimentar>.
     */
    public List<PreferenciaAlimentar> consultarPreferencias() {
        return preferenciasAlimentares.listar();
    }

    /**
     * Passo 6: Registra as preferências alimentares declaradas pelo assinante.
     * Correspondência no Diagrama de Sequência: mensagens 67 a 69 definirPreferencias(assinante: Assinante, preferencias: Lista<PreferenciaAlimentar>): void.
     */
    public void definirPreferencias(Assinante assinante, List<PreferenciaAlimentar> preferencias) {
        assinante.definirPreferencias(preferencias);
        assinantes.guardar(assinante);
    }

    /**
     * Passo 7: Consulta as ofertas do cardápio vigente elegíveis para o plano e compatíveis com as preferências.
     * Correspondência no Diagrama de Sequência: mensagens 71 a 86 consultarRefeicoes(assinatura: Assinatura): Lista<ItemCardapio>.
     */
    public List<ItemCardapio> consultarRefeicoes(Assinatura assinatura) {
        PlanoAssinatura plano = assinatura.getPlano();
        Assinante assinante = assinatura.getAssinante();
        List<PreferenciaAlimentar> preferencias = assinante.getPreferencias();
        Pedido pedido = assinatura.getPedido();
        Cardapio cardapio = cardapios.buscarVigente(LocalDate.now());
        return cardapio.itensElegiveis(plano, preferencias);
    }

    /**
     * Passo 8 e FA2: Monta ou corrige o pedido inicial validando vigência, permissão do plano,
     * compatibilidade com preferências, quantidades positivas e limite de refeições do plano.
     * Correspondência no Diagrama de Sequência: mensagens 93 a 128 montarPedido(assinatura: Assinatura, selecao: Mapa<ItemCardapio, Inteiro>): Booleano.
     */
    public boolean montarPedido(Assinatura assinatura, Map<ItemCardapio, Integer> selecao) {
        PlanoAssinatura plano = assinatura.getPlano();
        Assinante assinante = assinatura.getAssinante();
        List<PreferenciaAlimentar> preferencias = assinante.getPreferencias();
        Pedido pedido = assinatura.getPedido();
        boolean valido = pedido.definirSelecao(selecao, plano, preferencias, LocalDate.now());
        if (valido) {
            assinaturas.guardar(assinatura);
        }
        return valido;
    }

    /**
     * Passo 9 e FA2: Obtém os itens do pedido inicial para apresentação do resumo ao usuário.
     * Correspondência no Diagrama de Sequência: mensagens 128 a 133 consultarResumoPedido(assinatura: Assinatura): Lista<ItemPedido>.
     */
    public List<ItemPedido> consultarResumoPedido(Assinatura assinatura) {
        Pedido pedido = assinatura.getPedido();
        return pedido.getItens();
    }

    /**
     * Passo 9 e FA2: Confirma o resumo da seleção do pedido inicial (selecaoConfirmada = true).
     * Correspondência no Diagrama de Sequência: mensagens 134 a 139 confirmarPedido(assinatura: Assinatura): Booleano.
     */
    public boolean confirmarPedido(Assinatura assinatura) {
        Pedido pedido = assinatura.getPedido();
        boolean confirmado = pedido.confirmarSelecao();
        if (confirmado) {
            assinaturas.guardar(assinatura);
        }
        return confirmado;
    }

    /**
     * Passo 10: Cadastra o endereço de entrega, define a primeira entrega com previsão e coloca
     * a assinatura em AGUARDANDO_PAGAMENTO.
     * Correspondência no Diagrama de Sequência: mensagens 139 a 152 definirLocalEntrega(assinatura: Assinatura, dados: DadosEndereco): Data.
     */
    public LocalDate definirLocalEntrega(Assinatura assinatura, DadosEndereco dados) {
        EnderecoEntrega endereco = enderecosEntrega.cadastrar(dados);
        LocalDate previsao = assinatura.definirPrimeiraEntrega(endereco);
        assinatura.aguardarPagamento();
        assinaturas.guardar(assinatura);
        return previsao;
    }

    /**
     * Passo 11: Obtém o valor contratado da assinatura para exibição na tela de pagamento.
     * Correspondência no Diagrama de Sequência: mensagens 152 a 155 obterValorPagamento(assinatura: Assinatura): Dinheiro.
     */
    public BigDecimal obterValorPagamento(Assinatura assinatura) {
        return assinatura.getValorContratado();
    }

    /**
     * Passo 11, 12 e FA3: Inicia uma tentativa de pagamento, solicita autorização à OperadoraCartao
     * e, se AUTORIZADO, confirma a autorização, gera o protocolo e ativa a assinatura (aprovando o pedido);
     * se RECUSADO (FA3), registra a recusa preservando a tentativa e mantendo a assinatura em AGUARDANDO_PAGAMENTO.
     * Correspondência no Diagrama de Sequência: mensagens 156 a 183 pagarAssinatura(assinatura: Assinatura, cartao: DadosCartao): SituacaoPagamento.
     */
    public SituacaoPagamento pagarAssinatura(Assinatura assinatura, DadosCartao cartao) {
        Pagamento pagamento = assinatura.iniciarPagamento(operadoraCartao);
        BigDecimal valorContratado = assinatura.getValorContratado();
        SituacaoPagamento resultado = operadoraCartao.autorizar(valorContratado, cartao);
        if (resultado == SituacaoPagamento.AUTORIZADO) {
            pagamento.confirmarAutorizacao();
            String protocolo = processadorProtocolo.gerar();
            assinatura.ativar(protocolo);
            assinaturas.guardar(assinatura);
            return SituacaoPagamento.AUTORIZADO;
        } else {
            pagamento.registrarRecusa();
            assinaturas.guardar(assinatura);
            return SituacaoPagamento.RECUSADO;
        }
    }

    /**
     * Passo 12: Obtém os dados consolidados da confirmação da contratação após a ativação.
     * Correspondência no Diagrama de Sequência: mensagens 174 a 177 consultarConfirmacao(assinatura: Assinatura): ConfirmacaoContratacao.
     */
    public ConfirmacaoContratacao consultarConfirmacao(Assinatura assinatura) {
        return assinatura.obterConfirmacao();
    }

    public Assinantes getAssinantes() {
        return assinantes;
    }

    public Assinaturas getAssinaturas() {
        return assinaturas;
    }

    public Cardapios getCardapios() {
        return cardapios;
    }
}
