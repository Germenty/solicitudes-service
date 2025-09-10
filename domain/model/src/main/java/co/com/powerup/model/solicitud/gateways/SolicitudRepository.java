package co.com.powerup.model.solicitud.gateways;

import co.com.powerup.model.solicitud.Solicitud;
import reactor.core.publisher.Mono;

public interface SolicitudRepository {

    Mono<Solicitud> save(Solicitud user);

}
