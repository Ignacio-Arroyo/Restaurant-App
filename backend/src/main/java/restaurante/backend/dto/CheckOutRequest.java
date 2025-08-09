package restaurante.backend.dto;

public class CheckOutRequest {
    
    private String numeroEmpleado;
    private String password;
    private String notes;
    
    // Constructors
    public CheckOutRequest() {}
    
    public CheckOutRequest(String numeroEmpleado, String password, String notes) {
        this.numeroEmpleado = numeroEmpleado;
        this.password = password;
        this.notes = notes;
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
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
