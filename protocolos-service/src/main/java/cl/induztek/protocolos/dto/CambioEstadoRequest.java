package cl.induztek.protocolos.dto;

import cl.induztek.protocolos.model.EstadoProtocolo;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoRequest(@NotNull(message = "El nuevo estado es obligatorio") EstadoProtocolo estado,
        @NotNull(message = "El usuario es obligatorio") Long idUsuario,
        String motivo) {
}
