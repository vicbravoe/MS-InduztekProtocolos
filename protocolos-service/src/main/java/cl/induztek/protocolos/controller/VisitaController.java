package cl.induztek.protocolos.controller;

import cl.induztek.protocolos.dto.CierreVisitaRequest;
import cl.induztek.protocolos.dto.VisitaResumen;
import cl.induztek.protocolos.model.Visita;
import cl.induztek.protocolos.service.VisitaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/visitas")
@Tag(name = "Visitas", description = "Visitas técnicas a una instalación")
public class VisitaController {

    private final VisitaService servicio;

    public VisitaController(VisitaService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @Operation(summary = "Lista visitas con su total de protocolos y los formalizados")
    @ApiResponse(responseCode = "200", description = "Lista de visitas")
    public List<VisitaResumen> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene una visita")
    @ApiResponse(responseCode = "200", description = "Visita encontrada")
    @ApiResponse(responseCode = "404", description = "La visita no existe")
    public Visita obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea una visita con sus participantes")
    @ApiResponse(responseCode = "201", description = "Visita creada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "409", description = "El número de OT ya existe")
    public Visita crear(@Valid @RequestBody Visita visita) {
        return servicio.crear(visita);
    }

    @PutMapping("/{id}/cierre")
    @Operation(summary = "Cierra la visita", description = "Registra quién recibe (nombre, RUT válido y cargo) y la hora de cierre")
    @ApiResponse(responseCode = "200", description = "Visita cerrada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o RUT incorrecto")
    @ApiResponse(responseCode = "404", description = "La visita no existe")
    @ApiResponse(responseCode = "409", description = "La visita ya estaba cerrada")
    public Visita cerrar(@PathVariable Long id, @Valid @RequestBody CierreVisitaRequest cierre) {
        return servicio.cerrar(id, cierre);
    }
}
