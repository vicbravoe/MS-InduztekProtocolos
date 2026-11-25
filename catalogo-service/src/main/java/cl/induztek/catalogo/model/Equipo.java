package cl.induztek.catalogo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "equipos", uniqueConstraints = @UniqueConstraint(columnNames = {"id_instalacion", "tag"}))
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull(message = "La instalación es obligatoria")
    @Column(name = "id_instalacion", nullable = false)
    public Long idInstalacion;

    @NotBlank(message = "El TAG es obligatorio")
    @Size(max = 60)
    @Column(nullable = false, length = 60)
    public String tag;

    @NotNull(message = "El tipo de equipo es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    public TipoEquipo tipo;

    @Column(nullable = false, length = 100)
    public String marca = "";

    @Column(nullable = false, length = 100)
    public String numeroSerie = "";

    @Column(nullable = false, length = 100)
    public String tensiones = "";

    @DecimalMin(value = "0.0", message = "El máximo de contactos no puede ser negativo")
    public Double maxContactosUohm;

    public Equipo() {
    }

    public Equipo(Long idInstalacion, String tag, TipoEquipo tipo, String marca, String numeroSerie, String tensiones, Double maxContactosUohm) {
        this.idInstalacion = idInstalacion;
        this.tag = tag;
        this.tipo = tipo;
        this.marca = marca;
        this.numeroSerie = numeroSerie;
        this.tensiones = tensiones;
        this.maxContactosUohm = maxContactosUohm;
    }
}
