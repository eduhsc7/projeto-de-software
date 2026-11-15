package service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Processador responsável por gerar o número de protocolo da assinatura após a autorização do pagamento.
 * Correspondência UML: «processor» ProcessadorProtocolo.
 */
public class ProcessadorProtocolo {
    private static final Random RANDOM = new Random();

    /**
     * Gera o número de protocolo único da contratação.
     * Correspondência no Diagrama de Sequência: mensagem 167 gerar(): Texto.
     */
    public String gerar() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        int sufixo = RANDOM.nextInt(9000) + 1000;
        return "MR-" + timestamp + "-" + sufixo;
    }
}
