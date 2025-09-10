package co.com.powerup.r2dbc.repository;

import java.util.List;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import co.com.powerup.r2dbc.entity.SolicitudEntity;
import reactor.core.publisher.Flux;

public interface SolicitudReactiveRepository
        extends ReactiveCrudRepository<SolicitudEntity, Long>, ReactiveQueryByExampleExecutor<SolicitudEntity> {
    Flux<SolicitudEntity> findByIdEstadoIn(List<Long> idEstados);
}
