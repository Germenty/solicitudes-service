package co.com.powerup.api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.*;

public record RegisterSolicitudRequest(

        @NotBlank(message = "El id del prestamo es requerido") Long idPrestamo,

        @NotBlank(message = "El id del estado es requerido") Long idEstado,

        @NotBlank(message = "El id del tipo de prestamo es requerido") Long idTipoPrestamo,

        @NotBlank(message = "El monto es requerido") BigDecimal monto,

        @NotBlank(message = "El plazo es requerido") Integer plazo,

        @NotBlank(message = "El email es requerido") 
        @Email(message = "Email must be valid")
        String email

) {

}
