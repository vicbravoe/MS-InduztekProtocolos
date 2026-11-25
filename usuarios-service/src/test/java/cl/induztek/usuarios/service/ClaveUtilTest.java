package cl.induztek.usuarios.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClaveUtilTest {

    @Test
    void laClaveCorrectaCoincideConSuHash() {
        String hash = ClaveUtil.hashear("Induztek2026");
        assertTrue(ClaveUtil.coincide("Induztek2026", hash));
    }

    @Test
    void unaClaveDistintaNoCoincide() {
        String hash = ClaveUtil.hashear("Induztek2026");
        assertFalse(ClaveUtil.coincide("induztek2026", hash));
    }

    @Test
    void elHashNoContieneLaClaveYCambiaEnCadaLlamada() {
        String uno = ClaveUtil.hashear("secreto1");
        String dos = ClaveUtil.hashear("secreto1");
        assertFalse(uno.contains("secreto1"));
        assertNotEquals(uno, dos);
    }

    @Test
    void valoresNulosOMalFormadosNoCoinciden() {
        assertFalse(ClaveUtil.coincide(null, "aa:bb"));
        assertFalse(ClaveUtil.coincide("x", null));
        assertFalse(ClaveUtil.coincide("x", "sinformato"));
        assertFalse(ClaveUtil.coincide("x", "zz:yy"));
    }
}
