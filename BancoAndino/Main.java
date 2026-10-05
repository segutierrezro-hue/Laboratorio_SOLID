import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        Map<String, PoliticaComision> comisiones = Map.of(
            "MISMO_BANCO", new ComisionMismoBanco(),
            "OTRO_BANCO", new ComisionOtroBanco(),
            "INTERNACIONAL", new ComisionInternacional(),
            "LLAVE", new ComisionLlave());

        TransaccionService servicio = new TransaccionService(
            comisiones,
            new OracleRepositorio(),
            new ComprobanteConsola(),
            new SmsGateway(),
            new AuditoriaConsola());

        /* ===== PRUEBA TEMPORAL DEL PUNTO D (borrar después) =====
        TransaccionService servicioDePrueba = new TransaccionService(
            comisiones,
            (o, d, m, c) -> System.out.println("[FALSO] repositorio: no se conectó a Oracle"),
            (o, d, m, c) -> { },
            (dest, msg) -> System.out.println("[FALSO] notificador: no se envió SMS"),
            (t, o, d, m) -> { });

        CuentaConRetiros origenPrueba = new CuentaAhorros("TEST-1", "Prueba", 1_000_000);
        CuentaConRetiros destinoPrueba = new CuentaAhorros("TEST-2", "Prueba", 0);
        servicioDePrueba.transferir(origenPrueba, destinoPrueba, 100_000, "OTRO_BANCO");
        System.out.println("Saldo origen de prueba: " + origenPrueba.getSaldo());*/
        
        // ---- Uso ----
        CuentaConRetiros ana = new CuentaAhorros("001-1", "Ana", 2_000_000);
        CuentaConRetiros luis = new CuentaAhorros("001-2", "Luis", 500_000);
        Cuenta cdtAna = new CDT("CDT-9", "Ana", 10_000_000, LocalDate.now().plusMonths(6));

        servicio.transferir(ana, luis, 150_000, "OTRO_BANCO");

        // R1: transferencia por llave, sin comisión (se descuenta exactamente el monto)
        double saldoAntes = ana.getSaldo();
        servicio.transferir(ana, luis, 50_000, "LLAVE");
        System.out.println("Descuento LLAVE: $" + (saldoAntes - ana.getSaldo()));

        // R2: cuenta infantil, retiros máximo $200.000 por día
        CuentaInfantil nino = new CuentaInfantil("INF-1", "Sofía", 1_000_000);
        nino.retirar(150_000);
        try {
            nino.retirar(60_000);
        } catch (IllegalStateException e) {
            System.out.println("Retiro rechazado: " + e.getMessage() + " | saldo: $" + nino.getSaldo());
        }

        new CobroCuotaManejo().cobrarMensual(List.of(ana, luis, nino));
        
        List<Extractable> productos =
            List.of(new TarjetaCredito(3_000_000), new CreditoVivienda(120_000_000));
        new GeneradorExtractos().imprimir(productos);
    }
}