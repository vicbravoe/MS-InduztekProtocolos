package cl.induztek.protocolos.controller;

import cl.induztek.protocolos.dto.AmbienteRequest;
import cl.induztek.protocolos.dto.CambioEstadoRequest;
import cl.induztek.protocolos.dto.EvidenciaRequest;
import cl.induztek.protocolos.dto.FotoRespuesta;
import cl.induztek.protocolos.dto.NuevoProtocoloRequest;
import cl.induztek.protocolos.dto.ProtocoloDetalle;
import cl.induztek.protocolos.dto.ResultadoInicio;
import cl.induztek.protocolos.model.EstadoProtocolo;
import cl.induztek.protocolos.model.Protocolo;
import cl.induztek.protocolos.service.ProtocoloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/protocolos")
@Tag(name = "Protocolos", description = "Protocolos de prueba por equipo y visita")
public class ProtocoloController {

    private final ProtocoloService servicio;

    public ProtocoloController(ProtocoloService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @Operation(summary = "Lista protocolos", description = "Filtros opcionales: idVisita, idEquipo, estado")
    @ApiResponse(responseCode = "200", description = "Lista de protocolos")
    public List<Protocolo> listar(@RequestParam(required = false) Long idVisita,
                                  @RequestParam(required = false) Long idEquipo,
                                  @RequestParam(required = false) EstadoProtocolo estado) {
        return servicio.listar(idVisita, idEquipo, estado);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un protocolo")
    @ApiResponse(responseCode = "200", description = "Protocolo encontrado")
    @ApiResponse(responseCode = "404", description = "El protocolo no existe")
    public Protocolo obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @GetMapping("/{id}/detalle")
    @Operation(summary = "Obtiene el protocolo con sus pruebas, mediciones e historial de estados")
    @ApiResponse(responseCode = "200", description = "Detalle del protocolo")
    @ApiResponse(responseCode = "404", description = "El protocolo no existe")
    public ProtocoloDetalle detalle(@PathVariable Long id) {
        return servicio.detalle(id);
    }

    @GetMapping("/equipo/{idEquipo}/detalle")
    @Operation(summary = "Historial de protocolos de un equipo, del más reciente al más antiguo")
    @ApiResponse(responseCode = "200", description = "Lista de detalles")
    public List<ProtocoloDetalle> detalleDeEquipo(@PathVariable Long idEquipo) {
        return servicio.detalleDeEquipo(idEquipo);
    }

    @PostMapping
    @Operation(summary = "Abre el protocolo de un equipo en una visita", description = "Si ya existe uno para ese equipo y visita, devuelve el existente")
    @ApiResponse(responseCode = "201", description = "Protocolo creado")
    @ApiResponse(responseCode = "200", description = "Ya existía: se devuelve el mismo")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "La visita no existe")
    @ApiResponse(responseCode = "409", description = "Visita cerrada o número repetido")
    public ResponseEntity<Protocolo> iniciar(@Valid @RequestBody NuevoProtocoloRequest peticion) {
        ResultadoInicio resultado = servicio.iniciar(peticion);
        HttpStatus estado = resultado.creado() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(estado).body(resultado.protocolo());
    }

    @PutMapping("/{id}/ambiente")
    @Operation(summary = "Guarda temperatura, humedad y descripción del mantenimiento")
    @ApiResponse(responseCode = "200", description = "Condiciones guardadas")
    @ApiResponse(responseCode = "400", description = "Valores fuera de rango")
    @ApiResponse(responseCode = "404", description = "El protocolo no existe")
    @ApiResponse(responseCode = "409", description = "El protocolo no admite cambios")
    public Protocolo guardarAmbiente(@PathVariable Long id, @Valid @RequestBody AmbienteRequest ambiente) {
        return servicio.guardarAmbiente(id, ambiente);
    }

    @PutMapping("/{id}/evidencia")
    @Operation(summary = "Guarda la foto (Base64) y la ubicación GPS del protocolo")
    @ApiResponse(responseCode = "200", description = "Evidencia guardada")
    @ApiResponse(responseCode = "400", description = "Foto o coordenadas inválidas")
    @ApiResponse(responseCode = "404", description = "El protocolo no existe")
    @ApiResponse(responseCode = "409", description = "El protocolo no admite cambios")
    public Protocolo guardarEvidencia(@PathVariable Long id, @Valid @RequestBody EvidenciaRequest evidencia) {
        return servicio.guardarEvidencia(id, evidencia);
    }

    @GetMapping("/{id}/foto")
    @Operation(summary = "Devuelve la foto del protocolo en Base64")
    @ApiResponse(responseCode = "200", description = "Foto encontrada")
    @ApiResponse(responseCode = "404", description = "El protocolo no tiene foto")
    public FotoRespuesta foto(@PathVariable Long id) {
        return new FotoRespuesta(servicio.obtenerFoto(id));
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Cambia el estado y deja constancia en el historial")
    @ApiResponse(responseCode = "200", description = "Estado cambiado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "El protocolo no existe")
    @ApiResponse(responseCode = "409", description = "El protocolo ya está formalizado")
    public Protocolo cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambioEstadoRequest cambio) {
        return servicio.cambiarEstado(id, cambio);
    }
}
