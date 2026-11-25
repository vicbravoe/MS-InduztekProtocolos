package cl.induztek.protocolos.error;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ManejadorErroresTest {

    private final ManejadorErrores manejador = new ManejadorErrores();

    @Test
    void noEncontradoDevuelve404() {
        ResponseEntity<ErrorRespuesta> r = manejador.noEncontrado(new RecursoNoEncontradoException("no existe"));
        assertEquals(HttpStatus.NOT_FOUND, r.getStatusCode());
        assertEquals("no existe", r.getBody().mensaje());
        assertEquals(404, r.getBody().estado());
    }

    @Test
    void conflictoDevuelve409() {
        ResponseEntity<ErrorRespuesta> r = manejador.conflicto(new ConflictoException("repetido"));
        assertEquals(HttpStatus.CONFLICT, r.getStatusCode());
        assertEquals("repetido", r.getBody().mensaje());
    }

    @Test
    void reglaInvalidaDevuelve400() {
        ResponseEntity<ErrorRespuesta> r = manejador.reglaInvalida(new ReglaInvalidaException("mal"));
        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        assertEquals("mal", r.getBody().mensaje());
    }

    @Test
    void cuerpoInvalidoDevuelve400() {
        ResponseEntity<ErrorRespuesta> r = manejador.cuerpoInvalido(new IllegalArgumentException("x"));
        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        assertEquals("La solicitud tiene un formato inválido", r.getBody().mensaje());
    }
}
