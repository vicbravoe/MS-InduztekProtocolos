package cl.induztek.usuarios.config;

import cl.induztek.usuarios.model.Rol;
import cl.induztek.usuarios.model.Usuario;
import cl.induztek.usuarios.repository.UsuarioRepository;
import cl.induztek.usuarios.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatosIniciales implements CommandLineRunner {

    public static final String CLAVE_INICIAL = "Induztek2026";

    private final UsuarioRepository repositorio;
    private final UsuarioService servicio;

    public DatosIniciales(UsuarioRepository repositorio, UsuarioService servicio) {
        this.repositorio = repositorio;
        this.servicio = servicio;
    }

    @Override
    public void run(String... args) {
        if (repositorio.count() == 0) {
            sembrar();
        }
        servicio.asignarClavesFaltantes(CLAVE_INICIAL);
    }

    private void sembrar() {
        personal(Rol.TECNICO, "17.284.519-3", "Matías Godoy Herrera", "mgodoy@induztek.ejemplo");
        personal(Rol.TECNICO, "19.412.087-7", "Javiera Tapia Muñoz", "jtapia@induztek.ejemplo");
        personal(Rol.TECNICO, "26.104.583-4", "Carlos Rondón Pérez", "crondon@induztek.ejemplo");
        personal(Rol.INGENIERO, "15.906.334-8", "Andrea Contreras Silva", "acontreras@induztek.ejemplo");
        personal(Rol.INGENIERO, "16.553.270-8", "Sebastián Morales Gutiérrez", "smorales@induztek.ejemplo");
        personal(Rol.JEFE_TECNICO, "13.871.905-7", "Ricardo Espinoza Lagos", "respinoza@induztek.ejemplo");
        cliente("76.482.915-8", "Envasadora del Pacífico S.A.", "mfuentes@envpacifico.ejemplo", "Agroindustria", "Marcela Fuentes Rojas", "Jefa de Mantenimiento");
        cliente("76.903.218-5", "Clínica Los Aromos Ltda.", "cmendez@losaromos.ejemplo", "Salud", "Carolina Méndez Pizarro", "Supervisor Eléctrico");
        cliente("77.105.362-9", "Minera Cerro Alto SpA", "pnavarro@cerroalto.ejemplo", "Minería", "Patricio Navarro Soto", "Jefe de Mantenimiento Eléctrico");
    }

    private void personal(Rol rol, String rut, String nombre, String correo) {
        repositorio.save(new Usuario(rol, rut, nombre, correo));
    }

    private void cliente(String rut, String razon, String correo, String giro, String contacto, String cargo) {
        Usuario u = new Usuario(Rol.CLIENTE, rut, razon, correo);
        u.giro = giro;
        u.contactoNombre = contacto;
        u.contactoCargo = cargo;
        repositorio.save(u);
    }
}
