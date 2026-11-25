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
class VisitaServiceTest {

    @Mock
    private VisitaRepository visitas;

    @Mock
    private ProtocoloRepository protocolos;

    @InjectMocks
    private VisitaService servicio;

    private Visita visita() {
        Visita v = new Visita();
        v.id = 1L;
        v.numeroOt = "OT-20261120-100";
        v.idInstalacion = 7L;
        v.fecha = 1000L;
        v.tipoVisita = TipoVisita.SOLO_PRUEBAS;
        v.idIngenieroCargo = 4L;
        return v;
    }

    @Test
    void listaVisitasConSusConteos() {
        when(visitas.findAll(any(Sort.class))).thenReturn(List.of(visita()));
        when(protocolos.countByIdVisita(1L)).thenReturn(3L);
        when(protocolos.countByIdVisitaAndEstado(1L, EstadoProtocolo.FORMALIZADO)).thenReturn(2L);
        List<VisitaResumen> lista = servicio.listar();
        assertEquals(1, lista.size());
        assertEquals(3L, lista.get(0).totalProtocolos());
        assertEquals(2L, lista.get(0).formalizados());
    }

    @Test
    void obtenerLanzaNoEncontrado() {
        when(visitas.findById(9L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.obtener(9L));
    }

    @Test
    void crearLimpiaCamposDeCierreYGuarda() {
        when(visitas.save(any(Visita.class))).thenAnswer(i -> i.getArgument(0));
        Visita v = visita();
        v.fechaHoraCierre = 5L;
        v.receptorNombre = "X";
        Visita r = servicio.crear(v);
        assertNull(r.id);
        assertNull(r.fechaHoraCierre);
        assertNull(r.receptorNombre);
    }

    @Test
    void crearRechazaNumeroOtRepetido() {
        when(visitas.existsByNumeroOt("OT-20261120-100")).thenReturn(true);
        assertThrows(ConflictoException.class, () -> servicio.crear(visita()));
        verify(visitas, never()).save(any());
    }

    @Test
    void cerrarRegistraAQuienRecibe() {
        when(visitas.findById(1L)).thenReturn(Optional.of(visita()));
        when(visitas.save(any(Visita.class))).thenAnswer(i -> i.getArgument(0));
        Visita r = servicio.cerrar(1L, new CierreVisitaRequest(" Marcela Fuentes ", "76482915-8", " Jefa "));
        assertNotNull(r.fechaHoraCierre);
        assertEquals("Marcela Fuentes", r.receptorNombre);
        assertEquals("76.482.915-8", r.receptorRut);
        assertEquals("Jefa", r.receptorCargo);
    }

    @Test
    void cerrarRechazaRutInvalido() {
        when(visitas.findById(1L)).thenReturn(Optional.of(visita()));
        assertThrows(ReglaInvalidaException.class, () -> servicio.cerrar(1L, new CierreVisitaRequest("A", "76482915-1", "B")));
    }

    @Test
    void cerrarRechazaVisitaYaCerrada() {
        Visita v = visita();
        v.fechaHoraCierre = 10L;
        when(visitas.findById(1L)).thenReturn(Optional.of(v));
        assertThrows(ConflictoException.class, () -> servicio.cerrar(1L, new CierreVisitaRequest("A", "76482915-8", "B")));
    }
}
