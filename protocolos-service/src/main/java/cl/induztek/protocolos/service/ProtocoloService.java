package cl.induztek.protocolos.service;

import cl.induztek.protocolos.dto.AmbienteRequest;
import cl.induztek.protocolos.dto.CambioEstadoRequest;
import cl.induztek.protocolos.dto.EvidenciaRequest;
import cl.induztek.protocolos.dto.NuevoProtocoloRequest;
import cl.induztek.protocolos.dto.ProtocoloDetalle;
import cl.induztek.protocolos.dto.ResultadoInicio;
import cl.induztek.protocolos.error.ConflictoException;
import cl.induztek.protocolos.error.RecursoNoEncontradoException;
import cl.induztek.protocolos.error.ReglaInvalidaException;
import cl.induztek.protocolos.model.EstadoProtocolo;
import cl.induztek.protocolos.model.FotoProtocolo;
import cl.induztek.protocolos.model.HistorialEstado;
import cl.induztek.protocolos.model.Protocolo;
import cl.induztek.protocolos.model.Visita;
import cl.induztek.protocolos.repository.FotoProtocoloRepository;
import cl.induztek.protocolos.repository.HistorialEstadoRepository;
import cl.induztek.protocolos.repository.ProtocoloRepository;
import cl.induztek.protocolos.repository.PruebaRepository;
import cl.induztek.protocolos.repository.VisitaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class ProtocoloService {

    private final ProtocoloRepository protocolos;
    private final VisitaRepository visitas;
    private final PruebaRepository pruebas;
    private final HistorialEstadoRepository historial;
    private final FotoProtocoloRepository fotos;

    public ProtocoloService(ProtocoloRepository protocolos, VisitaRepository visitas, PruebaRepository pruebas,
                            HistorialEstadoRepository historial, FotoProtocoloRepository fotos) {
        this.protocolos = protocolos;
        this.visitas = visitas;
        this.pruebas = pruebas;
        this.historial = historial;
        this.fotos = fotos;
    }

    public List<Protocolo> listar(Long idVisita, Long idEquipo, EstadoProtocolo estado) {
        if (idVisita != null && idEquipo != null) {
            return protocolos.findByIdVisitaAndIdEquipo(idVisita, idEquipo).map(p -> List.of(p)).orElse(List.of());
        }
        if (idVisita != null) {
            return protocolos.findByIdVisitaOrderByIdAsc(idVisita);
        }
        if (idEquipo != null) {
            return protocolos.findByIdEquipoOrderByFechaHoraPruebaDesc(idEquipo);
        }
        if (estado != null) {
            return protocolos.findByEstadoOrderByFechaHoraPruebaAsc(estado);
        }
        return protocolos.findAll();
    }

    public Protocolo obtener(Long id) {
        return protocolos.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("El protocolo " + id + " no existe"));
    }

    public ProtocoloDetalle detalle(Long id) {
        return armarDetalle(obtener(id));
    }

    public List<ProtocoloDetalle> detalleDeEquipo(Long idEquipo) {
        return protocolos.findByIdEquipoOrderByFechaHoraPruebaDesc(idEquipo).stream().map(this::armarDetalle).toList();
    }

    private ProtocoloDetalle armarDetalle(Protocolo p) {
        return new ProtocoloDetalle(p, pruebas.findByIdProtocoloOrderByIdAsc(p.id), historial.findByIdProtocoloOrderByFechaHoraAsc(p.id));
    }

    @Transactional
    public ResultadoInicio iniciar(NuevoProtocoloRequest peticion) {
        Optional<Protocolo> existente = protocolos.findByIdVisitaAndIdEquipo(peticion.idVisita(), peticion.idEquipo());
        if (existente.isPresent()) {
            return new ResultadoInicio(existente.get(), false);
        }
        Visita visita = visitas.findById(peticion.idVisita())
                .orElseThrow(() -> new RecursoNoEncontradoException("La visita " + peticion.idVisita() + " no existe"));
        if (visita.fechaHoraCierre != null) {
            throw new ConflictoException("La visita está cerrada");
        }
        if (protocolos.existsByNumero(peticion.numero())) {
            throw new ConflictoException("Ya existe un protocolo con el número " + peticion.numero());
        }
        Protocolo p = new Protocolo();
        p.numero = peticion.numero();
        p.idVisita = peticion.idVisita();
        p.idEquipo = peticion.idEquipo();
        p.idUsuarioRegistra = peticion.idUsuarioRegistra();
        p.fechaHoraPrueba = peticion.fechaHoraPrueba();
        p.estado = EstadoProtocolo.BORRADOR;
        return new ResultadoInicio(protocolos.save(p), true);
    }

    public Protocolo guardarAmbiente(Long id, AmbienteRequest ambiente) {
        Protocolo p = obtenerEditable(id);
        p.temperaturaC = ambiente.temperaturaC();
        p.humedadPct = ambiente.humedadPct();
        String descripcion = ambiente.descripcionMantenimiento();
        p.descripcionMantenimiento = descripcion == null || descripcion.isBlank() ? null : descripcion.trim();
        return protocolos.save(p);
    }

    @Transactional
    public Protocolo guardarEvidencia(Long id, EvidenciaRequest evidencia) {
        Protocolo p = obtenerEditable(id);
        if (evidencia.fotoBase64() != null && !evidencia.fotoBase64().isBlank()) {
            byte[] contenido;
            try {
                contenido = Base64.getDecoder().decode(evidencia.fotoBase64());
            } catch (IllegalArgumentException e) {
                throw new ReglaInvalidaException("La foto no tiene un formato válido");
            }
            fotos.save(new FotoProtocolo(id, contenido));
            p.tieneFoto = true;
        }
        if (evidencia.latitud() != null && evidencia.longitud() != null) {
            p.latitud = evidencia.latitud();
            p.longitud = evidencia.longitud();
        }
        return protocolos.save(p);
    }

    public String obtenerFoto(Long id) {
        FotoProtocolo foto = fotos.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El protocolo " + id + " no tiene foto"));
        return Base64.getEncoder().encodeToString(foto.contenido);
    }

    @Transactional
    public Protocolo cambiarEstado(Long id, CambioEstadoRequest cambio) {
        Protocolo p = obtener(id);
        if (p.estado == EstadoProtocolo.FORMALIZADO) {
            throw new ConflictoException("Un protocolo formalizado no se puede modificar");
        }
        EstadoProtocolo anterior = p.estado;
        p.estado = cambio.estado();
        Protocolo guardado = protocolos.save(p);
        String motivo = cambio.motivo() == null || cambio.motivo().isBlank() ? null : cambio.motivo().trim();
        historial.save(new HistorialEstado(id, anterior, cambio.estado(), cambio.idUsuario(), System.currentTimeMillis(), motivo));
        return guardado;
    }

    private Protocolo obtenerEditable(Long id) {
        Protocolo p = obtener(id);
        if (p.estado == EstadoProtocolo.FORMALIZADO || p.estado == EstadoProtocolo.ANULADO) {
            throw new ConflictoException("El protocolo está en estado " + p.estado + " y no se puede modificar");
        }
        return p;
    }
}
