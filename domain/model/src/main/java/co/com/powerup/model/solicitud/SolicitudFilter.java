package co.com.powerup.model.solicitud;

import java.math.BigDecimal;

public record SolicitudFilter(
    String estado,
    String tipoPrestamo,
    BigDecimal minMonto,
    BigDecimal maxMonto
) {}