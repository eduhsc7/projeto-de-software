package service;

/**
 * Processador responsável pelo envio do código de confirmação por SMS ao celular do assinante.
 * Correspondência UML: «processor» ProcessadorSMS.
 */
public class ProcessadorSMS {

    /**
     * Envia a mensagem contendo o código de confirmação para o celular informado.
     * Correspondência no Diagrama de Sequência: mensagem 30 enviar(celular: Texto, mensagem: Texto): void.
     */
    public void enviar(String celular, String mensagem) {
        System.out.println("\n  +------------------------------------------------------------------+");
        System.out.println("  | [SMS ENVIADO -> " + celular + "]");
        System.out.println("  | " + mensagem);
        System.out.println("  +------------------------------------------------------------------+");
    }
}
