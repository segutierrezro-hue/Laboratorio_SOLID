/**
 * R5 - Persistencia de transacciones en PostgreSQL (simulada con consola).
 * OracleRepositorio se conserva por si acaso
 */
public class PostgresRepositorio implements RepositorioTransacciones {
    @Override
    public void guardarTransaccion(String origen, String destino,
                                   double monto, double comision) {
        System.out.println("[POSTGRES] Conectando a jdbc:postgresql://prod-db:5432/banco...");
        System.out.println("[POSTGRES] INSERT INTO transacciones VALUES ('"
            + origen + "', '" + destino + "', " + monto + ", " + comision + ")");
    }
}