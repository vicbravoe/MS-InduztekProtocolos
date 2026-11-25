package cl.induztek.protocolos.repository;

import cl.induztek.protocolos.model.HistorialEstado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HistorialEstadoRepository extends JpaRepository<HistorialEstado, Long> {

    List<HistorialEstado> findByIdProtocoloOrderByFechaHoraAsc(Long idProtocolo);
}
