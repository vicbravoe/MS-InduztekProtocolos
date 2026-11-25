package cl.induztek.protocolos.repository;

import cl.induztek.protocolos.model.Visita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VisitaRepository extends JpaRepository<Visita, Long> {

    boolean existsByNumeroOt(String numeroOt);
}
