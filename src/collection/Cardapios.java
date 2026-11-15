package collection;

import entity.Cardapio;
import entity.CategoriaAlimento;
import entity.ItemCardapio;
import entity.OpcaoCardapio;
import entity.PreferenciaAlimentar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Coleção responsável por localizar o cardápio vigente para seleção das refeições (Passo 7).
 * Persiste o catálogo de cardápios e opções em CSV na pasta dados/.
 * Correspondência UML: «collection» Cardapios.
 */
public class Cardapios {
    private static final String ARQUIVO_CARDAPIOS_CSV = "dados/cardapios.csv";
    private static final String ARQUIVO_ITENS_CARDAPIO_CSV = "dados/itens_cardapio.csv";

    private final List<Cardapio> cardapios = new ArrayList<>();
    private final List<OpcaoCardapio> catalogoOpcoes = new ArrayList<>();

    public Cardapios() {
        inicializarCardapiosPadrao();
    }

    /**
     * Localiza e retorna o Cardapio vigente na data informada.
     * Correspondência no Diagrama de Sequência: mensagem 78 buscarVigente(data: Data): Cardapio.
     */
    public Cardapio buscarVigente(LocalDate data) {
        for (Cardapio c : cardapios) {
            if (c.estaVigente(data)) {
                return c;
            }
        }
        return cardapios.isEmpty() ? null : cardapios.get(0);
    }

    public List<OpcaoCardapio> getCatalogoOpcoes() {
        return Collections.unmodifiableList(catalogoOpcoes);
    }

    public List<Cardapio> getCardapios() {
        return Collections.unmodifiableList(cardapios);
    }

    private void inicializarCardapiosPadrao() {
        PreferenciaAlimentar tradicional = new PreferenciaAlimentar("Tradicional");
        PreferenciaAlimentar vegetariana = new PreferenciaAlimentar("Vegetariana");
        PreferenciaAlimentar semLactose = new PreferenciaAlimentar("Sem lactose");

        List<PreferenciaAlimentar> todas = Arrays.asList(tradicional, vegetariana, semLactose);
        List<PreferenciaAlimentar> tradSemLactose = Arrays.asList(tradicional, semLactose);
        List<PreferenciaAlimentar> tradVeg = Arrays.asList(tradicional, vegetariana);
        List<PreferenciaAlimentar> apenasTrad = Collections.singletonList(tradicional);

        // Pratos Principais (cada unidade conta como 1 refeição do plano)
        catalogoOpcoes.add(new OpcaoCardapio("Filé de Frango Grelhado com Ervas Finas", CategoriaAlimento.PRATO_PRINCIPAL, tradSemLactose));
        catalogoOpcoes.add(new OpcaoCardapio("Escondidinho de Carne Seca com Mandioquinha", CategoriaAlimento.PRATO_PRINCIPAL, apenasTrad));
        catalogoOpcoes.add(new OpcaoCardapio("Salmão Assado ao Molho de Maracujá", CategoriaAlimento.PRATO_PRINCIPAL, tradSemLactose));
        catalogoOpcoes.add(new OpcaoCardapio("Lasanha de Berinjela Gratinada ao Sugo", CategoriaAlimento.PRATO_PRINCIPAL, tradVeg));
        catalogoOpcoes.add(new OpcaoCardapio("Moqueca de Banana-da-Terra com Castanhas", CategoriaAlimento.PRATO_PRINCIPAL, todas));
        catalogoOpcoes.add(new OpcaoCardapio("Bowl Proteico de Quinoa e Tofu Grelhado", CategoriaAlimento.PRATO_PRINCIPAL, todas));

        // Acompanhamentos (complementam as refeições)
        catalogoOpcoes.add(new OpcaoCardapio("Arroz Integral com Brócolis", CategoriaAlimento.ACOMPANHAMENTO, todas));
        catalogoOpcoes.add(new OpcaoCardapio("Mix de Legumes Assados no Azeite", CategoriaAlimento.ACOMPANHAMENTO, todas));
        catalogoOpcoes.add(new OpcaoCardapio("Purê de Batata-Doce Cremoso", CategoriaAlimento.ACOMPANHAMENTO, tradVeg));
        catalogoOpcoes.add(new OpcaoCardapio("Feijão Preto Caseiro Temperado", CategoriaAlimento.ACOMPANHAMENTO, todas));

        // Sobremesas (complementam as refeições)
        catalogoOpcoes.add(new OpcaoCardapio("Salada de Frutas Frescas da Estação", CategoriaAlimento.SOBREMESA, todas));
        catalogoOpcoes.add(new OpcaoCardapio("Mousse de Cacau 70% Zero Lactose", CategoriaAlimento.SOBREMESA, todas));
        catalogoOpcoes.add(new OpcaoCardapio("Pudim de Chia com Calda de Manga", CategoriaAlimento.SOBREMESA, todas));
        catalogoOpcoes.add(new OpcaoCardapio("Cheesecake Tradicional de Frutas Vermelhas", CategoriaAlimento.SOBREMESA, tradVeg));

        // Cardápio vigente atual
        LocalDate hoje = LocalDate.now();
        Cardapio cardapioVigente = new Cardapio(hoje.minusDays(7), hoje.plusDays(30));
        for (OpcaoCardapio opcao : catalogoOpcoes) {
            cardapioVigente.adicionarOferta(opcao);
        }
        cardapios.add(cardapioVigente);

        // Cardápio histórico encerrado (fora de vigência, útil para testes de FA2)
        Cardapio cardapioEncerrado = new Cardapio(hoje.minusDays(60), hoje.minusDays(31));
        cardapioEncerrado.adicionarOferta(new OpcaoCardapio("Risoto Sazonal de Inverno (Fora de Vigência)", CategoriaAlimento.PRATO_PRINCIPAL, todas));
        cardapios.add(cardapioEncerrado);

        persistirCardapiosCSV();
    }

    private void persistirCardapiosCSV() {
        try {
            File arqCardapios = new File(ARQUIVO_CARDAPIOS_CSV);
            if (arqCardapios.getParentFile() != null) {
                arqCardapios.getParentFile().mkdirs();
            }
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(arqCardapios, false), StandardCharsets.UTF_8))) {
                writer.write("idCardapio;inicioVigencia;fimVigencia;vigenteHoje;quantidadeOfertas");
                writer.newLine();
                int id = 1;
                for (Cardapio c : cardapios) {
                    writer.write(String.format("%d;%s;%s;%s;%d",
                            id++,
                            c.getInicioVigencia(),
                            c.getFimVigencia(),
                            c.estaVigente(LocalDate.now()),
                            c.getOfertas().size()));
                    writer.newLine();
                }
            }

            File arqItens = new File(ARQUIVO_ITENS_CARDAPIO_CSV);
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(arqItens, false), StandardCharsets.UTF_8))) {
                writer.write("idCardapio;inicioVigencia;fimVigencia;nomeOpcao;categoria;preferenciasAtendidas");
                writer.newLine();
                int idCardapio = 1;
                for (Cardapio c : cardapios) {
                    for (ItemCardapio item : c.getOfertas()) {
                        OpcaoCardapio op = item.getOpcao();
                        String prefs = op.getPreferenciasAtendidas().stream()
                                .map(PreferenciaAlimentar::getDescricao)
                                .collect(Collectors.joining(","));
                        writer.write(String.format("%d;%s;%s;%s;%s;%s",
                                idCardapio,
                                c.getInicioVigencia(),
                                c.getFimVigencia(),
                                op.getNome(),
                                op.getCategoria().name(),
                                prefs));
                        writer.newLine();
                    }
                    idCardapio++;
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar cardápios em CSV: " + e.getMessage());
        }
    }
}
