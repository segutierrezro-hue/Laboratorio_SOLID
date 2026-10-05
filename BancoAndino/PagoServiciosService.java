/**
 * R6 - Pago de facturas de servicios públicos (agua, luz, gas, internet).
 * Reutiliza las mismas piezas que TransaccionService: validación de monto,
 * política de comisión, repositorio, comprobante, notificador y auditoría.
 * El origen es CuentaConRetiros, así que un CDT no puede pagar (error de compilación).
 */
public class PagoServiciosService {
    private static final String TIPO = "PAGO_SERVICIOS";

    private final ValidadorMonto validador;
    private final PoliticaComision comision;
    private final RepositorioTransacciones repositorio;
    private final EmisorComprobante comprobante;
    private final Notificador notificador;
    private final Auditoria auditoria;

    public PagoServiciosService(ValidadorMonto validador,
                                PoliticaComision comision,
                                RepositorioTransacciones repositorio,
                                EmisorComprobante comprobante,
                                Notificador notificador,
                                Auditoria auditoria) {
        this.validador = validador;
        this.comision = comision;
        this.repositorio = repositorio;
        this.comprobante = comprobante;
        this.notificador = notificador;
        this.auditoria = auditoria;
    }

    public void pagar(CuentaConRetiros origen, String referenciaFactura, double monto) {
        validador.validar(monto);
        double valorComision = comision.calcular(monto);

        origen.retirar(monto + valorComision);

        repositorio.guardarTransaccion(origen.getNumero(), referenciaFactura, monto, valorComision);
        comprobante.emitir(origen.getNumero(), referenciaFactura, monto, valorComision);
        notificador.notificar(origen.getTitular(),
            "Pagaste $" + monto + " de la factura " + referenciaFactura);
        auditoria.registrar(TIPO, origen.getNumero(), referenciaFactura, monto);
    }
}
