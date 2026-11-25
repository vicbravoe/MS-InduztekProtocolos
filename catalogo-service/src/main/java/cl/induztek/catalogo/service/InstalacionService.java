package cl.induztek.catalogo.service;

import cl.induztek.catalogo.error.RecursoNoEncontradoException;
import cl.induztek.catalogo.model.Instalacion;
import cl.induztek.catalogo.repository.InstalacionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstalacionService {

    private final InstalacionRepository repositorio;

    public InstalacionService(InstalacionRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<Instalacion> listar(Long idCliente) {
        return idCliente == null
                ? repositorio.findAll(Sort.by("nombre"))
                : repositorio.findByIdUsuarioClienteOrderByNombreAsc(idCliente);
    }

    public Instalacion obtener(Long id) {
        return repositorio.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("La instalación " + id + " no existe"));
    }

    public Instalacion crear(Instalacion instalacion) {
        instalacion.id = null;
        return repositorio.save(instalacion);
    }

    public Instalacion actualizar(Long id, Instalacion datos) {
        Instalacion actual = obtener(id);
        actual.idUsuarioCliente = datos.idUsuarioCliente;
        actual.nombre = datos.nombre;
        actual.comuna = datos.comuna;
        actual.observacionesAcceso = datos.observacionesAcceso == null ? "" : datos.observacionesAcceso;
        return repositorio.save(actual);
    }

    public void eliminar(Long id) {
        repositorio.delete(obtener(id));
    }
}
