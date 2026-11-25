package cl.induztek.protocolos.dto;

import cl.induztek.protocolos.model.HistorialEstado;
import cl.induztek.protocolos.model.Protocolo;
import cl.induztek.protocolos.model.Prueba;

import java.util.List;

public record ProtocoloDetalle(Protocolo protocolo, List<Prueba> pruebas, List<HistorialEstado> historial) {
}
