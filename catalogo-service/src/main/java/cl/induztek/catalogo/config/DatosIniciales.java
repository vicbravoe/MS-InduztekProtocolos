package cl.induztek.catalogo.config;

import cl.induztek.catalogo.model.Equipo;
import cl.induztek.catalogo.model.Instalacion;
import cl.induztek.catalogo.model.Instrumento;
import cl.induztek.catalogo.model.TipoEquipo;
import cl.induztek.catalogo.repository.EquipoRepository;
import cl.induztek.catalogo.repository.InstalacionRepository;
import cl.induztek.catalogo.repository.InstrumentoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class DatosIniciales implements CommandLineRunner {

    private static final long DIA = TimeUnit.DAYS.toMillis(1);

    private final InstalacionRepository instalaciones;
    private final EquipoRepository equipos;
    private final InstrumentoRepository instrumentos;

    public DatosIniciales(InstalacionRepository instalaciones, EquipoRepository equipos, InstrumentoRepository instrumentos) {
        this.instalaciones = instalaciones;
        this.equipos = equipos;
        this.instrumentos = instrumentos;
    }

    @Override
    public void run(String... args) {
        if (instalaciones.count() > 0) {
            return;
        }
        Long lampa = instalaciones.save(new Instalacion(7L, "Planta Lampa", "Lampa", "Inducción obligatoria de 30 min")).id;
        Long clinico = instalaciones.save(new Instalacion(8L, "Edificio Clínico", "La Florida", "Cortes solo domingos 06:00–10:00")).id;
        Long tilTil = instalaciones.save(new Instalacion(9L, "Faena Til Til", "Til Til", "Sin señal en toda la faena")).id;

        equipos.save(new Equipo(lampa, "TR-01", TipoEquipo.TRANSFORMADOR, "Rhona", "RH-18733", "12 / 0,4 kV", null));
        equipos.save(new Equipo(lampa, "TR-02", TipoEquipo.TRANSFORMADOR, "Rhona", "RH-21904", "12 / 0,4 kV", null));
        equipos.save(new Equipo(clinico, "CEL-MT-03", TipoEquipo.CELDA_MT, "Ormazabal", "OR-77812", "12 kV, 630 A", 60.0));
        equipos.save(new Equipo(clinico, "RP-21", TipoEquipo.RELE_PROTECCION, "SEL", "SEL-50321", "—", null));
        equipos.save(new Equipo(tilTil, "INT-52-1", TipoEquipo.INTERRUPTOR, "Schneider", "SE-449021", "23 kV, 1.250 A", 45.0));
        equipos.save(new Equipo(tilTil, "CAB-MT-02", TipoEquipo.CABLE_POTENCIA, "Nexans", "NX-2201-B", "15 kV, 3×240 mm², 180 m", null));

        long hoy = System.currentTimeMillis();
        instrumentos.save(new Instrumento("Megóhmetro 5 kV", "Megger", "MIT525", "MIT-0045821", hoy - 210 * DIA, hoy + 155 * DIA));
        instrumentos.save(new Instrumento("Megóhmetro 10 kV", "Megger", "MIT1025", "MIT-0071233", hoy - 360 * DIA, hoy + 5 * DIA));
        instrumentos.save(new Instrumento("Medidor de relación (TTR)", "Megger", "TTR300", "TTR-30044", hoy - 120 * DIA, hoy + 245 * DIA));
        instrumentos.save(new Instrumento("Micro-ohmímetro", "Megger", "DLRO10X", "DLR-10882", hoy - 400 * DIA, hoy - 30 * DIA));
        instrumentos.save(new Instrumento("Equipo de inyección secundaria", "Omicron", "CMC 356", "CMC-HB411", hoy - 270 * DIA, hoy + 95 * DIA));
    }
}
