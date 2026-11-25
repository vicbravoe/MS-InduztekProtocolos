package cl.induztek.protocolos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NuevoProtocoloRequest(@NotBlank(message = "El número es obligatorio") String numero,
        @NotNull(message = "La visita es obligatoria") Long idVisita,
        @NotNull(message = "El equipo es obligatorio") Long idEquipo,
        @NotNull(message = "El usuario es obligatorio") Long idUsuarioRegistra,
        @NotNull(message = "La fecha y hora son obligatorias") Long fechaHoraPrueba) {
}
