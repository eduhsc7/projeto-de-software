package service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class GeradorProtocolo {
    public String gerarProtocolo(String assinaturaId) {
        String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int numeroAleatorio = new Random().nextInt(9000) + 1000;
        return "PROT-" + assinaturaId + "-" + dataHora + "-" + numeroAleatorio;
    }
}
