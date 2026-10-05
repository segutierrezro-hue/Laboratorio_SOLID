import java.util.List;

/**
 * Envía cada notificación por todos los canales configurados (SMS, push, ...).
 * Para el servicio es un solo Notificador, así que agregar un canal nuevo
 * no obliga a modificar TransaccionService.
 */
public class NotificadorMultiple implements Notificador {
    private final List<Notificador> canales;

    public NotificadorMultiple(List<Notificador> canales) {
        this.canales = List.copyOf(canales);
    }

    @Override
    public void notificar(String destinatario, String mensaje) {
        for (Notificador canal : canales) {
            canal.notificar(destinatario, mensaje);
        }
    }
}