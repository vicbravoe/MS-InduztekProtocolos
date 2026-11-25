package cl.induztek.usuarios.error;

public class ReglaInvalidaException extends RuntimeException {

    public ReglaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
