package co.com.powerup.r2dbc.repository;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import co.com.powerup.r2dbc.entity.TipoPrestamoEntity;

public interface TipoPrestamoReactiveRepository extends ReactiveCrudRepository<TipoPrestamoEntity,Long>,ReactiveQueryByExampleExecutor<TipoPrestamoEntity> {
    
}
