/**
 * R1 - Transferencias por llave (número de celular o cédula).
 * Son inmediatas y no tienen comisión.
 */
public class ComisionLlave implements PoliticaComision {
    public double calcular(double monto) { return 0; }
}