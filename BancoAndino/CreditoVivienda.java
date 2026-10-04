public class CreditoVivienda implements ProductoBancario {
    private double saldoPendiente;

    public CreditoVivienda(double valorPrestamo) { this.saldoPendiente = valorPrestamo; }

    public void depositar(double monto) { } // no aplica
    public void retirar(double monto) { } // no aplica
    public double calcularIntereses() { return saldoPendiente * 0.011; }
    public void pagarCuota(double monto) { saldoPendiente -= monto; }
    public String generarExtracto() { return "Crédito vivienda - pendiente: $" + saldoPendiente; }
}