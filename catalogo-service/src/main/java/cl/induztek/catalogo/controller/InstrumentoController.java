package cl.induztek.catalogo.controller;

import cl.induztek.catalogo.model.Instrumento;
import cl.induztek.catalogo.service.InstrumentoService;
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
@RequestMapping("/api/instrumentos")
@Tag(name = "Instrumentos", description = "Instrumentos de medición con su calibración")
public class InstrumentoController {

    private final InstrumentoService servicio;

    public InstrumentoController(InstrumentoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @Operation(summary = "Lista instrumentos")
    @ApiResponse(responseCode = "200", description = "Lista de instrumentos")
    public List<Instrumento> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene por id")
    @ApiResponse(responseCode = "200", description = "Encontrado")
    @ApiResponse(responseCode = "404", description = "No existe")
    public Instrumento obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un registro")
    @ApiResponse(responseCode = "201", description = "Creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "Referencia inexistente")
    @ApiResponse(responseCode = "409", description = "Registro repetido")
    public Instrumento crear(@Valid @RequestBody Instrumento cuerpo) {
        return servicio.crear(cuerpo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un registro")
    @ApiResponse(responseCode = "200", description = "Actualizado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "No existe")
    @ApiResponse(responseCode = "409", description = "Registro repetido")
    public Instrumento actualizar(@PathVariable Long id, @Valid @RequestBody Instrumento cuerpo) {
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
