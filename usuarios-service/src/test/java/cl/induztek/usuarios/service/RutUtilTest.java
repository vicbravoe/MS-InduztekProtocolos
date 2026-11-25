package cl.induztek.usuarios.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RutUtilTest {

    @Test
    void aceptaRutConDigitoVerificadorCorrecto() {
        assertTrue(RutUtil.esValido("17.284.519-3"));
        assertTrue(RutUtil.esValido("76482915-8"));
    }

    @Test
    void rechazaDigitoVerificadorIncorrecto() {
        assertFalse(RutUtil.esValido("17.284.519-4"));
    }

    @Test
    void rechazaTextoVacioCortoONoNumerico() {
        assertFalse(RutUtil.esValido(null));
        assertFalse(RutUtil.esValido("1"));
        assertFalse(RutUtil.esValido("ABC-1"));
    }

    @Test
    void calculaDigitosEspeciales() {
        assertEquals('0', RutUtil.calcularDv(0));
        assertEquals('K', RutUtil.calcularDv(6));
    }

    @Test
    void formateaConPuntosYGuion() {
        assertEquals("17.284.519-3", RutUtil.formatear("172845193"));
        assertEquals("1-9", RutUtil.formatear("1-9"));
        assertEquals("5", RutUtil.formatear("5"));
    }
}
