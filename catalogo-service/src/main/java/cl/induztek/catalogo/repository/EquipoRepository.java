package cl.induztek.catalogo.repository;

import cl.induztek.catalogo.model.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    List<Equipo> findByIdInstalacionOrderByTagAsc(Long idInstalacion);

    boolean existsByIdInstalacionAndTag(Long idInstalacion, String tag);

    boolean existsByIdInstalacionAndTagAndIdNot(Long idInstalacion, String tag, Long id);
}
