package cl.induztek.protocolos.controller;

import cl.induztek.protocolos.dto.ProtocoloDetalle;
import cl.induztek.protocolos.dto.ResultadoInicio;
import cl.induztek.protocolos.dto.VisitaResumen;
import cl.induztek.protocolos.error.ConflictoException;
import cl.induztek.protocolos.error.ManejadorErrores;
import cl.induztek.protocolos.error.RecursoNoEncontradoException;
import cl.induztek.protocolos.model.EstadoProtocolo;
import cl.induztek.protocolos.model.Protocolo;
import cl.induztek.protocolos.model.Prueba;
import cl.induztek.protocolos.model.ResultadoPrueba;
import cl.induztek.protocolos.model.TipoPrueba;
import cl.induztek.protocolos.model.Visita;
import cl.induztek.protocolos.service.ProtocoloService;
import cl.induztek.protocolos.service.PruebaService;
import cl.induztek.protocolos.service.VisitaService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VisitaControllerTest {

    private static final String VALIDA = "{\"numeroOt\":\"OT-1\",\"idInstalacion\":7,\"fecha\":1000,\"tipoVisita\":\"SOLO_PRUEBAS\",\"idIngenieroCargo\":4,\"participantes\":[1,2]}";

    @Mock
    private VisitaService servicio;

    private MockMvc mvc;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.standaloneSetup(new VisitaController(servicio)).setControllerAdvice(new ManejadorErrores()).build();
    }

    private Visita visita() {
        Visita v = new Visita();
        v.id = 1L;
        v.numeroOt = "OT-1";
        return v;
    }

    @Test
    void listarDevuelveResumenes() throws Exception {
        when(servicio.listar()).thenReturn(List.of(new VisitaResumen(visita(), 3, 1)));
        mvc.perform(get("/api/visitas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].totalProtocolos").value(3))
                .andExpect(jsonPath("$[0].visita.numeroOt").value("OT-1"));
    }

    @Test
    void obtenerExistente() throws Exception {
        when(servicio.obtener(1L)).thenReturn(visita());
        mvc.perform(get("/api/visitas/1")).andExpect(status().isOk());
    }

    @Test
    void obtenerInexistenteResponde404() throws Exception {
        when(servicio.obtener(2L)).thenThrow(new RecursoNoEncontradoException("no existe"));
        mvc.perform(get("/api/visitas/2")).andExpect(status().isNotFound());
    }

    @Test
    void crearResponde201() throws Exception {
        when(servicio.crear(any(Visita.class))).thenReturn(visita());
        mvc.perform(post("/api/visitas").contentType(MediaType.APPLICATION_JSON).content(VALIDA)).andExpect(status().isCreated());
    }

    @Test
    void crearSinNumeroOtResponde400() throws Exception {
        mvc.perform(post("/api/visitas").contentType(MediaType.APPLICATION_JSON).content("{\"idInstalacion\":7}")).andExpect(status().isBadRequest());
    }

    @Test
    void cerrarResponde200() throws Exception {
        when(servicio.cerrar(eq(1L), any())).thenReturn(visita());
        mvc.perform(put("/api/visitas/1/cierre").contentType(MediaType.APPLICATION_JSON)
                .content("{\"receptorNombre\":\"A\",\"receptorRut\":\"76482915-8\",\"receptorCargo\":\"B\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void cerrarSinDatosResponde400() throws Exception {
        mvc.perform(put("/api/visitas/1/cierre").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest());
    }

    @Test
    void cerrarVisitaCerradaResponde409() throws Exception {
        when(servicio.cerrar(eq(1L), any())).thenThrow(new ConflictoException("cerrada"));
        mvc.perform(put("/api/visitas/1/cierre").contentType(MediaType.APPLICATION_JSON)
                .content("{\"receptorNombre\":\"A\",\"receptorRut\":\"76482915-8\",\"receptorCargo\":\"B\"}"))
                .andExpect(status().isConflict());
    }
}
