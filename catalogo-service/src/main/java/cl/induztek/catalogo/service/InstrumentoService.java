package cl.induztek.catalogo.service;

import cl.induztek.catalogo.error.RecursoNoEncontradoException;
import cl.induztek.catalogo.error.ReglaInvalidaException;
import cl.induztek.catalogo.model.Instrumento;
import cl.induztek.catalogo.repository.InstrumentoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstrumentoService {

    private final InstrumentoRepository repositorio;

    public InstrumentoService(InstrumentoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<Instrumento> listar() {
        return repositorio.findAll(Sort.by("nombre"));
    }

    public Instrumento obtener(Long id) {
        return repositorio.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("El instrumento " + id + " no existe"));
    }

    public Instrumento crear(Instrumento instrumento) {
        validarFechas(instrumento);
        instrumento.id = null;
        return repositorio.save(instrumento);
    }

    public Instrumento actualizar(Long id, Instrumento datos) {
        Instrumento actual = obtener(id);
        validarFechas(datos);
        actual.nombre = datos.nombre;
        actual.marca = datos.marca == null ? "" : datos.marca;
        actual.modelo = datos.modelo == null ? "" : datos.modelo;
        actual.numeroSerie = datos.numeroSerie == null ? "" : datos.numeroSerie;
        actual.fechaUltimaCalibracion = datos.fechaUltimaCalibracion;
        actual.fechaVencimiento = datos.fechaVencimiento;
        return repositorio.save(actual);
    }

    public void eliminar(Long id) {
        repositorio.delete(obtener(id));
    }

    private void validarFechas(Instrumento instrumento) {
        if (instrumento.fechaVencimiento <= instrumento.fechaUltimaCalibracion) {
            throw new ReglaInvalidaException("El vencimiento debe ser posterior a la calibración");
        }
    }
}
