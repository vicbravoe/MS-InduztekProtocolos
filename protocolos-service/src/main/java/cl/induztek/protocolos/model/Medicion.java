package cl.induztek.protocolos.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Embeddable
public class Medicion {

    @NotBlank(message = "El parámetro es obligatorio")
    public String parametro;

    @NotNull(message = "El valor es obligatorio")
    public Double valor;

    @NotBlank(message = "La unidad es obligatoria")
    public String unidad;

    public Medicion() {
    }

    public Medicion(String parametro, Double valor, String unidad) {
        this.parametro = parametro;
        this.valor = valor;
        this.unidad = unidad;
    }
}
