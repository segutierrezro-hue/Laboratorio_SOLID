import java.util.Map;

public class TransaccionService {
    private final Map<String, PoliticaComision> comisiones;
    private final OracleRepositorio repositorio = new OracleRepositorio();
    private final SmsGateway sms = new SmsGateway();
    private final ComprobanteConsola comprobante = new ComprobanteConsola();
    private final AuditoriaConsola auditoria = new AuditoriaConsola();

    public TransaccionService(Map<String, PoliticaComision> comisiones) {
        this.comisiones = comisiones;
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
        sms.enviar(origen.getTitular(), "Transferiste $" + monto + " a la cuenta " + destino.getNumero());
        auditoria.registrar(tipo, origen.getNumero(), destino.getNumero(), monto);
    }
}