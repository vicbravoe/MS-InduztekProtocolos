package cl.induztek.usuarios.model;

import jakarta.validation.constraints.NotBlank;

public record CredencialesRequest(
        @NotBlank(message = "El correo es obligatorio") String correo,
        @NotBlank(message = "La contraseña es obligatoria") String clave) {
}
