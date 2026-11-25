package cl.induztek.catalogo.controller;

import cl.induztek.catalogo.model.Equipo;
import cl.induztek.catalogo.service.EquipoService;
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
@RequestMapping("/api/equipos")
@Tag(name = "Equipos", description = "Equipos eléctricos identificados por TAG dentro de una instalación")
public class EquipoController {

    private final EquipoService servicio;

    public EquipoController(EquipoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @Operation(summary = "Lista equipos")
    @ApiResponse(responseCode = "200", description = "Lista de equipos")
    public List<Equipo> listar(@RequestParam(required = false) Long idInstalacion) {
        return servicio.listar(idInstalacion);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene por id")
    @ApiResponse(responseCode = "200", description = "Encontrado")
    @ApiResponse(responseCode = "404", description = "No existe")
    public Equipo obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un registro")
    @ApiResponse(responseCode = "201", description = "Creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "Referencia inexistente")
    @ApiResponse(responseCode = "409", description = "Registro repetido")
    public Equipo crear(@Valid @RequestBody Equipo cuerpo) {
        return servicio.crear(cuerpo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un registro")
    @ApiResponse(responseCode = "200", description = "Actualizado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "No existe")
    @ApiResponse(responseCode = "409", description = "Registro repetido")
    public Equipo actualizar(@PathVariable Long id, @Valid @RequestBody Equipo cuerpo) {
        return servicio.actualizar(id, cuerpo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Elimina un registro")
    @ApiResponse(responseCode = "204", description = "Eliminado")
    @ApiResponse(responseCode = "404", description = "No existe")
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}
