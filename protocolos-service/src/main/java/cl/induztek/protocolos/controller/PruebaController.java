package cl.induztek.protocolos.controller;

import cl.induztek.protocolos.dto.ResultadoPruebaRequest;
import cl.induztek.protocolos.model.Prueba;
import cl.induztek.protocolos.service.PruebaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Pruebas", description = "Pruebas eléctricas y sus mediciones")
public class PruebaController {

    private final PruebaService servicio;

    public PruebaController(PruebaService servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/protocolos/{idProtocolo}/pruebas")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra una prueba con sus mediciones en el protocolo")
    @ApiResponse(responseCode = "201", description = "Prueba registrada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "El protocolo no existe")
    @ApiResponse(responseCode = "409", description = "El protocolo no admite cambios")
    public Prueba registrar(@PathVariable Long idProtocolo, @Valid @RequestBody Prueba prueba) {
        return servicio.registrar(idProtocolo, prueba);
    }

    @PutMapping("/pruebas/{id}/resultado")
    @Operation(summary = "Define a mano el resultado de una prueba", description = "Se usa en la resistencia de devanados")
    @ApiResponse(responseCode = "200", description = "Resultado guardado")
    @ApiResponse(responseCode = "400", description = "Resultado inválido")
    @ApiResponse(responseCode = "404", description = "La prueba no existe")
    @ApiResponse(responseCode = "409", description = "El protocolo no admite cambios")
    public Prueba fijarResultado(@PathVariable Long id, @Valid @RequestBody ResultadoPruebaRequest cuerpo) {
        return servicio.fijarResultado(id, cuerpo.resultado());
    }
}
