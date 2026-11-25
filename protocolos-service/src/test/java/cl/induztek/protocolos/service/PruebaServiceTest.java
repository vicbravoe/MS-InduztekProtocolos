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
class PruebaServiceTest {

    @Mock
    private PruebaRepository pruebas;

    @Mock
    private ProtocoloRepository protocolos;

    @InjectMocks
    private PruebaService servicio;

    private Protocolo protocolo(EstadoProtocolo estado) {
        Protocolo p = new Protocolo();
        p.id = 5L;
        p.estado = estado;
        return p;
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
    void registrarAsociaLaPruebaAlProtocolo() {
        when(protocolos.findById(5L)).thenReturn(Optional.of(protocolo(EstadoProtocolo.BORRADOR)));
        when(pruebas.save(any(Prueba.class))).thenAnswer(i -> i.getArgument(0));
        Prueba p = prueba();
        p.idProtocolo = 99L;
        Prueba r = servicio.registrar(5L, p);
        assertNull(r.id);
        assertEquals(5L, r.idProtocolo);
    }

    @Test
    void registrarRechazaProtocoloInexistente() {
        when(protocolos.findById(5L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.registrar(5L, prueba()));
    }

    @Test
    void registrarRechazaProtocoloFormalizado() {
        when(protocolos.findById(5L)).thenReturn(Optional.of(protocolo(EstadoProtocolo.FORMALIZADO)));
        assertThrows(ConflictoException.class, () -> servicio.registrar(5L, prueba()));
        verify(pruebas, never()).save(any());
    }

    @Test
    void fijarResultadoActualizaLaPrueba() {
        when(pruebas.findById(77L)).thenReturn(Optional.of(prueba()));
        when(protocolos.findById(5L)).thenReturn(Optional.of(protocolo(EstadoProtocolo.REGISTRADO_TERRENO)));
        when(pruebas.save(any(Prueba.class))).thenAnswer(i -> i.getArgument(0));
        assertEquals(ResultadoPrueba.APROBADO, servicio.fijarResultado(77L, ResultadoPrueba.APROBADO).resultado);
    }

    @Test
    void fijarResultadoRechazaPruebaInexistente() {
        when(pruebas.findById(77L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.fijarResultado(77L, ResultadoPrueba.APROBADO));
    }

    @Test
    void fijarResultadoRechazaProtocoloAnulado() {
        when(pruebas.findById(77L)).thenReturn(Optional.of(prueba()));
        when(protocolos.findById(5L)).thenReturn(Optional.of(protocolo(EstadoProtocolo.ANULADO)));
        assertThrows(ConflictoException.class, () -> servicio.fijarResultado(77L, ResultadoPrueba.RECHAZADO));
    }
}
