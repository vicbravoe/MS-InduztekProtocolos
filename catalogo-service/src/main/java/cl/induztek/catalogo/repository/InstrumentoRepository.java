package cl.induztek.catalogo.repository;

import cl.induztek.catalogo.model.Instrumento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstrumentoRepository extends JpaRepository<Instrumento, Long> {
}
