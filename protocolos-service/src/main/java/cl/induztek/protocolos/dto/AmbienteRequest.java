package cl.induztek.protocolos.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record AmbienteRequest(@NotNull(message = "La temperatura es obligatoria") @DecimalMin(value = "-20.0", message = "Temperatura mínima -20") @DecimalMax(value = "60.0", message = "Temperatura máxima 60") Double temperaturaC,
        @NotNull(message = "La humedad es obligatoria") @DecimalMin(value = "0.0", message = "Humedad mínima 0") @DecimalMax(value = "100.0", message = "Humedad máxima 100") Double humedadPct,
        String descripcionMantenimiento) {
}
