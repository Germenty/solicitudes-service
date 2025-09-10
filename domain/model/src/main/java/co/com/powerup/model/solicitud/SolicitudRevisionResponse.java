package co.com.powerup.model.solicitud;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudRevisionResponse {
    private BigDecimal monto;
    private Integer plazo;
    private String email;
    private String nombreUsuario;
    private String tipoPrestamo;
    private BigDecimal tasaInteres;
    private String estadoSolicitud;
    private BigDecimal salarioBase;
    private BigDecimal montoMensualSolicitud;
}