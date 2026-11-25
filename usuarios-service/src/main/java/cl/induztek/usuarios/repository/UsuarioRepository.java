package cl.induztek.usuarios.repository;

import cl.induztek.usuarios.model.Rol;
import cl.induztek.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findByActivoTrueOrderByRolAscNombreAsc();

    List<Usuario> findByRolAndActivoTrueOrderByNombreAsc(Rol rol);

    List<Usuario> findByRolOrderByNombreAsc(Rol rol);

    Optional<Usuario> findByCorreo(String correo);

    List<Usuario> findByClaveIsNull();

    boolean existsByRut(String rut);

    boolean existsByCorreo(String correo);

    boolean existsByRutAndIdNot(String rut, Long id);

    boolean existsByCorreoAndIdNot(String correo, Long id);
}
