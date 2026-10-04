public interface ProductoBancario {
    void depositar(double monto);
    void retirar(double monto);
    double calcularIntereses();
    void pagarCuota(double monto);
    String generarExtracto();
}