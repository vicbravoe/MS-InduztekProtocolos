package cl.induztek.usuarios.controller;

import cl.induztek.usuarios.error.ConflictoException;
import cl.induztek.usuarios.error.CredencialesInvalidasException;
import cl.induztek.usuarios.error.ManejadorErrores;
import cl.induztek.usuarios.error.RecursoNoEncontradoException;
import cl.induztek.usuarios.model.Rol;
import cl.induztek.usuarios.model.Usuario;
import cl.induztek.usuarios.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    private static final String CUERPO_VALIDO = "{\"rol\":\"TECNICO\",\"rut\":\"17.284.519-3\",\"nombre\":\"Matías Godoy\",\"correo\":\"mgodoy@induztek.ejemplo\"}";

    @Mock
    private UsuarioService servicio;

    private MockMvc mvc;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.standaloneSetup(new UsuarioController(servicio))
                .setControllerAdvice(new ManejadorErrores())
                .build();
    }

    private Usuario usuario() {
        Usuario u = new Usuario(Rol.TECNICO, "17.284.519-3", "Matías Godoy", "mgodoy@induztek.ejemplo");
        u.id = 1L;
        return u;
    }

    @Test
    void listarResponde200() throws Exception {
        when(servicio.listar(Rol.TECNICO, null)).thenReturn(List.of(usuario()));
        mvc.perform(get("/api/usuarios").param("rol", "TECNICO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Matías Godoy"));
    }

    @Test
    void obtenerResponde200() throws Exception {
        when(servicio.obtener(1L)).thenReturn(usuario());
        mvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("TECNICO"));
    }

    @Test
    void obtenerInexistenteResponde404() throws Exception {
        when(servicio.obtener(7L)).thenThrow(new RecursoNoEncontradoException("El usuario 7 no existe"));
        mvc.perform(get("/api/usuarios/7"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El usuario 7 no existe"));
    }

    @Test
    void crearResponde201() throws Exception {
        when(servicio.crear(any(Usuario.class))).thenReturn(usuario());
        mvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content(CUERPO_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void crearSinNombreResponde400() throws Exception {
        String cuerpo = "{\"rol\":\"TECNICO\",\"rut\":\"17.284.519-3\",\"nombre\":\"\",\"correo\":\"mgodoy@induztek.ejemplo\"}";
        mvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearConRolInexistenteResponde400() throws Exception {
        String cuerpo = "{\"rol\":\"GERENTE\",\"rut\":\"17.284.519-3\",\"nombre\":\"A\",\"correo\":\"a@b.cl\"}";
        mvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearDuplicadoResponde409() throws Exception {
        when(servicio.crear(any(Usuario.class))).thenThrow(new ConflictoException("Ya existe"));
        mvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content(CUERPO_VALIDO))
                .andExpect(status().isConflict());
    }

    @Test
    void actualizarResponde200() throws Exception {
        when(servicio.actualizar(eq(1L), any(Usuario.class))).thenReturn(usuario());
        mvc.perform(put("/api/usuarios/1").contentType(MediaType.APPLICATION_JSON).content(CUERPO_VALIDO))
                .andExpect(status().isOk());
    }

    @Test
    void desactivarResponde204() throws Exception {
        mvc.perform(delete("/api/usuarios/1")).andExpect(status().isNoContent());
        verify(servicio).desactivar(1L);
    }

    @Test
    void idNoNumericoResponde400() throws Exception {
        mvc.perform(get("/api/usuarios/abc")).andExpect(status().isBadRequest());
    }

    @Test
    void loginCorrectoResponde200SinExponerLaClave() throws Exception {
        Usuario u = usuario();
        u.clave = "hash:secreto";
        when(servicio.iniciarSesion("mgodoy@induztek.ejemplo", "Induztek2026")).thenReturn(u);
        mvc.perform(post("/api/usuarios/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"mgodoy@induztek.ejemplo\",\"clave\":\"Induztek2026\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Matías Godoy"))
                .andExpect(jsonPath("$.clave").doesNotExist());
    }

    @Test
    void loginIncorrectoResponde401() throws Exception {
        when(servicio.iniciarSesion(any(), any())).thenThrow(new CredencialesInvalidasException("Correo o contraseña incorrectos"));
        mvc.perform(post("/api/usuarios/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"mgodoy@induztek.ejemplo\",\"clave\":\"mala\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.estado").value(401))
                .andExpect(jsonPath("$.mensaje").value("Correo o contraseña incorrectos"));
    }

    @Test
    void loginSinDatosResponde400() throws Exception {
        mvc.perform(post("/api/usuarios/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"\",\"clave\":\"\"}"))
                .andExpect(status().isBadRequest());
        verify(servicio, never()).iniciarSesion(any(), any());
    }
}
