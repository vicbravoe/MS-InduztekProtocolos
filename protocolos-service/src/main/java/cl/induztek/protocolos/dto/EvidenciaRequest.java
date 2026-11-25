package cl.induztek.protocolos.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public record EvidenciaRequest(String fotoBase64,
        @DecimalMin(value = "-90.0", message = "Latitud inválida") @DecimalMax(value = "90.0", message = "Latitud inválida") Double latitud,
        @DecimalMin(value = "-180.0", message = "Longitud inválida") @DecimalMax(value = "180.0", message = "Longitud inválida") Double longitud) {
}
