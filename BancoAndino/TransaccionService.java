import java.util.Map;

public class TransaccionService {
    private final ValidadorMonto validador;
    private final Map<String, PoliticaComision> comisiones;
    private final RepositorioTransacciones repositorio;
    private final EmisorComprobante comprobante;
    private final Notificador notificador;
    private final Auditoria auditoria;

    public TransaccionService(Map<String, PoliticaComision> comisiones,
                              RepositorioTransacciones repositorio,
                              EmisorComprobante comprobante,
                              Notificador notificador,
                              Auditoria auditoria) {
        this(new ValidadorMonto(), comisiones, repositorio, comprobante, notificador, auditoria);
    }

    public TransaccionService(ValidadorMonto validador,
                              Map<String, PoliticaComision> comisiones,
                              RepositorioTransacciones repositorio,
                              EmisorComprobante comprobante,
                              Notificador notificador,
                              Auditoria auditoria) {
        this.validador = validador;
        this.comisiones = comisiones;
        this.repositorio = repositorio;
        this.comprobante = comprobante;
        this.notificador = notificador;
        this.auditoria = auditoria;
    }

    public void transferir(CuentaConRetiros origen, Cuenta destino, double monto, String tipo) {
        validador.validar(monto);

        PoliticaComision politica = comisiones.get(tipo);
        if (politica == null) {
            throw new IllegalArgumentException("Tipo de transferencia desconocido");
        }
        double comision = politica.calcular(monto);

        origen.retirar(monto + comision);
        destino.depositar(monto);

        repositorio.guardarTransaccion(origen.getNumero(), destino.getNumero(), monto, comision);
        comprobante.emitir(origen.getNumero(), destino.getNumero(), monto, comision);
        notificador.notificar(origen.getTitular(),
            "Transferiste $" + monto + " a la cuenta " + destino.getNumero());
        auditoria.registrar(tipo, origen.getNumero(), destino.getNumero(), monto);
    }
}