import java.time.LocalDateTime;

public class AuditoriaConsola implements Auditoria {
    @Override
    public void registrar(String tipo, String origen, String destino, double monto) {
        System.out.println("[AUDITORIA] " + LocalDateTime.now() + " " + tipo
            + " " + origen + " -> " + destino + " $" + monto);
    }
}