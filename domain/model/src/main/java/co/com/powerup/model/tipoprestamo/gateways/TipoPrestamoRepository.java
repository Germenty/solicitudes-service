package co.com.powerup.model.tipoprestamo.gateways;

import co.com.powerup.model.tipoprestamo.TipoPrestamo;
import reactor.core.publisher.Mono;

public interface TipoPrestamoRepository {

    Mono<TipoPrestamo> findById(String id);

}
