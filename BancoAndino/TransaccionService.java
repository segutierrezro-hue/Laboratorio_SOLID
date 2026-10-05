import java.util.Map;

public class TransaccionService {
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
        this.comisiones = comisiones;
        this.repositorio = repositorio;
        this.comprobante = comprobante;
        this.notificador = notificador;
        this.auditoria = auditoria;
    }

    public void transferir(CuentaConRetiros origen, Cuenta destino, double monto, String tipo) {
        if (monto <= 0) throw new IllegalArgumentException("Monto inválido");
        if (monto > 5_000_000) throw new IllegalArgumentException("Supera el tope diario");

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