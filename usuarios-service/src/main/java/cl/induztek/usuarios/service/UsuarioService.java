package cl.induztek.usuarios.service;

import cl.induztek.usuarios.error.ConflictoException;
import cl.induztek.usuarios.error.CredencialesInvalidasException;
import cl.induztek.usuarios.error.RecursoNoEncontradoException;
import cl.induztek.usuarios.error.ReglaInvalidaException;
import cl.induztek.usuarios.model.Rol;
import cl.induztek.usuarios.model.Usuario;
import cl.induztek.usuarios.repository.UsuarioRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repositorio;

    public UsuarioService(UsuarioRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<Usuario> listar(Rol rol, Boolean soloActivos) {
        if (Boolean.FALSE.equals(soloActivos)) {
            return rol == null ? repositorio.findAll(Sort.by("rol", "nombre")) : repositorio.findByRolOrderByNombreAsc(rol);
        }
        return rol == null ? repositorio.findByActivoTrueOrderByRolAscNombreAsc() : repositorio.findByRolAndActivoTrueOrderByNombreAsc(rol);
    }

    public Usuario obtener(Long id) {
        return repositorio.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("El usuario " + id + " no existe"));
    }

    public Usuario crear(Usuario usuario) {
        validarRut(usuario.rut);
        usuario.rut = RutUtil.formatear(usuario.rut);
        if (repositorio.existsByRut(usuario.rut)) {
            throw new ConflictoException("Ya existe un usuario con el RUT " + usuario.rut);
        }
        if (repositorio.existsByCorreo(usuario.correo)) {
            throw new ConflictoException("Ya existe un usuario con el correo " + usuario.correo);
        }
        usuario.clave = prepararClave(usuario.clave);
        usuario.id = null;
        return repositorio.save(usuario);
    }

    public Usuario actualizar(Long id, Usuario datos) {
        Usuario actual = obtener(id);
        validarRut(datos.rut);
        String rut = RutUtil.formatear(datos.rut);
        if (repositorio.existsByRutAndIdNot(rut, id)) {
            throw new ConflictoException("Ya existe un usuario con el RUT " + rut);
        }
        if (repositorio.existsByCorreoAndIdNot(datos.correo, id)) {
            throw new ConflictoException("Ya existe un usuario con el correo " + datos.correo);
        }
        actual.rol = datos.rol;
        actual.rut = rut;
        actual.nombre = datos.nombre;
        actual.correo = datos.correo;
        actual.telefono = datos.telefono == null ? "" : datos.telefono;
        actual.activo = datos.activo;
        actual.giro = datos.giro;
        actual.contactoNombre = datos.contactoNombre;
        actual.contactoCargo = datos.contactoCargo;
        if (datos.clave != null && !datos.clave.isBlank()) {
            actual.clave = prepararClave(datos.clave);
        }
        return repositorio.save(actual);
    }

    public void desactivar(Long id) {
        Usuario usuario = obtener(id);
        usuario.activo = false;
        repositorio.save(usuario);
    }

    public Usuario iniciarSesion(String correo, String clave) {
        Usuario usuario = repositorio.findByCorreo(correo.trim()).orElse(null);
        if (usuario == null || !usuario.activo || !ClaveUtil.coincide(clave, usuario.clave)) {
            throw new CredencialesInvalidasException("Correo o contraseña incorrectos");
        }
        return usuario;
    }

    public void asignarClavesFaltantes(String claveInicial) {
        for (Usuario usuario : repositorio.findByClaveIsNull()) {
            usuario.clave = ClaveUtil.hashear(claveInicial);
            repositorio.save(usuario);
        }
    }

    private String prepararClave(String clave) {
        if (clave == null || clave.isBlank()) {
            return null;
        }
        if (clave.length() < ClaveUtil.LARGO_MINIMO) {
            throw new ReglaInvalidaException("La contraseña debe tener al menos " + ClaveUtil.LARGO_MINIMO + " caracteres");
        }
        return ClaveUtil.hashear(clave);
    }

    private void validarRut(String rut) {
        if (!RutUtil.esValido(rut)) {
            throw new ReglaInvalidaException("El RUT no es válido");
        }
    }
}
