package cl.induztek.catalogo.service;

import cl.induztek.catalogo.error.ConflictoException;
import cl.induztek.catalogo.error.RecursoNoEncontradoException;
import cl.induztek.catalogo.error.ReglaInvalidaException;
import cl.induztek.catalogo.model.Equipo;
import cl.induztek.catalogo.model.Instalacion;
import cl.induztek.catalogo.model.Instrumento;
import cl.induztek.catalogo.model.TipoEquipo;
import cl.induztek.catalogo.repository.EquipoRepository;
import cl.induztek.catalogo.repository.InstalacionRepository;
import cl.induztek.catalogo.repository.InstrumentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstrumentoServiceTest {

    @Mock
    private InstrumentoRepository repositorio;

    @InjectMocks
    private InstrumentoService servicio;

    private Instrumento instrumento() {
        return new Instrumento("Megóhmetro", "Megger", "MIT525", "S-1", 1000L, 2000L);
    }

    @Test
    void listaOrdenadoPorNombre() {
        when(repositorio.findAll(any(Sort.class))).thenReturn(List.of(instrumento()));
        assertEquals(1, servicio.listar().size());
    }

    @Test
    void obtenerLanzaNoEncontrado() {
        when(repositorio.findById(3L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.obtener(3L));
    }

    @Test
    void crearGuardaConFechasValidas() {
        when(repositorio.save(any(Instrumento.class))).thenAnswer(i -> i.getArgument(0));
        Instrumento i = instrumento();
        i.id = 8L;
        assertNull(servicio.crear(i).id);
    }

    @Test
    void crearRechazaVencimientoAnteriorALaCalibracion() {
        Instrumento i = instrumento();
        i.fechaVencimiento = 500L;
        assertThrows(ReglaInvalidaException.class, () -> servicio.crear(i));
        verify(repositorio, never()).save(any());
    }

    @Test
    void crearRechazaVencimientoIgualALaCalibracion() {
        Instrumento i = instrumento();
        i.fechaVencimiento = i.fechaUltimaCalibracion;
        assertThrows(ReglaInvalidaException.class, () -> servicio.crear(i));
    }

    @Test
    void actualizarCopiaLosCampos() {
        when(repositorio.findById(1L)).thenReturn(Optional.of(instrumento()));
        when(repositorio.save(any(Instrumento.class))).thenAnswer(i -> i.getArgument(0));
        Instrumento datos = new Instrumento("Nuevo", null, null, null, 10L, 20L);
        Instrumento r = servicio.actualizar(1L, datos);
        assertEquals("Nuevo", r.nombre);
        assertEquals("", r.marca);
        assertEquals(20L, r.fechaVencimiento);
    }

    @Test
    void actualizarRechazaFechasInvalidas() {
        when(repositorio.findById(1L)).thenReturn(Optional.of(instrumento()));
        Instrumento datos = new Instrumento("Nuevo", "", "", "", 30L, 20L);
        assertThrows(ReglaInvalidaException.class, () -> servicio.actualizar(1L, datos));
    }

    @Test
    void eliminarBorraElInstrumento() {
        Instrumento i = instrumento();
        when(repositorio.findById(1L)).thenReturn(Optional.of(i));
        servicio.eliminar(1L);
        verify(repositorio).delete(i);
    }
}
