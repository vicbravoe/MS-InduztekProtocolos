package cl.induztek.protocolos.service;

import cl.induztek.protocolos.error.ConflictoException;
import cl.induztek.protocolos.error.RecursoNoEncontradoException;
import cl.induztek.protocolos.model.EstadoProtocolo;
import cl.induztek.protocolos.model.Protocolo;
import cl.induztek.protocolos.model.Prueba;
import cl.induztek.protocolos.model.ResultadoPrueba;
import cl.induztek.protocolos.repository.ProtocoloRepository;
import cl.induztek.protocolos.repository.PruebaRepository;
import org.springframework.stereotype.Service;

@Service
public class PruebaService {

    private final PruebaRepository pruebas;
    private final ProtocoloRepository protocolos;

    public PruebaService(PruebaRepository pruebas, ProtocoloRepository protocolos) {
        this.pruebas = pruebas;
        this.protocolos = protocolos;
    }

    public Prueba registrar(Long idProtocolo, Prueba prueba) {
        Protocolo protocolo = protocolos.findById(idProtocolo)
                .orElseThrow(() -> new RecursoNoEncontradoException("El protocolo " + idProtocolo + " no existe"));
        validarModificable(protocolo);
        prueba.id = null;
        prueba.idProtocolo = idProtocolo;
        return pruebas.save(prueba);
    }

    public Prueba fijarResultado(Long idPrueba, ResultadoPrueba resultado) {
        Prueba prueba = pruebas.findById(idPrueba)
                .orElseThrow(() -> new RecursoNoEncontradoException("La prueba " + idPrueba + " no existe"));
        protocolos.findById(prueba.idProtocolo).ifPresent(this::validarModificable);
        prueba.resultado = resultado;
        return pruebas.save(prueba);
    }

    private void validarModificable(Protocolo protocolo) {
        if (protocolo.estado == EstadoProtocolo.FORMALIZADO || protocolo.estado == EstadoProtocolo.ANULADO) {
            throw new ConflictoException("El protocolo está en estado " + protocolo.estado + " y no se puede modificar");
        }
    }
}
