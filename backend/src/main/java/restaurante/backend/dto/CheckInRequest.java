package restaurante.backend.dto;

public class CheckInRequest {
    
    private String numeroEmpleado;
    private String password;
    
    // Constructors
    public CheckInRequest() {}
    
    public CheckInRequest(String numeroEmpleado, String password) {
        this.numeroEmpleado = numeroEmpleado;
        this.password = password;
    }
    
    // Getters and Setters
    public String getNumeroEmpleado() {
        return numeroEmpleado;
    }
    
    public void setNumeroEmpleado(String numeroEmpleado) {
        this.numeroEmpleado = numeroEmpleado;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
