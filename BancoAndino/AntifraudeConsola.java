/**
 * R4 - Sistema antifraude del banco (simulado con consola).
 * Recibe cada transacción exitosa para su análisis.
 */
public class AntifraudeConsola implements Auditoria {
    @Override
    public void registrar(String tipo, String origen, String destino, double monto) {
        System.out.println("[ANTIFRAUDE] Transacción enviada a análisis: " + tipo
            + " " + origen + " -> " + destino + " $" + monto);
    }
}