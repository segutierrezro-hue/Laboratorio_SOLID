import java.time.LocalDateTime;

public class TransaccionService {
    private final OracleRepositorio repositorio = new OracleRepositorio();
    private final SmsGateway sms = new SmsGateway();

    public void transferir(Cuenta origen, Cuenta destino, double monto, String tipo) {
        // 1. Validación
        if (monto <= 0) throw new IllegalArgumentException("Monto inválido");
        if (monto > 5_000_000) throw new IllegalArgumentException("Supera el tope diario");

        // 2. Cálculo de la comisión
        double comision;
        switch (tipo) {
            case "MISMO_BANCO" -> comision = 0;
            case "OTRO_BANCO" -> comision = 7_500;
            case "INTERNACIONAL" -> comision = monto * 0.03 + 25_000;
            default -> throw new IllegalArgumentException("Tipo de transferencia desconocido");
        }

        // 3. Movimiento del dinero
        origen.retirar(monto + comision);
        destino.depositar(monto);

        // 4. Persistencia
        repositorio.guardarTransaccion(origen.getNumero(), destino.getNumero(), monto, comision);

        // 5. Comprobante
        System.out.println("===== BANCO ANDINO - COMPROBANTE =====");
        System.out.println("Origen: " + origen.getNumero());
        System.out.println("Destino: " + destino.getNumero());
        System.out.println("Monto: $" + monto);
        System.out.println("Comisión: $" + comision);
        System.out.println("======================================");

        // 6. Notificación
        sms.enviar(origen.getTitular(), "Transferiste $" + monto + " a la cuenta " + destino.getNumero());

        // 7. Auditoría
        System.out.println("[AUDITORIA] " + LocalDateTime.now() + " " + tipo
                + " " + origen.getNumero() + " -> " + destino.getNumero() + " $" + monto);
    }
}