import java.time.LocalDate;

public class CDT extends Cuenta {
    private final LocalDate vencimiento;

    public CDT(String numero, String titular, double monto, LocalDate vencimiento) {
        super(numero, titular, monto);
        this.vencimiento = vencimiento;
    }

    @Override
    public void retirar(double monto) {
        if (LocalDate.now().isBefore(vencimiento)) {
            throw new UnsupportedOperationException(
                "Un CDT no permite retiros antes del vencimiento");
        }
        super.retirar(monto);
    }
}