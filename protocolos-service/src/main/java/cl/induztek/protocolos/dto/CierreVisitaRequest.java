package cl.induztek.protocolos.dto;

import jakarta.validation.constraints.NotBlank;

public record CierreVisitaRequest(@NotBlank(message = "El nombre de quien recibe es obligatorio") String receptorNombre,
        @NotBlank(message = "El RUT de quien recibe es obligatorio") String receptorRut,
        @NotBlank(message = "El cargo de quien recibe es obligatorio") String receptorCargo) {
}
