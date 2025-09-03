package co.com.powerup.model.solicitud;
import java.math.BigDecimal;

import co.com.powerup.model.estado.Estado;
import co.com.powerup.model.tipoprestamo.TipoPrestamo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Solicitud {
    private BigDecimal monto;
    private Integer plazo;
    private String email;
    private Estado estado;
    private TipoPrestamo tipoPrestamo;
}
