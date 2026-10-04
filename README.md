# Laboratorio_SOLID
Desarrollo del Laboratorio L2: SOLID , Taller integrador: el backend de Banco Andino

##  1.1 Tabla de hallazgos

| Clase / método| Letra| Evidencia en el código| Consecuencia para el banco o el cliente |
|---------------|------|-----------------------|-----------------------------------------|
| `TransaccionService.transferir` | S | Un solo método se encarga de validar, calcular la comisión, mover el dinero, guardar en Oracle, imprimir el comprobante, mandar el SMS y registrar la auditoría: siete tareas distintas en una sola clase. | Si el área legal cambia el formato del comprobante o alguien cambia el texto del SMS, hay que abrir la misma clase que mueve la plata. Un cambio cosmético puede terminar cobrando mal una transferencia. |
| `TransaccionService.transferir` | O | Hay un `switch (tipo)` con los casos `MISMO_BANCO`, `OTRO_BANCO` e `INTERNACIONAL`. Para agregar un tipo nuevo hay que editar ese método. | El banco no puede lanzar un tipo de transferencia nuevo sin modificar el servicio central, y cada cambio pone en riesgo los tipos que ya funcionaban. |
| `TransaccionService` (atributos) | D | La clase hace `new OracleRepositorio()` y `new SmsGateway()` por su cuenta. No hay interfaces ni forma de que le pasen otras implementaciones. | No se puede cambiar de motor de base de datos ni de proveedor de SMS sin editar el servicio. Tampoco se puede probar: cada prueba se conecta a producción y le manda un SMS real al cliente. |
| `CDT.retirar` | L | `CDT` extiende `Cuenta`, pero su `retirar` lanza `UnsupportedOperationException` si el CDT no ha vencido. | Un CDT no se puede usar donde se espera una `Cuenta`. Si se cuela en una lista de cuentas, el programa falla en ejecución y no al compilar. |
| `CobroCuotaManejo.cobrarMensual` | L | Recibe una `List<Cuenta>` y llama `cuenta.retirar(CUOTA)` suponiendo que todas se pueden retirar. | Con un solo CDT en la lista, el cobro nocturno se detiene en esa cuenta: las anteriores ya quedaron cobradas, las siguientes nunca se cobran y no hay reversa. |
| `ProductoBancario` | I | Una sola interfaz con cinco métodos (`depositar`, `retirar`, `calcularIntereses`, `pagarCuota`, `generarExtracto`) que se le exige a todos los productos. | Quien crea un producto nuevo queda obligado a implementar métodos que no tienen sentido para él. La interfaz promete cosas que algunos productos no pueden cumplir. |
| `TarjetaCredito.depositar` | I | El método está vacío con el comentario `// no aplica`. | Si alguien llama `depositar` sobre una tarjeta, no pasa nada y tampoco hay error: el cliente cree que abonó y su deuda sigue igual. |
| `TarjetaCredito.retirar` | L | En una cuenta `retirar` baja el saldo, pero en la tarjeta es un avance en efectivo y sube la deuda. | El mismo nombre significa cosas opuestas. Código genérico que use `ProductoBancario` puede hacer un movimiento contable al revés de lo que espera. |
| `CreditoVivienda.depositar` y `retirar` | I / L | Los dos métodos están vacíos (`{ }`) con el comentario `// no aplica`. | El contrato de `ProductoBancario` no se cumple. Cualquier código genérico que opere con productos falla en silencio cuando le toca un crédito de vivienda. |

##  1.2 Dos experimentos

### 1.2.1 El CDT

Agregamos cdtAna a la lista que recibe cobrarMensual en el Main:
`new CobroCuotaManejo().cobrarMensual(List.of(ana, luis, cdtAna));`

**Resultado observado** 

```text
Cuota de manejo cobrada a 001-1
Cuota de manejo cobrada a 001-2
Exception in thread "main" java.lang.UnsupportedOperationException: Un CDT no permite retiros antes del vencimiento
        at CDT.retirar(CDT.java:14)
        at CobroCuotaManejo.cobrarMensual(CobroCuotaManejo.java:8)
        at Main.main(Main.java:13)
```

**¿Qué pasa?** El programa se cae apenas llega al CDT. CobroCuotaManej recibe una `List<Cuenta>` y le llama retirar a cada elemento, asumiendo que todas las cuentas se comportan igual. Pero CDT sobrescribe retirar para bloquear el retiro antes del vencimiento y lanza una excepción, de esta manera nadie la captura, así que el hilo principal termina

**¿Qué pasaría en producción con un millón de cuentas y el CDT en la posición 500.000?**
Se cobrarían bien las primeras 499.999 cuentas, pero el proceso fallaría en el CDT y las 500.000 cuentas siguientes más el propio CDT y quedarían sin cobro, con esto tenemos las sigueintes consecuencias:

- El banco deja de recibir ese día aproximadamente la mitad de las cuotas de manejo.
- Como el proceso corre de noche el fallo se note hasta el día siguiente.
- No hay transacción ni reversa ya que el cobro queda a medias y hay que averiguar a mano hasta qué cuenta llegó.
- Reintentar el proceso completo cobraría otra vez a las primeras 499.999 cuentas y volvería a fallar en el mismo CDT y nunca llegaría a las demás.
- Cobrar con retraso a los clientes restantes podría traer reclamos y problemas con la contabilidad del banco.

### .2.2 La prueba imposible

**Objetivo:** probar que una transferencia `OTRO_BANCO` cobra $7.500 de comisión,
sin conectarse a Oracle y sin enviar un SMS.

**Intento 1: leer lo que se imprime.** Escribimos una prueba JUnit que captura
`System.out` con `System.setOut(...)` y hace
`assertTrue(salida.contains("Comisión: $7500.0"))`. La prueba pasa, pero no cumple
la condición, y además tiene otros problemas:

- Se conecta a Oracle: aparece la línea `[ORACLE] Conectando...` en la salida.
- Envía el SMS: aparece la línea `[SMS] Para Ana: ...`.
- Es frágil: si alguien cambia el formato del comprobante, la prueba se rompe
  aunque la comisión esté bien calculada.

**Intento 2: verificar el saldo.** Probamos comprobando que el saldo de `ana`
quedara en `2_000_000 - 150_000 - 7_500`. Esto sí verifica la comisión de verdad y
no depende del formato del texto, pero la transferencia se ejecuta completa, así que
sigue tocando Oracle y mandando el SMS. Tampoco cumple la condición.

**Intento 3: reemplazar las dependencias por falsas.** Intentamos escribir
`new TransaccionService(repoFalso, notifFalso)`, pero no compila: la clase no tiene
constructor con parámetros porque crea sus dependencias adentro con `new`. Tampoco
podríamos fabricar los falsos, porque no existe una interfaz que implementar.

**Intento 4: probar el cálculo por separado.** Buscamos un método tipo
`calcularComision(tipo, monto)`, pero no existe: el cálculo está metido en el
`switch` dentro de `transferir`.

**Resultado:** no logramos cumplir la condición. La prueba que sí corre toca la
infraestructura real, y las que la cumplirían no compilan.

**Qué lo impide:**

1. **D (inversión de dependencias):** `TransaccionService` crea `OracleRepositorio`
   y `SmsGateway` concretos con `new`, así que no hay manera de reemplazarlos
   desde afuera.
2. **S (responsabilidad única):** `transferir` hace siete cosas a la vez, y como es
   `void`, lo único observable son sus efectos secundarios (consola y saldos).
3. **O (abierto/cerrado):** el cálculo de la comisión está dentro de un `switch`.
   No se puede probar solo ni extender sin editar el método.

**Cómo lo resolveremos en el bloque 2:**

- Separar el cálculo en una interfaz `PoliticaComision`, con una clase por tipo de
  transferencia.
- Crear las interfaces `RepositorioTransacciones`, `Notificador`, `EmisorComprobante`
  y `Auditoria`, e inyectarlas en `TransaccionService` por el constructor.

Con eso, la prueba usa dobles que guardan los datos en memoria y comprueba la
comisión a través del saldo de la cuenta de origen:

```java
servicio.transferir(origen, destino, 100_000, "OTRO_BANCO");
assertEquals(1_000_000 - 100_000 - 7_500, origen.getSaldo(), 0.001);
```

Sin Oracle, sin SMS y en milisegundos.

