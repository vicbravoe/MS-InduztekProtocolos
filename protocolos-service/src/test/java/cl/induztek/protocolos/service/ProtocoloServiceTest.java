package cl.induztek.protocolos.service;

import cl.induztek.protocolos.dto.AmbienteRequest;
import cl.induztek.protocolos.dto.CambioEstadoRequest;
import cl.induztek.protocolos.dto.CierreVisitaRequest;
import cl.induztek.protocolos.dto.EvidenciaRequest;
import cl.induztek.protocolos.dto.NuevoProtocoloRequest;
import cl.induztek.protocolos.dto.ProtocoloDetalle;
import cl.induztek.protocolos.dto.ResultadoInicio;
import cl.induztek.protocolos.dto.VisitaResumen;
import cl.induztek.protocolos.error.ConflictoException;
import cl.induztek.protocolos.error.RecursoNoEncontradoException;
import cl.induztek.protocolos.error.ReglaInvalidaException;
import cl.induztek.protocolos.model.EstadoProtocolo;
import cl.induztek.protocolos.model.FotoProtocolo;
import cl.induztek.protocolos.model.HistorialEstado;
import cl.induztek.protocolos.model.Protocolo;
import cl.induztek.protocolos.model.Prueba;
import cl.induztek.protocolos.model.ResultadoPrueba;
import cl.induztek.protocolos.model.TipoPrueba;
import cl.induztek.protocolos.model.TipoVisita;
import cl.induztek.protocolos.model.Visita;
import cl.induztek.protocolos.repository.FotoProtocoloRepository;
import cl.induztek.protocolos.repository.HistorialEstadoRepository;
import cl.induztek.protocolos.repository.ProtocoloRepository;
import cl.induztek.protocolos.repository.PruebaRepository;
import cl.induztek.protocolos.repository.VisitaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProtocoloServiceTest {

    @Mock
    private ProtocoloRepository protocolos;

    @Mock
    private VisitaRepository visitas;

    @Mock
    private PruebaRepository pruebas;

    @Mock
    private HistorialEstadoRepository historial;

    @Mock
    private FotoProtocoloRepository fotos;

    @InjectMocks
    private ProtocoloService servicio;

    private Protocolo protocolo(EstadoProtocolo estado) {
        Protocolo p = new Protocolo();
        p.id = 5L;
        p.numero = "PT-20261120-1234";
        p.idVisita = 1L;
        p.idEquipo = 2L;
        p.idUsuarioRegistra = 3L;
        p.fechaHoraPrueba = 1000L;
        p.estado = estado;
        return p;
    }

    private Visita visita(Long cierre) {
        Visita v = new Visita();
        v.id = 1L;
        v.fechaHoraCierre = cierre;
        return v;
    }

    private NuevoProtocoloRequest peticion() {
        return new NuevoProtocoloRequest("PT-20261120-1234", 1L, 2L, 3L, 1000L);
    }

    @Test
    void listarPorVisitaYEquipoDevuelveElUnico() {
        when(protocolos.findByIdVisitaAndIdEquipo(1L, 2L)).thenReturn(Optional.of(protocolo(EstadoProtocolo.BORRADOR)));
        assertEquals(1, servicio.listar(1L, 2L, null).size());
    }

    @Test
    void listarPorVisitaYEquipoSinResultadoDevuelveVacio() {
        when(protocolos.findByIdVisitaAndIdEquipo(1L, 2L)).thenReturn(Optional.empty());
        assertTrue(servicio.listar(1L, 2L, null).isEmpty());
    }

    @Test
    void listarPorVisita() {
        when(protocolos.findByIdVisitaOrderByIdAsc(1L)).thenReturn(List.of(protocolo(EstadoProtocolo.BORRADOR)));
        assertEquals(1, servicio.listar(1L, null, null).size());
    }

    @Test
    void listarPorEquipo() {
        when(protocolos.findByIdEquipoOrderByFechaHoraPruebaDesc(2L)).thenReturn(List.of());
        assertEquals(0, servicio.listar(null, 2L, null).size());
    }

    @Test
    void listarPorEstado() {
        when(protocolos.findByEstadoOrderByFechaHoraPruebaAsc(EstadoProtocolo.REGISTRADO_TERRENO)).thenReturn(List.of(protocolo(EstadoProtocolo.REGISTRADO_TERRENO)));
        assertEquals(1, servicio.listar(null, null, EstadoProtocolo.REGISTRADO_TERRENO).size());
    }

    @Test
    void listarSinFiltrosDevuelveTodos() {
        when(protocolos.findAll()).thenReturn(List.of(protocolo(EstadoProtocolo.BORRADOR), protocolo(EstadoProtocolo.ANULADO)));
        assertEquals(2, servicio.listar(null, null, null).size());
    }

    @Test
    void obtenerLanzaNoEncontrado() {
        when(protocolos.findById(5L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.obtener(5L));
    }

    @Test
    void detalleIncluyePruebasEHistorial() {
        when(protocolos.findById(5L)).thenReturn(Optional.of(protocolo(EstadoProtocolo.BORRADOR)));
        when(pruebas.findByIdProtocoloOrderByIdAsc(5L)).thenReturn(List.of(new Prueba()));
        when(historial.findByIdProtocoloOrderByFechaHoraAsc(5L)).thenReturn(List.of());
        ProtocoloDetalle d = servicio.detalle(5L);
        assertEquals(1, d.pruebas().size());
        assertEquals(0, d.historial().size());
    }

    @Test
    void detalleDeEquipoArmaUnDetallePorProtocolo() {
        when(protocolos.findByIdEquipoOrderByFechaHoraPruebaDesc(2L)).thenReturn(List.of(protocolo(EstadoProtocolo.BORRADOR)));
        when(pruebas.findByIdProtocoloOrderByIdAsc(5L)).thenReturn(List.of());
        when(historial.findByIdProtocoloOrderByFechaHoraAsc(5L)).thenReturn(List.of());
        assertEquals(1, servicio.detalleDeEquipo(2L).size());
    }

    @Test
    void iniciarDevuelveElExistenteSinCrear() {
        Protocolo existente = protocolo(EstadoProtocolo.BORRADOR);
        when(protocolos.findByIdVisitaAndIdEquipo(1L, 2L)).thenReturn(Optional.of(existente));
        ResultadoInicio r = servicio.iniciar(peticion());
        assertFalse(r.creado());
        assertEquals(existente, r.protocolo());
        verify(protocolos, never()).save(any());
    }

    @Test
    void iniciarCreaElProtocoloEnBorrador() {
        when(protocolos.findByIdVisitaAndIdEquipo(1L, 2L)).thenReturn(Optional.empty());
        when(visitas.findById(1L)).thenReturn(Optional.of(visita(null)));
        when(protocolos.save(any(Protocolo.class))).thenAnswer(i -> i.getArgument(0));
        ResultadoInicio r = servicio.iniciar(peticion());
        assertTrue(r.creado());
        assertEquals(EstadoProtocolo.BORRADOR, r.protocolo().estado);
        assertEquals("PT-20261120-1234", r.protocolo().numero);
    }

    @Test
    void iniciarRechazaVisitaInexistente() {
        when(protocolos.findByIdVisitaAndIdEquipo(1L, 2L)).thenReturn(Optional.empty());
        when(visitas.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.iniciar(peticion()));
    }

    @Test
    void iniciarRechazaVisitaCerrada() {
        when(protocolos.findByIdVisitaAndIdEquipo(1L, 2L)).thenReturn(Optional.empty());
        when(visitas.findById(1L)).thenReturn(Optional.of(visita(99L)));
        assertThrows(ConflictoException.class, () -> servicio.iniciar(peticion()));
    }

    @Test
    void iniciarRechazaNumeroRepetido() {
        when(protocolos.findByIdVisitaAndIdEquipo(1L, 2L)).thenReturn(Optional.empty());
        when(visitas.findById(1L)).thenReturn(Optional.of(visita(null)));
        when(protocolos.existsByNumero("PT-20261120-1234")).thenReturn(true);
        assertThrows(ConflictoException.class, () -> servicio.iniciar(peticion()));
    }

    @Test
    void guardarAmbienteActualizaLosValores() {
        when(protocolos.findById(5L)).thenReturn(Optional.of(protocolo(EstadoProtocolo.BORRADOR)));
        when(protocolos.save(any(Protocolo.class))).thenAnswer(i -> i.getArgument(0));
        Protocolo r = servicio.guardarAmbiente(5L, new AmbienteRequest(21.5, 55.0, "  Limpieza  "));
        assertEquals(21.5, r.temperaturaC);
        assertEquals(55.0, r.humedadPct);
        assertEquals("Limpieza", r.descripcionMantenimiento);
    }

    @Test
    void guardarAmbienteConDescripcionVaciaLaDejaNula() {
        when(protocolos.findById(5L)).thenReturn(Optional.of(protocolo(EstadoProtocolo.OBSERVADO)));
        when(protocolos.save(any(Protocolo.class))).thenAnswer(i -> i.getArgument(0));
        assertNull(servicio.guardarAmbiente(5L, new AmbienteRequest(20.0, 50.0, "  ")).descripcionMantenimiento);
        assertNull(servicio.guardarAmbiente(5L, new AmbienteRequest(20.0, 50.0, null)).descripcionMantenimiento);
    }

    @Test
    void guardarAmbienteRechazaProtocoloFormalizado() {
        when(protocolos.findById(5L)).thenReturn(Optional.of(protocolo(EstadoProtocolo.FORMALIZADO)));
        assertThrows(ConflictoException.class, () -> servicio.guardarAmbiente(5L, new AmbienteRequest(20.0, 50.0, null)));
    }

    @Test
    void guardarAmbienteRechazaProtocoloAnulado() {
        when(protocolos.findById(5L)).thenReturn(Optional.of(protocolo(EstadoProtocolo.ANULADO)));
        assertThrows(ConflictoException.class, () -> servicio.guardarAmbiente(5L, new AmbienteRequest(20.0, 50.0, null)));
    }

    @Test
    void guardarEvidenciaGuardaFotoYUbicacion() {
        when(protocolos.findById(5L)).thenReturn(Optional.of(protocolo(EstadoProtocolo.BORRADOR)));
        when(protocolos.save(any(Protocolo.class))).thenAnswer(i -> i.getArgument(0));
        String base64 = Base64.getEncoder().encodeToString(new byte[]{1, 2, 3});
        Protocolo r = servicio.guardarEvidencia(5L, new EvidenciaRequest(base64, -33.4, -70.6));
        assertTrue(r.tieneFoto);
        assertEquals(-33.4, r.latitud);
        assertEquals(-70.6, r.longitud);
        ArgumentCaptor<FotoProtocolo> captor = ArgumentCaptor.forClass(FotoProtocolo.class);
        verify(fotos).save(captor.capture());
        assertEquals(3, captor.getValue().contenido.length);
    }

    @Test
    void guardarEvidenciaSoloUbicacionNoTocaLaFoto() {
        when(protocolos.findById(5L)).thenReturn(Optional.of(protocolo(EstadoProtocolo.BORRADOR)));
        when(protocolos.save(any(Protocolo.class))).thenAnswer(i -> i.getArgument(0));
        Protocolo r = servicio.guardarEvidencia(5L, new EvidenciaRequest(null, -33.4, -70.6));
        assertFalse(r.tieneFoto);
        verify(fotos, never()).save(any());
    }

    @Test
    void guardarEvidenciaSinDatosNoCambiaNada() {
        when(protocolos.findById(5L)).thenReturn(Optional.of(protocolo(EstadoProtocolo.BORRADOR)));
        when(protocolos.save(any(Protocolo.class))).thenAnswer(i -> i.getArgument(0));
        Protocolo r = servicio.guardarEvidencia(5L, new EvidenciaRequest("  ", null, null));
        assertNull(r.latitud);
        assertFalse(r.tieneFoto);
    }

    @Test
    void guardarEvidenciaRechazaBase64Invalido() {
        when(protocolos.findById(5L)).thenReturn(Optional.of(protocolo(EstadoProtocolo.BORRADOR)));
        assertThrows(ReglaInvalidaException.class, () -> servicio.guardarEvidencia(5L, new EvidenciaRequest("%%%no-es-base64%%%", null, null)));
    }

    @Test
    void obtenerFotoDevuelveBase64() {
        when(fotos.findById(5L)).thenReturn(Optional.of(new FotoProtocolo(5L, new byte[]{9, 8})));
        assertEquals(Base64.getEncoder().encodeToString(new byte[]{9, 8}), servicio.obtenerFoto(5L));
    }

    @Test
    void obtenerFotoSinFotoLanzaNoEncontrado() {
        when(fotos.findById(5L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.obtenerFoto(5L));
    }

    @Test
    void cambiarEstadoGuardaElHistorial() {
        when(protocolos.findById(5L)).thenReturn(Optional.of(protocolo(EstadoProtocolo.BORRADOR)));
        when(protocolos.save(any(Protocolo.class))).thenAnswer(i -> i.getArgument(0));
        Protocolo r = servicio.cambiarEstado(5L, new CambioEstadoRequest(EstadoProtocolo.REGISTRADO_TERRENO, 3L, "  listo  "));
        assertEquals(EstadoProtocolo.REGISTRADO_TERRENO, r.estado);
        ArgumentCaptor<HistorialEstado> captor = ArgumentCaptor.forClass(HistorialEstado.class);
        verify(historial).save(captor.capture());
        assertEquals(EstadoProtocolo.BORRADOR, captor.getValue().estadoAnterior);
        assertEquals("listo", captor.getValue().motivo);
        assertEquals(3L, captor.getValue().idUsuario);
    }

    @Test
    void cambiarEstadoSinMotivoGuardaNulo() {
        when(protocolos.findById(5L)).thenReturn(Optional.of(protocolo(EstadoProtocolo.REGISTRADO_TERRENO)));
        when(protocolos.save(any(Protocolo.class))).thenAnswer(i -> i.getArgument(0));
        servicio.cambiarEstado(5L, new CambioEstadoRequest(EstadoProtocolo.FORMALIZADO, 3L, " "));
        ArgumentCaptor<HistorialEstado> captor = ArgumentCaptor.forClass(HistorialEstado.class);
        verify(historial).save(captor.capture());
        assertNull(captor.getValue().motivo);
    }

    @Test
    void cambiarEstadoRechazaProtocoloFormalizado() {
        when(protocolos.findById(5L)).thenReturn(Optional.of(protocolo(EstadoProtocolo.FORMALIZADO)));
        assertThrows(ConflictoException.class, () -> servicio.cambiarEstado(5L, new CambioEstadoRequest(EstadoProtocolo.ANULADO, 3L, "x")));
        verify(historial, never()).save(any());
    }
}
