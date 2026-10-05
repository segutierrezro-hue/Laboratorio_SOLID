public abstract class Cuenta implements Extractable {
    protected final String numero;
    protected final String titular;
    protected double saldo;

    public Cuenta(String numero, String titular, double saldoInicial) {
        this.numero = numero;
        this.titular = titular;
        this.saldo = saldoInicial;
    }

    public String getNumero() { return numero; }
    public String getTitular() { return titular; }
    public double getSaldo() { return saldo; }

    public void depositar(double monto) {
        if (monto <= 0) throw new IllegalArgumentException("Monto inválido");
        saldo += monto;
    }

    @Override
    public String generarExtracto() {
        return "Cuenta " + numero + " - saldo: $" + saldo;
    }
}