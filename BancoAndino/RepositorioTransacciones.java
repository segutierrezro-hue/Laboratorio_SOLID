public interface RepositorioTransacciones {
    void guardarTransaccion(String origen, String destino, double monto, double comision);
}