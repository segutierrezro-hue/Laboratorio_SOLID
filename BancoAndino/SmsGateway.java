public class SmsGateway {
    public void enviar(String destinatario, String mensaje) {
        System.out.println("[SMS] Conectando al proveedor de mensajería...");
        System.out.println("[SMS] Para " + destinatario + ": " + mensaje);
    }
}