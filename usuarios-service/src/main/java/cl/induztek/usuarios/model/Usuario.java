package cl.induztek.usuarios.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull(message = "El rol es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public Rol rol;

    @NotBlank(message = "El RUT es obligatorio")
    @Size(max = 12)
    @Column(nullable = false, unique = true, length = 12)
    public String rut;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    public String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no es válido")
    @Column(nullable = false, unique = true, length = 150)
    public String correo;

    @Column(nullable = false, length = 30)
    public String telefono = "";

    @Column(nullable = false)
    public boolean activo = true;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "clave_hash", length = 200)
    public String clave;

    @Column(length = 100)
    public String giro;

    @Column(length = 150)
    public String contactoNombre;

    @Column(length = 100)
    public String contactoCargo;

    public Usuario() {
    }

    public Usuario(Rol rol, String rut, String nombre, String correo) {
        this.rol = rol;
        this.rut = rut;
        this.nombre = nombre;
        this.correo = correo;
    }
}
