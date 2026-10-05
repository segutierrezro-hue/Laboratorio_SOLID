import java.time.Clock;
import java.time.LocalDate;

/**
 * R2 - Cuenta infantil: depósitos sin límite, retiros de máximo $200.000 por día.
 * Se puede usar como origen de transferencias y se le cobra la cuota de manejo.
 */
public class CuentaInfantil extends CuentaConRetiros {
    public static final double LIMITE_RETIRO_DIARIO = 200_000;

    private final Clock reloj;
    private LocalDate fechaRetiros;
    private double retiradoEseDia;

    public CuentaInfantil(String numero, String titular, double saldoInicial) {
        this(numero, titular, saldoInicial, Clock.systemDefaultZone());
    }

    // El reloj se puede inyectar para poder probar el cambio de día
    public CuentaInfantil(String numero, String titular, double saldoInicial, Clock reloj) {
        super(numero, titular, saldoInicial);
        this.reloj = reloj;
    }

    @Override
    public void retirar(double monto) {
        LocalDate hoy = LocalDate.now(reloj);
        double acumulado = hoy.equals(fechaRetiros) ? retiradoEseDia : 0;

        if (acumulado + monto > LIMITE_RETIRO_DIARIO) {
            throw new IllegalStateException("Supera el límite diario de retiros de la cuenta infantil");
        }
        super.retirar(monto); // valida saldo; si falla no se registra nada

        fechaRetiros = hoy;
        retiradoEseDia = acumulado + monto;
    }
}