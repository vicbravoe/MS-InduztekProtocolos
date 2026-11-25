package cl.induztek.protocolos.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pruebas")
public class Prueba {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "id_protocolo", nullable = false)
    public Long idProtocolo;

    @NotNull(message = "El tipo de prueba es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    public TipoPrueba tipoPrueba;

    @NotBlank(message = "El elemento probado es obligatorio")
    @Column(nullable = false, length = 100)
    public String elemento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public MomentoPrueba momento = MomentoPrueba.UNICA;

    @Column(nullable = false)
    public boolean realizada = true;

    @Column(length = 300)
    public String motivoNoRealizada;

    public Long idInstrumento;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    public ResultadoPrueba resultado;

    public Double valorDerivado;

    @Column(length = 60)
    public String etiquetaDerivada;

    @Valid
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "mediciones", joinColumns = @JoinColumn(name = "id_prueba"))
    public List<Medicion> mediciones = new ArrayList<>();

    public Prueba() {
    }
}
