package restaurante.backend.dto;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    // Campos adicionales para workers
    private String numeroEmpleado;
    private Boolean isWorker;

    // Constructors
    public AuthResponse() {}

    public AuthResponse(String token, String email, String firstName, String lastName, String role) {
        this.token = token;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.isWorker = false;
    }

    // Constructor específico para workers
    public AuthResponse(String token, String email, String firstName, String lastName, String role, String numeroEmpleado) {
        this.token = token;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.numeroEmpleado = numeroEmpleado;
        this.isWorker = true;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNumeroEmpleado() {
        return numeroEmpleado;
    }

    public void setNumeroEmpleado(String numeroEmpleado) {
        this.numeroEmpleado = numeroEmpleado;
    }

    public Boolean getIsWorker() {
        return isWorker;
    }

    public void setIsWorker(Boolean isWorker) {
        this.isWorker = isWorker;
    }
}
