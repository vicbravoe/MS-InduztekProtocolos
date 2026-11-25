package cl.induztek.protocolos.dto;

import cl.induztek.protocolos.model.Visita;

public record VisitaResumen(Visita visita, long totalProtocolos, long formalizados) {
}
