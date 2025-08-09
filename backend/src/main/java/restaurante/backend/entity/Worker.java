package restaurante.backend.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@Entity
@Table(name = "workers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Worker {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String apellido;
    
    @Column(nullable = false)
    private String direccion;
    
    @Column(name = "numero_telefono", nullable = false)
    private String numeroTelefono;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkerRole rol;
    
    @Column(name = "numero_empleado", unique = true, nullable = false, length = 6)
    private String numeroEmpleado;
    
    @Column(nullable = false)
    private String nacionalidad;
    
    @Column(nullable = false)
    private String password;
    
    @Column(name = "fecha_contratacion")
    private LocalDateTime fechaContratacion;
    
    @Column(nullable = false)
    private boolean activo = true;
    
    // Constructors
    public Worker() {
        this.fechaContratacion = LocalDateTime.now();
    }
    
    public Worker(String nombre, String apellido, String direccion, String numeroTelefono, 
                  String email, WorkerRole rol, String numeroEmpleado, String nacionalidad, String password) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.direccion = direccion;
        this.numeroTelefono = numeroTelefono;
        this.email = email;
        this.rol = rol;
        this.numeroEmpleado = numeroEmpleado;
        this.nacionalidad = nacionalidad;
        this.password = password;
        this.fechaContratacion = LocalDateTime.now();
        this.activo = true;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getApellido() {
        return apellido;
    }
    
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    
    public String getDireccion() {
        return direccion;
    }
    
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    public String getNumeroTelefono() {
        return numeroTelefono;
    }
    
    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public WorkerRole getRol() {
        return rol;
    }
    
    public void setRol(WorkerRole rol) {
        this.rol = rol;
    }
    
    public String getNumeroEmpleado() {
        return numeroEmpleado;
    }
    
    public void setNumeroEmpleado(String numeroEmpleado) {
        this.numeroEmpleado = numeroEmpleado;
    }
    
    public String getNacionalidad() {
        return nacionalidad;
    }
    
    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public LocalDateTime getFechaContratacion() {
        return fechaContratacion;
    }
    
    public void setFechaContratacion(LocalDateTime fechaContratacion) {
        this.fechaContratacion = fechaContratacion;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}
