package co.com.powerup.model.estado;

public enum EstadoNombre {
    PENDING("Pendiente de revisión"),
    APPROVED("Solicitud aprobada"),
    REJECTED("Solicitud rechazada"),
    IN_PROGRESS("Solicitud en proceso de gestión"),
    COMPLETED("Solicitud finalizada con éxito"),
    CANCELLED("Solicitud cancelada por el usuario o el sistema"),
    EXPIRED("Solicitud expirada por tiempo límite");

    private final String descripcion;

    EstadoNombre(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}