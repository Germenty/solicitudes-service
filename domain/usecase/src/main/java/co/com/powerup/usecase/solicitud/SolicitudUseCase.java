package co.com.powerup.usecase.solicitud;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import co.com.powerup.model.estado.gateways.EstadoRepository;
import co.com.powerup.model.jwt.gateways.JWTRepository;
import co.com.powerup.model.solicitud.Solicitud;
import co.com.powerup.model.solicitud.SolicitudFilter;
import co.com.powerup.model.solicitud.SolicitudPageResponse;
import co.com.powerup.model.solicitud.SolicitudRevisionResponse;
import co.com.powerup.model.solicitud.gateways.SolicitudRepository;
import co.com.powerup.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.powerup.model.user.User;
import co.com.powerup.model.user.gateways.UserRepository;
import co.com.powerup.usecase.solicitud.validation.SolicitudValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final EstadoRepository estadoRepository;
    private final UserRepository userRepository;
    private final JWTRepository jwtRepository;

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

    public Mono<SolicitudPageResponse> listarSolicitudesFiltradas(String token, SolicitudFilter filter, int page,
            int size) {
        return solicitudRepository.findFiltradas(
                filter.tipoPrestamo(), filter.minMonto(), filter.maxMonto(), page, size)
                .collectList()
                .zipWith(solicitudRepository.countFiltradas(
                        filter.tipoPrestamo(), filter.minMonto(), filter.maxMonto()))
                .flatMap(tuple -> {
                    List<Solicitud> solicitudes = tuple.getT1();
                    long total = tuple.getT2();

                    return Flux.fromIterable(solicitudes)
                            .flatMap(s -> userRepository.findByEmail(s.getEmail(), token)
                                    .map(user -> SolicitudRevisionResponse.builder()
                                            .monto(s.getMonto())
                                            .plazo(s.getPlazo())
                                            .email(s.getEmail())
                                            .nombreUsuario(user.getName())
                                            .tipoPrestamo(s.getTipoPrestamo().getNombre())
                                            .tasaInteres(s.getTipoPrestamo().getTasaInteres())
                                            .estadoSolicitud(s.getEstado().getNombre())
                                            .salarioBase(user.getBaseSalary())
                                            .montoMensualSolicitud(calcularMontoMensual(s.getMonto(), s.getPlazo()))
                                            .build())
                                    .onErrorResume(ex -> Mono.empty()))
                            .collectList()
                            .map(items -> SolicitudPageResponse.builder()
                                    .page(page)
                                    .size(size)
                                    .total(total)
                                    .items(items)
                                    .build());
                });
    }

    private BigDecimal calcularMontoMensual(BigDecimal monto, Integer plazo) {
        return monto.divide(BigDecimal.valueOf(plazo), 2, RoundingMode.HALF_UP);
    }
}