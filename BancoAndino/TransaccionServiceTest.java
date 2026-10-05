import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TransaccionServiceTest {

    // ---- Dobles de prueba  ----

    // Guarda las transacciones en una lista en memoria en vez de Oracle
    static class RepoEnMemoria implements RepositorioTransacciones {
        final List<String> guardadas = new ArrayList<>();

        @Override
        public void guardarTransaccion(String origen, String destino, double monto, double comision) {
            guardadas.add(origen + "->" + destino + ":" + monto + ":" + comision);
        }
    }

    // Anota los mensajes en vez de enviar SMS
    static class NotificadorEspia implements Notificador {
        final List<String> mensajes = new ArrayList<>();

        @Override
        public void notificar(String destinatario, String mensaje) {
            mensajes.add(mensaje);
        }
    }

    // No imprimen nada: aquí no nos interesa el comprobante ni la auditoría
    static class ComprobanteNulo implements EmisorComprobante {
        @Override
        public void emitir(String origen, String destino, double monto, double comision) { }
    }

    static class AuditoriaNula implements Auditoria {
        @Override
        public void registrar(String tipo, String origen, String destino, double monto) { }
    }

    // ---- Preparación donde se ejecuta antes de cada prueba ----
    RepoEnMemoria repo;
    NotificadorEspia notificador;
    TransaccionService servicio;
    CuentaConRetiros origen;
    CuentaAhorros destino;

    @BeforeEach
    void preparar() {
        repo = new RepoEnMemoria();
        notificador = new NotificadorEspia();
        servicio = new TransaccionService(
            Map.of("MISMO_BANCO", new ComisionMismoBanco(),
                   "OTRO_BANCO", new ComisionOtroBanco(),
                   "INTERNACIONAL", new ComisionInternacional()),
            repo, new ComprobanteNulo(), notificador, new AuditoriaNula());
        origen = new CuentaAhorros("001-1", "Ana", 1_000_000);
        destino = new CuentaAhorros("001-2", "Luis", 500_000);
    }

    // Prueba 1
    @Test
    void mismoBancoNoCobraComisionYMueveElMonto() {
        servicio.transferir(origen, destino, 100_000, "MISMO_BANCO");

        assertEquals(900_000, origen.getSaldo(), 0.001);
        assertEquals(600_000, destino.getSaldo(), 0.001);
    }

    // Prueba 2
    @Test
    void otroBancoCobra7500DeComision() {
        servicio.transferir(origen, destino, 100_000, "OTRO_BANCO");

        assertEquals(1_000_000 - 100_000 - 7_500, origen.getSaldo(), 0.001);
        assertEquals(600_000, destino.getSaldo(), 0.001);
    }

    // Prueba 3
    @Test
    void saldoInsuficienteRechazaYNoGuardaNiNotifica() {
        assertThrows(IllegalStateException.class,
            () -> servicio.transferir(origen, destino, 1_000_000, "OTRO_BANCO"));

        assertTrue(repo.guardadas.isEmpty());
        assertTrue(notificador.mensajes.isEmpty());
        assertEquals(1_000_000, origen.getSaldo(), 0.001);
    }

    // Prueba 4
    @Test
    void transferenciaExitosaSeGuardaUnaVezYNotificaUnaVez() {
        servicio.transferir(origen, destino, 100_000, "MISMO_BANCO");

        assertEquals(1, repo.guardadas.size());
        assertEquals(1, notificador.mensajes.size());
    }

    // Prueba 5
    @Test
    void tipoDesconocidoSeRechazaYNoCambiaElSaldo() {
        assertThrows(IllegalArgumentException.class,
            () -> servicio.transferir(origen, destino, 100_000, "MARCIANA"));

        assertEquals(1_000_000, origen.getSaldo(), 0.001);
        assertTrue(repo.guardadas.isEmpty());
    }
}