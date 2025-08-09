package restaurante.backend.entity;

public enum WorkerRole {
    COCINERO("Cocinero"),
    MESERO("Mesero"),
    CAJERO("Cajero"),
    AFANADOR("Afanador"),
    GERENTE("Gerente");
    
    private final String displayName;
    
    WorkerRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
