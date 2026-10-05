import java.util.List;

/**
 * Registra cada transacción exitosa en todos los destinos configurados
 * (auditoría, antifraude, ...). Para TransaccionService es una sola Auditoria,
 * así que agregar un destino nuevo no obliga a modificar el servicio.
 */
public class AuditoriaMultiple implements Auditoria {
    private final List<Auditoria> destinos;

    public AuditoriaMultiple(List<Auditoria> destinos) {
        this.destinos = List.copyOf(destinos);
    }

    @Override
    public void registrar(String tipo, String origen, String destino, double monto) {
        for (Auditoria d : destinos) {
            d.registrar(tipo, origen, destino, monto);
        }
    }
}