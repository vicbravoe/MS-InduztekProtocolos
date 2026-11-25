package cl.induztek.protocolos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "protocolos", uniqueConstraints = @UniqueConstraint(columnNames = {"id_visita", "id_equipo"}))
public class Protocolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true, length = 30)
    public String numero;

    @Column(name = "id_visita", nullable = false)
    public Long idVisita;

    @Column(name = "id_equipo", nullable = false)
    public Long idEquipo;

    @Column(nullable = false)
    public Long idUsuarioRegistra;

    @Column(nullable = false)
    public Long fechaHoraPrueba;

    public Double temperaturaC;

    public Double humedadPct;

    @Column(length = 500)
    public String descripcionMantenimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    public EstadoProtocolo estado = EstadoProtocolo.BORRADOR;

    public Double latitud;

    public Double longitud;

    @Column(nullable = false)
    public boolean tieneFoto = false;

    public Protocolo() {
    }
}
