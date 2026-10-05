public class CalculadoraComision {
    public double calcular(String tipo, double monto) {
        switch (tipo) {
            case "MISMO_BANCO" -> { return 0; }
            case "OTRO_BANCO" -> { return 7_500; }
            case "INTERNACIONAL" -> { return monto * 0.03 + 25_000; }
            default -> throw new IllegalArgumentException("Tipo de transferencia desconocido");
        }
    }
}