package co.com.powerup.model.estado.gateways;

import co.com.powerup.model.estado.Estado;
import reactor.core.publisher.Mono;

public interface EstadoRepository {

    Mono<Estado> findById(String id);

}
