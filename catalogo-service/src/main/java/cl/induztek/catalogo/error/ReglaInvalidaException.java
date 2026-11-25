package cl.induztek.catalogo.error;

public class ReglaInvalidaException extends RuntimeException {

    public ReglaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
