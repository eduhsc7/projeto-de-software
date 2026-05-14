package service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ServicoSMS {
    private Map<String, String> codigosPorCelular = new HashMap<>();
    private Random random = new Random();

    public void enviarCodigo(String numeroCelular) {
        String codigo = String.format("%04d", random.nextInt(10000));
        codigosPorCelular.put(numeroCelular, codigo);

        // Simulação do envio de SMS. Em um sistema real, o código seria enviado para o celular.
        System.out.println("[SIMULAÇÃO SMS] Código enviado para " + numeroCelular + ": " + codigo);
    }

    public boolean verificarCodigo(String codigo, String numeroCelular) {
        String codigoGerado = codigosPorCelular.get(numeroCelular);
        return codigoGerado != null && codigoGerado.equals(codigo);
    }
}
