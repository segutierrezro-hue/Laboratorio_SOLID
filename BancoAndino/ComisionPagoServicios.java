/**
 * R6 - Pago de servicios públicos: comisión fija de $1.500.
 */
public class ComisionPagoServicios implements PoliticaComision {
    public double calcular(double monto) { return 1_500; }
}
