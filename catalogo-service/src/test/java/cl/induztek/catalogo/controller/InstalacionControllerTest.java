package cl.induztek.catalogo.controller;

import cl.induztek.catalogo.error.ConflictoException;
import cl.induztek.catalogo.error.ManejadorErrores;
import cl.induztek.catalogo.error.RecursoNoEncontradoException;
import cl.induztek.catalogo.model.Equipo;
import cl.induztek.catalogo.model.Instalacion;
import cl.induztek.catalogo.model.Instrumento;
import cl.induztek.catalogo.model.TipoEquipo;
import cl.induztek.catalogo.service.EquipoService;
import cl.induztek.catalogo.service.InstalacionService;
import cl.induztek.catalogo.service.InstrumentoService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class InstalacionControllerTest {

    private static final String VALIDO = "{\"idUsuarioCliente\":7,\"nombre\":\"Planta Lampa\",\"comuna\":\"Lampa\"}";

    @Mock
    private InstalacionService servicio;

    private MockMvc mvc;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.standaloneSetup(new InstalacionController(servicio)).setControllerAdvice(new ManejadorErrores()).build();
    }

    private Instalacion instalacion() {
        Instalacion i = new Instalacion(7L, "Planta Lampa", "Lampa", "");
        i.id = 1L;
        return i;
    }

    @Test
    void listarDeUnCliente() throws Exception {
        when(servicio.listar(7L)).thenReturn(List.of(instalacion()));
        mvc.perform(get("/api/instalaciones").param("idCliente", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comuna").value("Lampa"));
    }

    @Test
    void obtenerExistente() throws Exception {
        when(servicio.obtener(1L)).thenReturn(instalacion());
        mvc.perform(get("/api/instalaciones/1")).andExpect(status().isOk());
    }

    @Test
    void crearResponde201() throws Exception {
        when(servicio.crear(any(Instalacion.class))).thenReturn(instalacion());
        mvc.perform(post("/api/instalaciones").contentType(MediaType.APPLICATION_JSON).content(VALIDO))
                .andExpect(status().isCreated());
    }

    @Test
    void crearSinComunaResponde400() throws Exception {
        mvc.perform(post("/api/instalaciones").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"X\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizarResponde200() throws Exception {
        when(servicio.actualizar(eq(1L), any(Instalacion.class))).thenReturn(instalacion());
        mvc.perform(put("/api/instalaciones/1").contentType(MediaType.APPLICATION_JSON).content(VALIDO))
                .andExpect(status().isOk());
    }

    @Test
    void eliminarResponde204() throws Exception {
        mvc.perform(delete("/api/instalaciones/1")).andExpect(status().isNoContent());
        verify(servicio).eliminar(1L);
    }
}
