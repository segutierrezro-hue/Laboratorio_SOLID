public class CreditoVivienda implements Extractable, GeneraIntereses, Pagable {
    private double saldoPendiente;

    public CreditoVivienda(double valorPrestamo) { this.saldoPendiente = valorPrestamo; }

    @Override public double calcularIntereses() { return saldoPendiente * 0.011; }
    @Override public void pagarCuota(double monto) { saldoPendiente -= monto; }
    @Override public String generarExtracto() {
        return "Crédito vivienda - pendiente: $" + saldoPendiente;
    }
}