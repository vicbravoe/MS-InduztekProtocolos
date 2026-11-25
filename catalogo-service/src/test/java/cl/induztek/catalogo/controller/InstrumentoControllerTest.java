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
class InstrumentoControllerTest {

    private static final String VALIDO = "{\"nombre\":\"Megóhmetro\",\"fechaUltimaCalibracion\":1000,\"fechaVencimiento\":2000}";

    @Mock
    private InstrumentoService servicio;

    private MockMvc mvc;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.standaloneSetup(new InstrumentoController(servicio)).setControllerAdvice(new ManejadorErrores()).build();
    }

    private Instrumento instrumento() {
        Instrumento i = new Instrumento("Megóhmetro", "Megger", "MIT525", "S-1", 1000L, 2000L);
        i.id = 2L;
        return i;
    }

    @Test
    void listarResponde200() throws Exception {
        when(servicio.listar()).thenReturn(List.of(instrumento()));
        mvc.perform(get("/api/instrumentos")).andExpect(status().isOk()).andExpect(jsonPath("$[0].nombre").value("Megóhmetro"));
    }

    @Test
    void obtenerResponde200() throws Exception {
        when(servicio.obtener(2L)).thenReturn(instrumento());
        mvc.perform(get("/api/instrumentos/2")).andExpect(status().isOk());
    }

    @Test
    void crearResponde201() throws Exception {
        when(servicio.crear(any(Instrumento.class))).thenReturn(instrumento());
        mvc.perform(post("/api/instrumentos").contentType(MediaType.APPLICATION_JSON).content(VALIDO))
                .andExpect(status().isCreated());
    }

    @Test
    void crearSinFechasResponde400() throws Exception {
        mvc.perform(post("/api/instrumentos").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"X\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizarResponde200() throws Exception {
        when(servicio.actualizar(eq(2L), any(Instrumento.class))).thenReturn(instrumento());
        mvc.perform(put("/api/instrumentos/2").contentType(MediaType.APPLICATION_JSON).content(VALIDO))
                .andExpect(status().isOk());
    }

    @Test
    void eliminarResponde204() throws Exception {
        mvc.perform(delete("/api/instrumentos/2")).andExpect(status().isNoContent());
        verify(servicio).eliminar(2L);
    }
}
