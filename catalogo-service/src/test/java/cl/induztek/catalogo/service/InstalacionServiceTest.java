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
class InstalacionServiceTest {

    @Mock
    private InstalacionRepository repositorio;

    @InjectMocks
    private InstalacionService servicio;

    private Instalacion instalacion() {
        return new Instalacion(7L, "Planta Lampa", "Lampa", "Inducción");
    }

    @Test
    void listaTodasOrdenadasPorNombre() {
        when(repositorio.findAll(any(Sort.class))).thenReturn(List.of(instalacion()));
        assertEquals(1, servicio.listar(null).size());
    }

    @Test
    void listaLasDeUnCliente() {
        when(repositorio.findByIdUsuarioClienteOrderByNombreAsc(7L)).thenReturn(List.of(instalacion(), instalacion()));
        assertEquals(2, servicio.listar(7L).size());
    }

    @Test
    void obtenerLanzaNoEncontrado() {
        when(repositorio.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.obtener(1L));
    }

    @Test
    void crearLimpiaElIdYGuarda() {
        when(repositorio.save(any(Instalacion.class))).thenAnswer(i -> i.getArgument(0));
        Instalacion i = instalacion();
        i.id = 99L;
        assertNull(servicio.crear(i).id);
    }

    @Test
    void actualizarCopiaLosCampos() {
        when(repositorio.findById(1L)).thenReturn(Optional.of(instalacion()));
        when(repositorio.save(any(Instalacion.class))).thenAnswer(i -> i.getArgument(0));
        Instalacion datos = new Instalacion(8L, "Otra", "La Florida", null);
        Instalacion r = servicio.actualizar(1L, datos);
        assertEquals("Otra", r.nombre);
        assertEquals(8L, r.idUsuarioCliente);
        assertEquals("", r.observacionesAcceso);
    }

    @Test
    void eliminarBorraLaInstalacion() {
        Instalacion i = instalacion();
        when(repositorio.findById(1L)).thenReturn(Optional.of(i));
        servicio.eliminar(1L);
        verify(repositorio).delete(i);
    }
}
