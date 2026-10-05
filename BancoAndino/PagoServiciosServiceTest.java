import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PagoServiciosServiceTest {

    // Reutiliza los dobles de TransaccionServiceTest y agrega un espía del comprobante
    static class ComprobanteEspia implements EmisorComprobante {
        final List<String> destinos = new ArrayList<>();

        @Override
        public void emitir(String origen, String destino, double monto, double comision) {
            destinos.add(destino);
        }
    }

    TransaccionServiceTest.RepoEnMemoria repo;
    TransaccionServiceTest.NotificadorEspia notificador;
    ComprobanteEspia comprobante;
    PagoServiciosService pagos;
    CuentaConRetiros cuenta;

    @BeforeEach
    void preparar() {
        repo = new TransaccionServiceTest.RepoEnMemoria();
        notificador = new TransaccionServiceTest.NotificadorEspia();
        comprobante = new ComprobanteEspia();
        pagos = new PagoServiciosService(new ValidadorMonto(), new ComisionPagoServicios(),
            repo, comprobante, notificador, new TransaccionServiceTest.AuditoriaNula());
        cuenta = new CuentaAhorros("001-1", "Ana", 1_000_000);
    }

    // Criterio de aceptación de R6
    @Test
    void pagoDescuentaMontoMasComisionGuardaYEmiteComprobanteConReferencia() {
        pagos.pagar(cuenta, "EAAB-FACT-778812", 184_300);

        assertEquals(1_000_000 - 185_800, cuenta.getSaldo(), 0.001);
        assertEquals(1, repo.guardadas.size());
        assertEquals(List.of("EAAB-FACT-778812"), comprobante.destinos);
        assertEquals(1, notificador.mensajes.size());
    }

    @Test
    void montoInvalidoSeRechazaSinGuardarNiNotificar() {
        assertThrows(IllegalArgumentException.class,
            () -> pagos.pagar(cuenta, "EAAB-FACT-778812", 6_000_000));

        assertEquals(1_000_000, cuenta.getSaldo(), 0.001);
        assertTrue(repo.guardadas.isEmpty());
        assertTrue(notificador.mensajes.isEmpty());
    }
}
