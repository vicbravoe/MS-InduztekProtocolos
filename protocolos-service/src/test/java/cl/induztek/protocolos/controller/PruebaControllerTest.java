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
class PruebaControllerTest {

    @Mock
    private PruebaService servicio;

    private MockMvc mvc;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.standaloneSetup(new PruebaController(servicio)).setControllerAdvice(new ManejadorErrores()).build();
    }

    private Prueba prueba() {
        Prueba p = new Prueba();
        p.id = 77L;
        p.idProtocolo = 5L;
        p.tipoPrueba = TipoPrueba.AISLACION;
        p.elemento = "Fase A";
        return p;
    }

    @Test
    void registrarResponde201() throws Exception {
        when(servicio.registrar(eq(5L), any(Prueba.class))).thenReturn(prueba());
        mvc.perform(post("/api/protocolos/5/pruebas").contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipoPrueba\":\"AISLACION\",\"elemento\":\"Fase A\",\"mediciones\":[{\"parametro\":\"LECTURA_1MIN\",\"valor\":500,\"unidad\":\"MΩ\"}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(77));
    }

    @Test
    void registrarSinElementoResponde400() throws Exception {
        mvc.perform(post("/api/protocolos/5/pruebas").contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipoPrueba\":\"AISLACION\",\"elemento\":\"\"}")).andExpect(status().isBadRequest());
    }

    @Test
    void registrarConMedicionIncompletaResponde400() throws Exception {
        mvc.perform(post("/api/protocolos/5/pruebas").contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipoPrueba\":\"AISLACION\",\"elemento\":\"A\",\"mediciones\":[{\"parametro\":\"\"}]}")).andExpect(status().isBadRequest());
    }

    @Test
    void registrarEnProtocoloCerradoResponde409() throws Exception {
        when(servicio.registrar(eq(5L), any(Prueba.class))).thenThrow(new ConflictoException("cerrado"));
        mvc.perform(post("/api/protocolos/5/pruebas").contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipoPrueba\":\"AISLACION\",\"elemento\":\"Fase A\"}")).andExpect(status().isConflict());
    }

    @Test
    void fijarResultadoResponde200() throws Exception {
        Prueba p = prueba();
        p.resultado = ResultadoPrueba.OBSERVADO;
        when(servicio.fijarResultado(77L, ResultadoPrueba.OBSERVADO)).thenReturn(p);
        mvc.perform(put("/api/pruebas/77/resultado").contentType(MediaType.APPLICATION_JSON).content("{\"resultado\":\"OBSERVADO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultado").value("OBSERVADO"));
    }

    @Test
    void fijarResultadoSinValorResponde400() throws Exception {
        mvc.perform(put("/api/pruebas/77/resultado").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest());
    }
}
