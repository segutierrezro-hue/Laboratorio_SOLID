public class OracleRepositorio {
    public void guardarTransaccion(String origen, String destino,
                                   double monto, double comision) {
        System.out.println("[ORACLE] Conectando a jdbc:oracle:thin:@prod-db:1521/BANCO...");
        System.out.println("[ORACLE] INSERT INTO transacciones VALUES ('"
                + origen + "', '" + destino + "', " + monto + ", " + comision + ")");
    }
}