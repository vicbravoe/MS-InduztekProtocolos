package cl.induztek.protocolos.repository;

import cl.induztek.protocolos.model.FotoProtocolo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FotoProtocoloRepository extends JpaRepository<FotoProtocolo, Long> {
}
