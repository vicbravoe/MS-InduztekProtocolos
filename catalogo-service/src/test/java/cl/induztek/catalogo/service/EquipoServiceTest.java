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
class EquipoServiceTest {

    @Mock
    private EquipoRepository repositorio;

    @Mock
    private InstalacionRepository instalaciones;

    @InjectMocks
    private EquipoService servicio;

    private Equipo equipo() {
        return new Equipo(1L, " TR-01 ", TipoEquipo.TRANSFORMADOR, "Rhona", "RH-1", "12 kV", null);
    }

    @Test
    void listaTodosSinFiltro() {
        when(repositorio.findAll()).thenReturn(List.of(equipo()));
        assertEquals(1, servicio.listar(null).size());
    }

    @Test
    void listaLosDeUnaInstalacion() {
        when(repositorio.findByIdInstalacionOrderByTagAsc(1L)).thenReturn(List.of(equipo(), equipo()));
        assertEquals(2, servicio.listar(1L).size());
    }

    @Test
    void obtenerLanzaNoEncontrado() {
        when(repositorio.findById(5L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.obtener(5L));
    }

    @Test
    void crearQuitaEspaciosDelTagYGuarda() {
        when(instalaciones.existsById(1L)).thenReturn(true);
        when(repositorio.save(any(Equipo.class))).thenAnswer(i -> i.getArgument(0));
        Equipo e = equipo();
        e.id = 40L;
        Equipo r = servicio.crear(e);
        assertEquals("TR-01", r.tag);
        assertNull(r.id);
    }

    @Test
    void crearRechazaTagRepetidoEnLaInstalacion() {
        when(instalaciones.existsById(1L)).thenReturn(true);
        when(repositorio.existsByIdInstalacionAndTag(1L, "TR-01")).thenReturn(true);
        assertThrows(ConflictoException.class, () -> servicio.crear(equipo()));
        verify(repositorio, never()).save(any());
    }

    @Test
    void crearRechazaInstalacionInexistente() {
        when(instalaciones.existsById(1L)).thenReturn(false);
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.crear(equipo()));
    }

    @Test
    void actualizarCopiaLosCampos() {
        Equipo actual = equipo();
        when(repositorio.findById(2L)).thenReturn(Optional.of(actual));
        when(instalaciones.existsById(1L)).thenReturn(true);
        when(repositorio.save(any(Equipo.class))).thenAnswer(i -> i.getArgument(0));
        Equipo datos = new Equipo(1L, "TR-09", TipoEquipo.CELDA_MT, null, null, null, 60.0);
        Equipo r = servicio.actualizar(2L, datos);
        assertEquals("TR-09", r.tag);
        assertEquals(TipoEquipo.CELDA_MT, r.tipo);
        assertEquals("", r.marca);
        assertEquals(60.0, r.maxContactosUohm);
    }

    @Test
    void actualizarRechazaTagRepetido() {
        when(repositorio.findById(2L)).thenReturn(Optional.of(equipo()));
        when(instalaciones.existsById(1L)).thenReturn(true);
        when(repositorio.existsByIdInstalacionAndTagAndIdNot(1L, "TR-01", 2L)).thenReturn(true);
        assertThrows(ConflictoException.class, () -> servicio.actualizar(2L, equipo()));
    }

    @Test
    void eliminarBorraElEquipo() {
        Equipo e = equipo();
        when(repositorio.findById(2L)).thenReturn(Optional.of(e));
        servicio.eliminar(2L);
        verify(repositorio).delete(e);
    }
}
