package cl.induztek.catalogo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "instalaciones")
public class Instalacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public Long idUsuarioCliente;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    public String nombre;

    @NotBlank(message = "La comuna es obligatoria")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    public String comuna;

    @Column(nullable = false, length = 300)
    public String observacionesAcceso = "";

    public Instalacion() {
    }

    public Instalacion(Long idUsuarioCliente, String nombre, String comuna, String observacionesAcceso) {
        this.idUsuarioCliente = idUsuarioCliente;
        this.nombre = nombre;
        this.comuna = comuna;
        this.observacionesAcceso = observacionesAcceso;
    }
}
