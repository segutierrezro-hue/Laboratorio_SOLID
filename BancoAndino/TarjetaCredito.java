public class TarjetaCredito implements Extractable, GeneraIntereses, Pagable, AvanceEfectivo {
    private double deuda;
    private final double cupo;

    public TarjetaCredito(double cupo) { this.cupo = cupo; }

    @Override
    public void avance(double monto) {
        if (deuda + monto > cupo) throw new IllegalStateException("Cupo insuficiente");
        deuda += monto;
    }
    @Override public double calcularIntereses() { return deuda * 0.028; }
    @Override public void pagarCuota(double monto) { deuda -= monto; }
    @Override public String generarExtracto() { return "Tarjeta - deuda: $" + deuda; }
}