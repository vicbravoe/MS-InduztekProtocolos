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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "visitas")
public class Visita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank(message = "El número de OT es obligatorio")
    @Column(nullable = false, unique = true, length = 30)
    public String numeroOt;

    @NotNull(message = "La instalación es obligatoria")
    @Column(nullable = false)
    public Long idInstalacion;

    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    public Long fecha;

    @NotNull(message = "El tipo de visita es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    public TipoVisita tipoVisita;

    @NotNull(message = "El ingeniero a cargo es obligatorio")
    @Column(nullable = false)
    public Long idIngenieroCargo;

    public Long fechaHoraCierre;

    public String receptorNombre;

    public String receptorRut;

    public String receptorCargo;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "visita_participantes", joinColumns = @JoinColumn(name = "id_visita"))
    @Column(name = "id_usuario")
    public List<Long> participantes = new ArrayList<>();

    public Visita() {
    }
}
