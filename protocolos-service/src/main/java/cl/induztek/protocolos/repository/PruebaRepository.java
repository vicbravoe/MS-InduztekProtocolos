package cl.induztek.protocolos.repository;

import cl.induztek.protocolos.model.Prueba;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PruebaRepository extends JpaRepository<Prueba, Long> {

    List<Prueba> findByIdProtocoloOrderByIdAsc(Long idProtocolo);
}
