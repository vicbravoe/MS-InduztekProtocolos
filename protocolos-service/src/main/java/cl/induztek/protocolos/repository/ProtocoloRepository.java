package cl.induztek.protocolos.repository;

import cl.induztek.protocolos.model.Protocolo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProtocoloRepository extends JpaRepository<Protocolo, Long> {

    List<Protocolo> findByIdVisitaOrderByIdAsc(Long idVisita);

    List<Protocolo> findByIdEquipoOrderByFechaHoraPruebaDesc(Long idEquipo);

    List<Protocolo> findByEstadoOrderByFechaHoraPruebaAsc(cl.induztek.protocolos.model.EstadoProtocolo estado);

    Optional<Protocolo> findByIdVisitaAndIdEquipo(Long idVisita, Long idEquipo);

    boolean existsByNumero(String numero);

    long countByIdVisita(Long idVisita);

    long countByIdVisitaAndEstado(Long idVisita, cl.induztek.protocolos.model.EstadoProtocolo estado);
}
