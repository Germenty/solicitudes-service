package co.com.powerup.model.solicitud.gateways;

import co.com.powerup.model.solicitud.Solicitud;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SolicitudRepository {

    Mono<Solicitud> save(Solicitud user);

    Mono<Solicitud> findByEmail(String email);

    Flux<Solicitud> findAll();

    Mono<Solicitud> delete(Solicitud user);

}
