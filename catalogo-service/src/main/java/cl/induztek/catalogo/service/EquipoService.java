package cl.induztek.catalogo.service;

import cl.induztek.catalogo.error.ConflictoException;
import cl.induztek.catalogo.error.RecursoNoEncontradoException;
import cl.induztek.catalogo.model.Equipo;
import cl.induztek.catalogo.repository.EquipoRepository;
import cl.induztek.catalogo.repository.InstalacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipoService {

    private final EquipoRepository repositorio;
    private final InstalacionRepository instalaciones;

    public EquipoService(EquipoRepository repositorio, InstalacionRepository instalaciones) {
        this.repositorio = repositorio;
        this.instalaciones = instalaciones;
    }

    public List<Equipo> listar(Long idInstalacion) {
        return idInstalacion == null ? repositorio.findAll() : repositorio.findByIdInstalacionOrderByTagAsc(idInstalacion);
    }

    public Equipo obtener(Long id) {
        return repositorio.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("El equipo " + id + " no existe"));
    }

    public Equipo crear(Equipo equipo) {
        equipo.tag = equipo.tag.trim();
        validarInstalacion(equipo.idInstalacion);
        if (repositorio.existsByIdInstalacionAndTag(equipo.idInstalacion, equipo.tag)) {
            throw new ConflictoException("El equipo \"" + equipo.tag + "\" ya existe en esta instalación");
        }
        equipo.id = null;
        return repositorio.save(equipo);
    }

    public Equipo actualizar(Long id, Equipo datos) {
        Equipo actual = obtener(id);
        String tag = datos.tag.trim();
        validarInstalacion(datos.idInstalacion);
        if (repositorio.existsByIdInstalacionAndTagAndIdNot(datos.idInstalacion, tag, id)) {
            throw new ConflictoException("El equipo \"" + tag + "\" ya existe en esta instalación");
        }
        actual.idInstalacion = datos.idInstalacion;
        actual.tag = tag;
        actual.tipo = datos.tipo;
        actual.marca = datos.marca == null ? "" : datos.marca;
        actual.numeroSerie = datos.numeroSerie == null ? "" : datos.numeroSerie;
        actual.tensiones = datos.tensiones == null ? "" : datos.tensiones;
        actual.maxContactosUohm = datos.maxContactosUohm;
        return repositorio.save(actual);
    }

    public void eliminar(Long id) {
        repositorio.delete(obtener(id));
    }

    private void validarInstalacion(Long idInstalacion) {
        if (!instalaciones.existsById(idInstalacion)) {
            throw new RecursoNoEncontradoException("La instalación " + idInstalacion + " no existe");
        }
    }
}
