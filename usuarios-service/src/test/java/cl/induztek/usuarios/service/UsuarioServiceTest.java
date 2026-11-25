package cl.induztek.usuarios.service;

import cl.induztek.usuarios.error.ConflictoException;
import cl.induztek.usuarios.error.CredencialesInvalidasException;
import cl.induztek.usuarios.error.RecursoNoEncontradoException;
import cl.induztek.usuarios.error.ReglaInvalidaException;
import cl.induztek.usuarios.model.Rol;
import cl.induztek.usuarios.model.Usuario;
import cl.induztek.usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repositorio;

    @InjectMocks
    private UsuarioService servicio;

    private Usuario nuevo() {
        return new Usuario(Rol.TECNICO, "172845193", "Matías Godoy", "mgodoy@induztek.ejemplo");
    }

    @Test
    void listaSoloActivosPorDefecto() {
        when(repositorio.findByActivoTrueOrderByRolAscNombreAsc()).thenReturn(List.of(nuevo()));
        assertEquals(1, servicio.listar(null, null).size());
    }

    @Test
    void listaActivosFiltrandoPorRol() {
        when(repositorio.findByRolAndActivoTrueOrderByNombreAsc(Rol.INGENIERO)).thenReturn(List.of());
        assertEquals(0, servicio.listar(Rol.INGENIERO, true).size());
    }

    @Test
    void listaTodosCuandoSePideIncluirInactivos() {
        when(repositorio.findAll(any(Sort.class))).thenReturn(List.of(nuevo(), nuevo()));
        assertEquals(2, servicio.listar(null, false).size());
    }

    @Test
    void listaInactivosFiltrandoPorRol() {
        when(repositorio.findByRolOrderByNombreAsc(Rol.CLIENTE)).thenReturn(List.of(nuevo()));
        assertEquals(1, servicio.listar(Rol.CLIENTE, false).size());
    }

    @Test
    void obtenerDevuelveElUsuario() {
        Usuario u = nuevo();
        when(repositorio.findById(1L)).thenReturn(Optional.of(u));
        assertEquals(u, servicio.obtener(1L));
    }

    @Test
    void obtenerLanzaNoEncontrado() {
        when(repositorio.findById(9L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.obtener(9L));
    }

    @Test
    void crearFormateaElRutYGuarda() {
        when(repositorio.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));
        Usuario u = nuevo();
        u.id = 55L;
        Usuario guardado = servicio.crear(u);
        assertEquals("17.284.519-3", guardado.rut);
        assertNull(guardado.id);
    }

    @Test
    void crearRechazaRutInvalido() {
        Usuario u = nuevo();
        u.rut = "17.284.519-9";
        assertThrows(ReglaInvalidaException.class, () -> servicio.crear(u));
        verify(repositorio, never()).save(any());
    }

    @Test
    void crearRechazaRutRepetido() {
        when(repositorio.existsByRut("17.284.519-3")).thenReturn(true);
        assertThrows(ConflictoException.class, () -> servicio.crear(nuevo()));
    }

    @Test
    void crearRechazaCorreoRepetido() {
        when(repositorio.existsByCorreo("mgodoy@induztek.ejemplo")).thenReturn(true);
        assertThrows(ConflictoException.class, () -> servicio.crear(nuevo()));
    }

    @Test
    void actualizarCopiaLosDatos() {
        Usuario actual = nuevo();
        actual.id = 3L;
        when(repositorio.findById(3L)).thenReturn(Optional.of(actual));
        when(repositorio.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));
        Usuario datos = new Usuario(Rol.INGENIERO, "17.284.519-3", "Nuevo Nombre", "otro@induztek.ejemplo");
        datos.telefono = null;
        datos.giro = "Minería";
        Usuario resultado = servicio.actualizar(3L, datos);
        assertEquals("Nuevo Nombre", resultado.nombre);
        assertEquals(Rol.INGENIERO, resultado.rol);
        assertEquals("", resultado.telefono);
        assertEquals("Minería", resultado.giro);
    }

    @Test
    void actualizarRechazaRutDeOtroUsuario() {
        when(repositorio.findById(3L)).thenReturn(Optional.of(nuevo()));
        when(repositorio.existsByRutAndIdNot("17.284.519-3", 3L)).thenReturn(true);
        assertThrows(ConflictoException.class, () -> servicio.actualizar(3L, nuevo()));
    }

    @Test
    void actualizarRechazaCorreoDeOtroUsuario() {
        when(repositorio.findById(3L)).thenReturn(Optional.of(nuevo()));
        when(repositorio.existsByCorreoAndIdNot("mgodoy@induztek.ejemplo", 3L)).thenReturn(true);
        assertThrows(ConflictoException.class, () -> servicio.actualizar(3L, nuevo()));
    }

    @Test
    void actualizarRechazaRutInvalido() {
        when(repositorio.findById(3L)).thenReturn(Optional.of(nuevo()));
        Usuario datos = nuevo();
        datos.rut = "1-1";
        assertThrows(ReglaInvalidaException.class, () -> servicio.actualizar(3L, datos));
    }

    @Test
    void desactivarMarcaComoInactivo() {
        Usuario u = nuevo();
        when(repositorio.findById(2L)).thenReturn(Optional.of(u));
        servicio.desactivar(2L);
        assertFalse(u.activo);
        verify(repositorio).save(u);
    }

    private Usuario conClave(String clave) {
        Usuario u = nuevo();
        u.clave = ClaveUtil.hashear(clave);
        return u;
    }

    @Test
    void iniciaSesionConCredencialesCorrectas() {
        when(repositorio.findByCorreo("mgodoy@induztek.ejemplo")).thenReturn(Optional.of(conClave("Induztek2026")));
        assertEquals("Matías Godoy", servicio.iniciarSesion(" mgodoy@induztek.ejemplo ", "Induztek2026").nombre);
    }

    @Test
    void rechazaClaveIncorrecta() {
        when(repositorio.findByCorreo("mgodoy@induztek.ejemplo")).thenReturn(Optional.of(conClave("Induztek2026")));
        assertThrows(CredencialesInvalidasException.class, () -> servicio.iniciarSesion("mgodoy@induztek.ejemplo", "otra"));
    }

    @Test
    void rechazaCorreoInexistente() {
        when(repositorio.findByCorreo("nadie@induztek.ejemplo")).thenReturn(Optional.empty());
        assertThrows(CredencialesInvalidasException.class, () -> servicio.iniciarSesion("nadie@induztek.ejemplo", "Induztek2026"));
    }

    @Test
    void rechazaUsuarioInactivo() {
        Usuario u = conClave("Induztek2026");
        u.activo = false;
        when(repositorio.findByCorreo("mgodoy@induztek.ejemplo")).thenReturn(Optional.of(u));
        assertThrows(CredencialesInvalidasException.class, () -> servicio.iniciarSesion("mgodoy@induztek.ejemplo", "Induztek2026"));
    }

    @Test
    void rechazaUsuarioSinClaveAsignada() {
        when(repositorio.findByCorreo("mgodoy@induztek.ejemplo")).thenReturn(Optional.of(nuevo()));
        assertThrows(CredencialesInvalidasException.class, () -> servicio.iniciarSesion("mgodoy@induztek.ejemplo", "Induztek2026"));
    }

    @Test
    void alCrearGuardaLaClaveCifrada() {
        Usuario u = nuevo();
        u.clave = "Induztek2026";
        when(repositorio.existsByRut(any())).thenReturn(false);
        when(repositorio.existsByCorreo(any())).thenReturn(false);
        when(repositorio.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));
        Usuario guardado = servicio.crear(u);
        assertNotNull(guardado.clave);
        assertTrue(ClaveUtil.coincide("Induztek2026", guardado.clave));
    }

    @Test
    void alCrearRechazaClaveMuyCorta() {
        Usuario u = nuevo();
        u.clave = "123";
        when(repositorio.existsByRut(any())).thenReturn(false);
        when(repositorio.existsByCorreo(any())).thenReturn(false);
        assertThrows(ReglaInvalidaException.class, () -> servicio.crear(u));
    }

    @Test
    void asignaClaveInicialSoloAQuienNoLaTiene() {
        Usuario u = nuevo();
        when(repositorio.findByClaveIsNull()).thenReturn(List.of(u));
        servicio.asignarClavesFaltantes("Induztek2026");
        assertTrue(ClaveUtil.coincide("Induztek2026", u.clave));
        verify(repositorio).save(u);
    }
}
