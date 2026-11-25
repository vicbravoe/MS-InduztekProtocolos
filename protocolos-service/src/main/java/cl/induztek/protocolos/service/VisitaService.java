package cl.induztek.protocolos.service;

import cl.induztek.protocolos.dto.CierreVisitaRequest;
import cl.induztek.protocolos.dto.VisitaResumen;
import cl.induztek.protocolos.error.ConflictoException;
import cl.induztek.protocolos.error.RecursoNoEncontradoException;
import cl.induztek.protocolos.error.ReglaInvalidaException;
import cl.induztek.protocolos.model.EstadoProtocolo;
import cl.induztek.protocolos.model.Visita;
import cl.induztek.protocolos.repository.ProtocoloRepository;
import cl.induztek.protocolos.repository.VisitaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisitaService {

    private final VisitaRepository visitas;
    private final ProtocoloRepository protocolos;

    public VisitaService(VisitaRepository visitas, ProtocoloRepository protocolos) {
        this.visitas = visitas;
        this.protocolos = protocolos;
    }

    public List<VisitaResumen> listar() {
        return visitas.findAll(Sort.by(Sort.Direction.DESC, "fecha")).stream()
                .map(v -> new VisitaResumen(v,
                        protocolos.countByIdVisita(v.id),
                        protocolos.countByIdVisitaAndEstado(v.id, EstadoProtocolo.FORMALIZADO)))
                .toList();
    }

    public Visita obtener(Long id) {
        return visitas.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("La visita " + id + " no existe"));
    }

    public Visita crear(Visita visita) {
        if (visitas.existsByNumeroOt(visita.numeroOt)) {
            throw new ConflictoException("Ya existe una visita con el número " + visita.numeroOt);
        }
        visita.id = null;
        visita.fechaHoraCierre = null;
        visita.receptorNombre = null;
        visita.receptorRut = null;
        visita.receptorCargo = null;
        return visitas.save(visita);
    }

    public Visita cerrar(Long id, CierreVisitaRequest cierre) {
        Visita visita = obtener(id);
        if (visita.fechaHoraCierre != null) {
            throw new ConflictoException("La visita ya está cerrada");
        }
        if (!RutUtil.esValido(cierre.receptorRut())) {
            throw new ReglaInvalidaException("El RUT de quien recibe no es válido");
        }
        visita.fechaHoraCierre = System.currentTimeMillis();
        visita.receptorNombre = cierre.receptorNombre().trim();
        visita.receptorRut = RutUtil.formatear(cierre.receptorRut());
        visita.receptorCargo = cierre.receptorCargo().trim();
        return visitas.save(visita);
    }
}
