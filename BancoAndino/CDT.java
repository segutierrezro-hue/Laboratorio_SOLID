import java.time.LocalDate;

public class CDT extends Cuenta {
    private final LocalDate vencimiento;

    public CDT(String numero, String titular, double monto, LocalDate vencimiento) {
        super(numero, titular, monto);
        this.vencimiento = vencimiento;
    }

    // El CDT no se "retira": se liquida completo al vencer.
    public double liquidar(LocalDate hoy) {
        if (hoy.isBefore(vencimiento)) {
            throw new IllegalStateException("El CDT aún no vence");
        }
        double total = saldo;
        saldo = 0;
        return total;
    }
}