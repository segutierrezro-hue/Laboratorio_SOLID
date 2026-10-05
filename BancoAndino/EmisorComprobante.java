public interface EmisorComprobante {
    void emitir(String origen, String destino, double monto, double comision);
}