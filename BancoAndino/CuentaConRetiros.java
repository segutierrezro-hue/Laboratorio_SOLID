public abstract class CuentaConRetiros extends Cuenta {
    public CuentaConRetiros(String numero, String titular, double saldoInicial) {
        super(numero, titular, saldoInicial);
    }

    public void retirar(double monto) {
        if (monto > saldo) throw new IllegalStateException("Saldo insuficiente");
        saldo -= monto;
    }
}