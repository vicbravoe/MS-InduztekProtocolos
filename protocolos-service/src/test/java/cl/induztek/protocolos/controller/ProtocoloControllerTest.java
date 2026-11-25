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
class ProtocoloControllerTest {

    @Mock
    private ProtocoloService servicio;

    private MockMvc mvc;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.standaloneSetup(new ProtocoloController(servicio)).setControllerAdvice(new ManejadorErrores()).build();
    }

    private Protocolo protocolo() {
        Protocolo p = new Protocolo();
        p.id = 5L;
        p.numero = "PT-1";
        p.estado = EstadoProtocolo.BORRADOR;
        return p;
    }

    @Test
    void listarConFiltros() throws Exception {
        when(servicio.listar(1L, null, EstadoProtocolo.BORRADOR)).thenReturn(List.of(protocolo()));
        mvc.perform(get("/api/protocolos").param("idVisita", "1").param("estado", "BORRADOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numero").value("PT-1"))
                .andExpect(jsonPath("$[0].tieneFoto").value(false));
    }

    @Test
    void listarConEstadoInvalidoResponde400() throws Exception {
        mvc.perform(get("/api/protocolos").param("estado", "INEXISTENTE")).andExpect(status().isBadRequest());
    }

    @Test
    void obtenerExistente() throws Exception {
        when(servicio.obtener(5L)).thenReturn(protocolo());
        mvc.perform(get("/api/protocolos/5")).andExpect(status().isOk());
    }

    @Test
    void detalleResponde200() throws Exception {
        when(servicio.detalle(5L)).thenReturn(new ProtocoloDetalle(protocolo(), List.of(), List.of()));
        mvc.perform(get("/api/protocolos/5/detalle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.protocolo.id").value(5));
    }

    @Test
    void detalleDeEquipoResponde200() throws Exception {
        when(servicio.detalleDeEquipo(2L)).thenReturn(List.of(new ProtocoloDetalle(protocolo(), List.of(), List.of())));
        mvc.perform(get("/api/protocolos/equipo/2/detalle")).andExpect(status().isOk()).andExpect(jsonPath("$[0].protocolo.id").value(5));
    }

    @Test
    void iniciarNuevoResponde201() throws Exception {
        when(servicio.iniciar(any())).thenReturn(new ResultadoInicio(protocolo(), true));
        mvc.perform(post("/api/protocolos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"numero\":\"PT-1\",\"idVisita\":1,\"idEquipo\":2,\"idUsuarioRegistra\":3,\"fechaHoraPrueba\":1000}"))
                .andExpect(status().isCreated());
    }

    @Test
    void iniciarExistenteResponde200() throws Exception {
        when(servicio.iniciar(any())).thenReturn(new ResultadoInicio(protocolo(), false));
        mvc.perform(post("/api/protocolos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"numero\":\"PT-1\",\"idVisita\":1,\"idEquipo\":2,\"idUsuarioRegistra\":3,\"fechaHoraPrueba\":1000}"))
                .andExpect(status().isOk());
    }

    @Test
    void iniciarSinDatosResponde400() throws Exception {
        mvc.perform(post("/api/protocolos").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest());
    }

    @Test
    void ambienteValidoResponde200() throws Exception {
        when(servicio.guardarAmbiente(eq(5L), any())).thenReturn(protocolo());
        mvc.perform(put("/api/protocolos/5/ambiente").contentType(MediaType.APPLICATION_JSON)
                .content("{\"temperaturaC\":21.5,\"humedadPct\":55}")).andExpect(status().isOk());
    }

    @Test
    void ambienteFueraDeRangoResponde400() throws Exception {
        mvc.perform(put("/api/protocolos/5/ambiente").contentType(MediaType.APPLICATION_JSON)
                .content("{\"temperaturaC\":90,\"humedadPct\":55}")).andExpect(status().isBadRequest());
    }

    @Test
    void evidenciaResponde200() throws Exception {
        when(servicio.guardarEvidencia(eq(5L), any())).thenReturn(protocolo());
        mvc.perform(put("/api/protocolos/5/evidencia").contentType(MediaType.APPLICATION_JSON)
                .content("{\"latitud\":-33.4,\"longitud\":-70.6}")).andExpect(status().isOk());
    }

    @Test
    void evidenciaConLatitudInvalidaResponde400() throws Exception {
        mvc.perform(put("/api/protocolos/5/evidencia").contentType(MediaType.APPLICATION_JSON)
                .content("{\"latitud\":120,\"longitud\":-70.6}")).andExpect(status().isBadRequest());
    }

    @Test
    void fotoResponde200() throws Exception {
        when(servicio.obtenerFoto(5L)).thenReturn("AQID");
        mvc.perform(get("/api/protocolos/5/foto")).andExpect(status().isOk()).andExpect(jsonPath("$.fotoBase64").value("AQID"));
    }

    @Test
    void fotoInexistenteResponde404() throws Exception {
        when(servicio.obtenerFoto(5L)).thenThrow(new RecursoNoEncontradoException("sin foto"));
        mvc.perform(get("/api/protocolos/5/foto")).andExpect(status().isNotFound());
    }

    @Test
    void cambiarEstadoResponde200() throws Exception {
        when(servicio.cambiarEstado(eq(5L), any())).thenReturn(protocolo());
        mvc.perform(put("/api/protocolos/5/estado").contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\":\"REGISTRADO_TERRENO\",\"idUsuario\":3}")).andExpect(status().isOk());
    }

    @Test
    void cambiarEstadoSinEstadoResponde400() throws Exception {
        mvc.perform(put("/api/protocolos/5/estado").contentType(MediaType.APPLICATION_JSON).content("{\"idUsuario\":3}")).andExpect(status().isBadRequest());
    }

    @Test
    void cambiarEstadoDeFormalizadoResponde409() throws Exception {
        when(servicio.cambiarEstado(eq(5L), any())).thenThrow(new ConflictoException("formalizado"));
        mvc.perform(put("/api/protocolos/5/estado").contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\":\"ANULADO\",\"idUsuario\":3}")).andExpect(status().isConflict());
    }
}
