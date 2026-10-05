public abstract class CuentaConRetiros extends Cuenta {
    public CuentaConRetiros(String numero, String titular, double saldoInicial) {
        super(numero, titular, saldoInicial);
    }

    public void retirar(double monto) {
        cobrar(monto);
    }

    // Cargo del banco (p. ej. cuota de manejo): no cuenta como retiro del cliente,
    // por eso no le aplican límites de retiro como el de la cuenta infantil.
    public void cobrar(double monto) {
        if (monto > saldo) throw new IllegalStateException("Saldo insuficiente");
        saldo -= monto;
    }
}