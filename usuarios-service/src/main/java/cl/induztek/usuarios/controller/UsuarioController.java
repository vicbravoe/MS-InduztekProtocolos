package cl.induztek.usuarios.controller;

import cl.induztek.usuarios.model.CredencialesRequest;
import cl.induztek.usuarios.model.Rol;
import cl.induztek.usuarios.model.Usuario;
import cl.induztek.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Personal de Induztek y empresas cliente")
public class UsuarioController {

    private final UsuarioService servicio;

    public UsuarioController(UsuarioService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @Operation(summary = "Lista usuarios", description = "Filtra por rol y por estado activo (por defecto solo activos)")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios")
    public List<Usuario> listar(@RequestParam(required = false) Rol rol,
                                @RequestParam(required = false) Boolean activos) {
        return servicio.listar(rol, activos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un usuario por id")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "El usuario no existe")
    public Usuario obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un usuario", description = "Valida el RUT (módulo 11) y que RUT y correo no estén repetidos")
    @ApiResponse(responseCode = "201", description = "Usuario creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o RUT incorrecto")
    @ApiResponse(responseCode = "409", description = "RUT o correo ya registrados")
    public Usuario crear(@Valid @RequestBody Usuario usuario) {
        return servicio.crear(usuario);
    }

    @PostMapping("/login")
    @Operation(summary = "Inicia sesión", description = "Valida correo y contraseña de un usuario activo")
    @ApiResponse(responseCode = "200", description = "Credenciales correctas")
    @ApiResponse(responseCode = "400", description = "Faltan datos")
    @ApiResponse(responseCode = "401", description = "Correo o contraseña incorrectos")
    public Usuario iniciarSesion(@Valid @RequestBody CredencialesRequest credenciales) {
        return servicio.iniciarSesion(credenciales.correo(), credenciales.clave());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un usuario")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o RUT incorrecto")
    @ApiResponse(responseCode = "404", description = "El usuario no existe")
    @ApiResponse(responseCode = "409", description = "RUT o correo ya registrados")
    public Usuario actualizar(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        return servicio.actualizar(id, usuario);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desactiva un usuario", description = "No borra el registro: lo marca como inactivo")
    @ApiResponse(responseCode = "204", description = "Usuario desactivado")
    @ApiResponse(responseCode = "404", description = "El usuario no existe")
    public void desactivar(@PathVariable Long id) {
        servicio.desactivar(id);
    }
}
