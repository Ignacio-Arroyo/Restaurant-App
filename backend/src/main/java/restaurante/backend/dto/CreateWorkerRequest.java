package restaurante.backend.dto;

import restaurante.backend.entity.WorkerRole;

public class CreateWorkerRequest {
    
    private String nombre;
    private String apellido;
    private String direccion;
    private String numeroTelefono;
    private String email;
    private WorkerRole rol;
    private String nacionalidad;
    private String password;
    
    // Constructors
    public CreateWorkerRequest() {}
    
    public CreateWorkerRequest(String nombre, String apellido, String direccion, 
                              String numeroTelefono, String email, WorkerRole rol, 
                              String nacionalidad, String password) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.direccion = direccion;
        this.numeroTelefono = numeroTelefono;
        this.email = email;
        this.rol = rol;
        this.nacionalidad = nacionalidad;
        this.password = password;
    }
    
    // Getters and Setters
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
}
