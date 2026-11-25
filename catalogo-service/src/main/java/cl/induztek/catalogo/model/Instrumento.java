package cl.induztek.catalogo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "instrumentos")
public class Instrumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    public String nombre;

    @Column(nullable = false, length = 100)
    public String marca = "";

    @Column(nullable = false, length = 100)
    public String modelo = "";

    @Column(nullable = false, length = 100)
    public String numeroSerie = "";

    @NotNull(message = "La fecha de calibración es obligatoria")
    @Column(nullable = false)
    public Long fechaUltimaCalibracion;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Column(nullable = false)
    public Long fechaVencimiento;

    public Instrumento() {
    }

    public Instrumento(String nombre, String marca, String modelo, String numeroSerie, Long fechaUltimaCalibracion, Long fechaVencimiento) {
        this.nombre = nombre;
        this.marca = marca;
        this.modelo = modelo;
        this.numeroSerie = numeroSerie;
        this.fechaUltimaCalibracion = fechaUltimaCalibracion;
        this.fechaVencimiento = fechaVencimiento;
    }
}
