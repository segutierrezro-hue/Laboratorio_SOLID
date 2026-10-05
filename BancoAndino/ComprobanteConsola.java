public class ComprobanteConsola implements EmisorComprobante {
    @Override
    public void emitir(String origen, String destino, double monto, double comision) {
        System.out.println("===== BANCO ANDINO - COMPROBANTE =====");
        System.out.println("Origen: " + origen);
        System.out.println("Destino: " + destino);
        System.out.println("Monto: $" + monto);
        System.out.println("Comisión: $" + comision);
        System.out.println("======================================");
    }
}