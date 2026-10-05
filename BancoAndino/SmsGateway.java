public class SmsGateway implements Notificador {
    @Override
    public void notificar(String destinatario, String mensaje) {
        System.out.println("[SMS] Conectando al proveedor de mensajería...");
        System.out.println("[SMS] Para " + destinatario + ": " + mensaje);
    }
}