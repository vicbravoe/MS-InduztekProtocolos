package cl.induztek.protocolos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "historial_estados")
public class HistorialEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "id_protocolo", nullable = false)
    public Long idProtocolo;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    public EstadoProtocolo estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    public EstadoProtocolo estadoNuevo;

    @Column(nullable = false)
    public Long idUsuario;

    @Column(nullable = false)
    public Long fechaHora;

    @Column(length = 300)
    public String motivo;

    public HistorialEstado() {
    }

    public HistorialEstado(Long idProtocolo, EstadoProtocolo estadoAnterior, EstadoProtocolo estadoNuevo, Long idUsuario, Long fechaHora, String motivo) {
        this.idProtocolo = idProtocolo;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.idUsuario = idUsuario;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
    }
}
