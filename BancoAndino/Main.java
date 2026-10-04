import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Cuenta ana = new CuentaAhorros("001-1", "Ana", 2_000_000);
        Cuenta luis = new CuentaAhorros("001-2", "Luis", 500_000);
        Cuenta cdtAna = new CDT("CDT-9", "Ana", 10_000_000, LocalDate.now().plusMonths(6));

        TransaccionService servicio = new TransaccionService();
        servicio.transferir(ana, luis, 150_000, "OTRO_BANCO");

        new CobroCuotaManejo().cobrarMensual(List.of(ana, luis, cdtAna));

        List<ProductoBancario> productos =
                List.of(new TarjetaCredito(3_000_000), new CreditoVivienda(120_000_000));
        for (ProductoBancario p : productos) System.out.println(p.generarExtracto());
    }
}