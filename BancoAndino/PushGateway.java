/**
 * R3 - Notificación push en la app del cliente (simulada con consola).
 */
public class PushGateway implements Notificador {
    @Override
    public void notificar(String destinatario, String mensaje) {
        System.out.println("[PUSH] Enviando notificación a la app de " + destinatario + ": " + mensaje);
    }
}