import java.util.List;

public class CobroCuotaManejo {
    private static final double CUOTA = 12_900;

    public void cobrarMensual(List<CuentaConRetiros> cuentas) {
        for (CuentaConRetiros cuenta : cuentas) {
            cuenta.cobrar(CUOTA);
            System.out.println("Cuota de manejo cobrada a " + cuenta.getNumero());
        }
    }
}