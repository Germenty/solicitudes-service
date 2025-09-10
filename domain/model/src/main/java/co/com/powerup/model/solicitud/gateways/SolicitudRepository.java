package co.com.powerup.model.solicitud.gateways;

import java.math.BigDecimal;
import java.util.List;

import co.com.powerup.model.solicitud.Solicitud;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SolicitudRepository {

    Mono<Solicitud> save(Solicitud solicitud);

    Flux<Solicitud> findByEstados(List<String> estados);

    Flux<Solicitud> findFiltradas(String tipoPrestamo, BigDecimal minMonto, BigDecimal maxMonto, int page, int size);

    Mono<Long> countFiltradas(String tipoPrestamo, BigDecimal minMonto, BigDecimal maxMonto);
    
}