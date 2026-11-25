package cl.induztek.protocolos.dto;

import cl.induztek.protocolos.model.ResultadoPrueba;
import jakarta.validation.constraints.NotNull;

public record ResultadoPruebaRequest(@NotNull(message = "El resultado es obligatorio") ResultadoPrueba resultado) {
}
