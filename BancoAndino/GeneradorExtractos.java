import java.util.List;

public class GeneradorExtractos {
    public void imprimir(List<? extends Extractable> productos) {
        for (Extractable p : productos) System.out.println(p.generarExtracto());
    }
}