package co.com.powerup.usecase.solicitud;

import co.com.powerup.model.estado.gateways.EstadoRepository;
import co.com.powerup.model.solicitud.Solicitud;
import co.com.powerup.model.solicitud.gateways.SolicitudRepository;
import co.com.powerup.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.powerup.usecase.solicitud.validation.SolicitudValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final EstadoRepository estadoRepository;

    public Mono<Solicitud> createSolicitud(Solicitud solicitud) {
        return SolicitudValidator.validate(solicitud)
                .switchIfEmpty(
                        tipoPrestamoRepository
                                .findById(solicitud.getTipoPrestamo()
                                        .getIdTipoPrestamo())
                                .switchIfEmpty(Mono.error(new RuntimeException(
                                        "El tipo de prestamo no existe")))
                                .map(tipoPrestamo -> solicitud.toBuilder()
                                        .tipoPrestamo(tipoPrestamo).build())
                                .flatMap(solicitudConTipoPrestamo -> estadoRepository
                                        .findById(solicitudConTipoPrestamo
                                                .getEstado()
                                                .getIdEstado())
                                        .switchIfEmpty(Mono.error(
                                                new RuntimeException(
                                                        "El estado no existe")))
                                        .map(estado -> solicitudConTipoPrestamo
                                                .toBuilder()
                                                .estado(estado)
                                                .build()))
                                .flatMap(solicitudConTipoPrestamo -> solicitudRepository
                                        .save(solicitudConTipoPrestamo)));
    }

}
