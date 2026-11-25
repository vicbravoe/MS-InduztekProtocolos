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
class EquipoControllerTest {

    private static final String VALIDO = "{\"idInstalacion\":1,\"tag\":\"TR-01\",\"tipo\":\"TRANSFORMADOR\"}";

    @Mock
    private EquipoService servicio;

    private MockMvc mvc;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.standaloneSetup(new EquipoController(servicio)).setControllerAdvice(new ManejadorErrores()).build();
    }

    private Equipo equipo() {
        Equipo e = new Equipo(1L, "TR-01", TipoEquipo.TRANSFORMADOR, "Rhona", "RH-1", "12 kV", null);
        e.id = 4L;
        return e;
    }

    @Test
    void listarPorInstalacion() throws Exception {
        when(servicio.listar(1L)).thenReturn(List.of(equipo()));
        mvc.perform(get("/api/equipos").param("idInstalacion", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tag").value("TR-01"));
    }

    @Test
    void obtenerExistente() throws Exception {
        when(servicio.obtener(4L)).thenReturn(equipo());
        mvc.perform(get("/api/equipos/4")).andExpect(status().isOk()).andExpect(jsonPath("$.tipo").value("TRANSFORMADOR"));
    }

    @Test
    void obtenerInexistenteResponde404() throws Exception {
        when(servicio.obtener(9L)).thenThrow(new RecursoNoEncontradoException("no existe"));
        mvc.perform(get("/api/equipos/9")).andExpect(status().isNotFound());
    }

    @Test
    void crearResponde201() throws Exception {
        when(servicio.crear(any(Equipo.class))).thenReturn(equipo());
        mvc.perform(post("/api/equipos").contentType(MediaType.APPLICATION_JSON).content(VALIDO))
                .andExpect(status().isCreated());
    }

    @Test
    void crearSinTagResponde400() throws Exception {
        mvc.perform(post("/api/equipos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"idInstalacion\":1,\"tag\":\"\",\"tipo\":\"TRANSFORMADOR\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearTagRepetidoResponde409() throws Exception {
        when(servicio.crear(any(Equipo.class))).thenThrow(new ConflictoException("ya existe"));
        mvc.perform(post("/api/equipos").contentType(MediaType.APPLICATION_JSON).content(VALIDO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("ya existe"));
    }

    @Test
    void actualizarResponde200() throws Exception {
        when(servicio.actualizar(eq(4L), any(Equipo.class))).thenReturn(equipo());
        mvc.perform(put("/api/equipos/4").contentType(MediaType.APPLICATION_JSON).content(VALIDO))
                .andExpect(status().isOk());
    }

    @Test
    void eliminarResponde204() throws Exception {
        mvc.perform(delete("/api/equipos/4")).andExpect(status().isNoContent());
        verify(servicio).eliminar(4L);
    }
}
