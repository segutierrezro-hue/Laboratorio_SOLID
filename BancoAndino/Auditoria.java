public interface Auditoria {
    void registrar(String tipo, String origen, String destino, double monto);
}