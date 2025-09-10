package co.com.powerup.usecase.solicitud;

import co.com.powerup.model.estado.gateways.EstadoRepository;
import co.com.powerup.model.solicitud.Solicitud;
import co.com.powerup.model.solicitud.gateways.SolicitudRepository;
import co.com.powerup.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.powerup.model.user.User;
import co.com.powerup.model.user.gateways.UserRepository;
import co.com.powerup.usecase.solicitud.validation.SolicitudValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final EstadoRepository estadoRepository;
    private final UserRepository userRepository;

    public Mono<Solicitud> createSolicitud(Solicitud solicitud, String token) {
        return SolicitudValidator.validate(solicitud)
                .flatMap(validSolicitud -> validarEmail(validSolicitud.getEmail(), token)
                        .flatMap(user -> tipoPrestamoRepository
                                .findById(validSolicitud.getTipoPrestamo().getIdTipoPrestamo())
                                .switchIfEmpty(Mono.error(new RuntimeException("El tipo de préstamo no existe")))
                                .map(tipoPrestamo -> validSolicitud.toBuilder().tipoPrestamo(tipoPrestamo).build())
                                .flatMap(solicitudConTipoPrestamo -> estadoRepository
                                        .findById(solicitudConTipoPrestamo.getEstado().getIdEstado())
                                        .switchIfEmpty(Mono.error(new RuntimeException("El estado no existe")))
                                        .map(estado -> solicitudConTipoPrestamo.toBuilder().estado(estado).build()))
                                .flatMap(solicitudFinal -> solicitudRepository.save(solicitudFinal))));
    }

    private Mono<User> validarEmail(String email, String token) {
        if (email == null || email.isBlank()) {
            return Mono.error(new RuntimeException("El email es obligatorio"));
        }
        return userRepository.findByEmail(email, token)
                .switchIfEmpty(Mono.error(new RuntimeException("El usuario con email " + email + " no existe")));
    }
}
