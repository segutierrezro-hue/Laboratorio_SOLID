import java.util.List;

public class CobroCuotaManejo {
    private static final double CUOTA = 12_900;

    public void cobrarMensual(List<Cuenta> cuentas) {
        for (Cuenta cuenta : cuentas) {
            cuenta.retirar(CUOTA);
            System.out.println("Cuota de manejo cobrada a " + cuenta.getNumero());
        }
    }
}