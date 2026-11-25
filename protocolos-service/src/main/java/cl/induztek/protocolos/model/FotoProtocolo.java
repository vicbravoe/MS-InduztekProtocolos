package cl.induztek.protocolos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "fotos_protocolo")
public class FotoProtocolo {

    @Id
    public Long idProtocolo;

    @Lob
    @Column(name = "contenido", columnDefinition = "LONGBLOB", nullable = false)
    public byte[] contenido;

    public FotoProtocolo() {
    }

    public FotoProtocolo(Long idProtocolo, byte[] contenido) {
        this.idProtocolo = idProtocolo;
        this.contenido = contenido;
    }
}
