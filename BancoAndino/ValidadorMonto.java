/**
 * Reglas de monto comunes a toda transacción (transferencias, pagos, ...).
 * Se extrajo de TransaccionService para que R6 pudiera reutilizarlas sin copiarlas.
 */
public class ValidadorMonto {
    public static final double TOPE_DIARIO = 5_000_000;

    public void validar(double monto) {
        if (monto <= 0) throw new IllegalArgumentException("Monto inválido");
        if (monto > TOPE_DIARIO) throw new IllegalArgumentException("Supera el tope diario");
    }
}
